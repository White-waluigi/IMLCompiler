package imlcompiler.Codegenerator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

//import com.sun.org.apache.bcel.internal.classfile.CodeException;

import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions.IInstr;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions.UncondJump;
import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlComposite;
import imlcompiler.Parser.ImlItem;
import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.Token.Attribute;
import imlcompiler.Scanner.Token.EnumAttribute;
import imlcompiler.Scanner.Token.IdentAttribute;
import imlcompiler.Scanner.Token.OtherAttribute;
import imlcompiler.Scanner.Token.Terminal;
import imlcompiler.Symboltable.Symbol;
import imlcompiler.Symboltable.SymbolMap;

public class CodeGenerator {

	CodeArray codeArray;
	int SIZE = 12;
	ArrayList<IInstructions.IInstr> ar;
	private SymbolMap currentst;
	private SymbolMap globalst;
	public class ProcMeta{
		public ProcMeta(int a, Symbol[] p) {
			addr=a;
			params=p;
		}
		public int addr;
		public Symbol[] params;
	}
	Map<String, ProcMeta> ProcAddr;
	long freeProcSpace = 0;
	private int curPrStart = 1;

	public CodeGenerator(ImlComponent ast, SymbolMap symbolTables) throws ICodeArray.CodeTooSmallError {

		ProcAddr = new HashMap<>();

		this.globalst = symbolTables;

		ar = new ArrayList<>();

		// PLACE HOLDER WILL BE DELETED
		ar.add(new IInstructions.Stop());

		if (ast != null)
			genRoot(ast);

		ar.remove(0);
		this.codeArray = new CodeArray(ar.size() + 1);
		int i = 0;
		codeArray.put(i++, new IInstructions.UncondJump(curPrStart));
		for (IInstr a : ar) {
			codeArray.put(i++, a);
		}

	}

	public CodeArray getCode() {
		return codeArray;
	}

	private void genRoot(ImlComponent ast) {
		genAllProcs(ast);

		currentst=globalst;
		reserveSpaceGlobal(ast);

		genCpsCmd(ast.getChild("cpsCmd"));

		ar.add(new IInstructions.Stop());
	}

	private void genCpsCmd(ImlComponent child) {
		for (int i = 0; i < child.size(); i++) {
			ImlComposite a = (ImlComposite) child.getChild(i);

			switch (a.getToken().getTerminal()) {
			case BECOMES:
				genAssignment(a);
				break;
			case DEBUGOUT:
				genDebugOut(a);
				break;
			case DEBUGIN:
				genDebugIn(a);
				break;
			case CALL:
				genCall(a);
				break;
			case IF:
				genIf(a);
				break;
			case WHILE:
				genWhile(a);
				break;
			default:
				throw new CodeGenerationException("terminal " + a.getToken().getTerminal() + " not valid command");

			}
		}
	}

	private void genWhile(ImlComposite a) {
		int whilearpos=ar.size();
		genEvalExpression(a.getChild(1));
		
		int skiparpos=ar.size();
		//PLACEHOLDER; DO NOT DELETE!
		ar.add(new IInstructions.Stop());
		genCpsCmd(a.getChild("cpsCmd"));
		
		
		ar.add(new UncondJump(whilearpos));

		ar.set(skiparpos, new IInstructions.CondJump(ar.size()));
		
	}

	private void genIf(ImlComposite a) {

		
		
		genEvalExpression(a.getChild(1));
		
		int jumparpos=ar.size();
		
		//PLACEHOLDER; DO NOT DELETE!
		ar.add(new IInstructions.Stop());
		genCpsCmd(a.getChild(3));
		
		int skiparpos=ar.size();
		
		//PLACEHOLDER; DO NOT DELETE!
		ar.add(new IInstructions.Stop());
		ar.set(jumparpos, new IInstructions.CondJump(ar.size()));
		
		
		genCpsCmd(a.getChild(5));
		ar.set(skiparpos, new IInstructions.UncondJump(ar.size()));

		
		
	}

	private void genCall(ImlComposite a) {
		String name = ((IdentAttribute) a.getChild(Terminal.IDENT).getToken().getAttribute()).value;

		
		ProcMeta as = this.ProcAddr.get(name);
		
		ImlComponent b = a.getChild("exprList");
		
		int paramsize=0;
		int g=0;
		for (int i = 0; i < b.size(); i++) {
			if(as.params[g].isGlobal) {
				i--;
			}else if(as.params[g].isRef) {
				genMakeRef(b.getChild(i));
				paramsize++;
			}else {
				genEvalExpression(b.getChild(i));
				paramsize += as.params[g].tupSize == -1 ? 1 : as.params[g].tupSize;
			}
			
			g++;
		}
		
		System.out.println(this.ProcAddr.entrySet());
		
		ar.add(new IInstructions.Call(this.ProcAddr.get(name).addr));
		
		//Clear stack paramters
		ar.add(new IInstructions.LoadImInt(0));
		for(int i=0;i<paramsize;i++) {
			ar.add(new IInstructions.MultInt());
		}
		ar.add(new IInstructions.AddInt());
	}

	private void genMakeRef(ImlComponent child) {
		Symbol x=getSymbole(child);
		if(x.isGlobal) {
			ar.add(new IInstructions.LoadAddrRel((x).location));
		}else{
			ar.add(new IInstructions.LoadAddrRel(getStackLocation(x)));
			if(x.isRef) {
				ar.add(new IInstructions.Deref());
			}
		}
		
	}

	private void genDebugOut(ImlComposite a) {
		genEvalExpression(a.getChild(0));
		String output="";
		try {
			output=getSymbole(a.getChild(0)).name;
		}catch(Throwable x) {}
		ar.add(new IInstructions.OutputInt(output));

	}

	private void genDebugIn(ImlComposite a){
		Symbol symbol = getSymbole(a.getChild(0));
		if (symbol.isGlobal) {
			ar.add(new IInstructions.LoadImInt(symbol.location));
		}
		else {
			throw new CodeGenerationException("not yet implemented");
		}

		String identifier = "";
		try {
			identifier=getSymbole(a.getChild(0)).name;
		}catch(Throwable x) {}
		ar.add(new IInstructions.InputInt(identifier));
	}

	private void genAssignment(ImlComposite a) {
		Symbol symb = getSymbole(a.getChild(0));

		
		if(symb.tupSize==-1) {
			genStackLocation(symb);
			genEvalExpression(a.getChild(1));
			ar.add(new IInstructions.Store());
		}else {
			ImlComposite tail=(ImlComposite) a.getChild(1).getChild(1);

			if(symb.tupSize!=tail.size()) {
				throw new CodeGenerationException("tupel declaration doesn't match tupel size");
			}
			for (int i = 0; i < symb.tupSize; i++) {
				
				if( (a.getChild(1).getToken()==null||a.getChild(1).getToken().getTerminal()!=Terminal.TUP)  && (a.getChild(1).getToken().getAttribute()==null || getSymbole(a.getChild(1)).tupSize!=-1  ))  {
					throw new CodeGenerationException("Tupel can be only assigned to new or other tuple, not "+a.getChild(1).getToken());
				}
				genStackLocation(symb,i);

				genEvalExpression(tail.getChild(i));
				ar.add(new IInstructions.Store());
			}
		}
	}
	private void genStackLocation(Symbol symbole) {
		genStackLocation(symbole,0);
	}
	private void genStackLocation(Symbol symbole, int i) {
		if(global(symbole)) {
			ar.add(new IInstructions.LoadImInt(symbole.location+i));
		}else {
			int offset=0;
			if(!symbole.isRef)
				offset=i;
			
			int x=getStackLocation(symbole);
			ar.add(new IInstructions.LoadAddrRel(x+i));
			if(symbole.isRef) {
				ar.add(new IInstructions.Deref());
				if(offset!=0) {
					ar.add(new IInstructions.LoadImInt(offset));
					ar.add(new IInstructions.AddInt());
				}
			}
		}
	}
	private boolean global(Symbol e) {
		return e.isGlobal||currentst==globalst;
	}

	private void genEvalExpression(ImlComponent imlComponent) {
		if (imlComponent instanceof ImlComposite &&imlComponent.getToken()==null&&  imlComponent.getChild(1).getToken().getTerminal() == Terminal.LBRACK) {
			genIndexTup((ImlComposite) imlComponent);
		}else if (imlComponent.getToken().getTerminal() == Terminal.IDENT) {
			genEvalIdent((ImlItem) imlComponent,0);
		} else if (imlComponent.getToken().getTerminal() == Terminal.LITERAL) {
			genEvalLiteral((ImlItem) imlComponent);
		} else if (imlComponent.getToken().isOperator()) {
			genOperation((ImlComposite) imlComponent);
		}else {
			throw new CodeGenerationException("Expression "+imlComponent+" cannot be evaluated. Line: "+ar.size());
		}

	}

	private void genIndexTup(ImlComposite imlComponent) {
		Token index = ((ImlComposite)imlComponent.getChild(1)).getChild(Terminal.LITERAL).getToken();
		genEvalIdent((ImlItem) imlComponent.getChild(0),(     (Token.IntAttribute)index.getAttribute()).value    );
	}
	
	private void genEvalIdent(ImlItem a,int offset) {
		Symbol o = getSymbole(a);
		genEvalSymbol(o,offset);
	}
	private void genEvalSymbol(Symbol o) {
		genEvalSymbol(o,0);
	}
	private void genEvalSymbol(Symbol o,int offset) {
		if (o.isGlobal||currentst==globalst) {
			ar.add(new IInstructions.LoadImInt(o.location+offset));
			ar.add(new IInstructions.Deref());

		} else if (o.isRef) {
			int l = getStackLocation(o);
			ar.add(new IInstructions.LoadAddrRel(l));
			ar.add(new IInstructions.Deref());
			if(offset!=0) {
				ar.add(new IInstructions.LoadImInt(offset));
				ar.add(new IInstructions.AddInt());
			}
			ar.add(new IInstructions.Deref());
		} else {
			int l = getStackLocation(o);
			ar.add(new IInstructions.LoadAddrRel(l+offset));
			ar.add(new IInstructions.Deref());

		}

	}

	private void genEvalLiteral(ImlItem a) {
		ar.add((new IInstructions.LoadImInt(((Token.IntAttribute) a.getToken().getAttribute()).value)));
	}

	private void genOperation(ImlComposite a) {
		genEvalExpression(a.getChild(0));
		for (int i = 1; i < a.size(); i++) {
			genEvalExpression(a.getChild(i));
			genOperator(a);
		}

	}

	private void genOperator(ImlComposite a) {
		if (a.getToken().getTerminal() == Terminal.ADDOPR) {
			if (a.getToken().has(EnumAttribute.PLUS))
				ar.add(new IInstructions.AddInt());
			else if (a.getToken().has(EnumAttribute.MINUS))
				ar.add(new IInstructions.SubInt());
			else
				throw new CodeGenerationException("unexpected operator " + a);
		} else if (a.getToken().getTerminal() == Terminal.MULTOPR) {

			if (a.getToken().has(EnumAttribute.DIV_E))
				ar.add(new IInstructions.DivTruncInt());
			else if (a.getToken().has(EnumAttribute.TIMES))
				ar.add(new IInstructions.MultInt());
			else if (a.getToken().has(EnumAttribute.MOD_E))
				ar.add(new IInstructions.ModTruncInt());
			else
				throw new CodeGenerationException("unexpected operator " + a);
		} else if (a.getToken().getTerminal() == Terminal.RELOPR) {
			if (a.getToken().has(EnumAttribute.LE))
				ar.add(new IInstructions.LeInt());
			else if (a.getToken().has(EnumAttribute.LT))
				ar.add(new IInstructions.LtInt());
			else if (a.getToken().has(EnumAttribute.EQ))
				ar.add(new IInstructions.EqInt());
			else if (a.getToken().has(EnumAttribute.GE))
				ar.add(new IInstructions.GeInt());
			else if (a.getToken().has(EnumAttribute.GT))
				ar.add(new IInstructions.GtInt());
			else if (a.getToken().has(EnumAttribute.NE))
				ar.add(new IInstructions.NeInt());
			else
				throw new CodeGenerationException("unexpected operator " + a.getToken());
		} else if (a.getToken().getTerminal() == Terminal.BOOLOPR) {
			if (a.getToken().has(EnumAttribute.AND)) {
				ar.add(new IInstructions.AddInt());
				ar.add(new IInstructions.LoadImInt(2));	
				ar.add(new IInstructions.EqInt());
			}
			else if (a.getToken().has(EnumAttribute.OR)) {
				ar.add(new IInstructions.AddInt());
				ar.add(new IInstructions.LoadImInt(0));	
				ar.add(new IInstructions.NeInt());
			}
			else
				throw new CodeGenerationException("unexpected operator " + a);
		} else {
			throw new CodeGenerationException("unexpected operator " + a.getToken().getTerminal());
		}
	}

	private void reserveSpaceGlobal(ImlComponent ast2) {
		//todo parameter übernehmen
		//ar.add(new IInstructions.AllocBlock(currentst.getSize()));
		for (int i = 0; i < currentst.getSize(); i++) {
//			if(currentst.get(i).tupSize==-1) {
				ar.add(new IInstructions.LoadImInt(0));
//			}else {
//				for (int j = 0; j < currentst.get(i).tupSize; j++) {
//					 
//					ar.add(new IInstructions.LoadImInt(0));
//
//				}
//			}
		}
	}

	private void genAllProcs(ImlComponent ast2) {
		for (int i = 0; i < ast2.size(); i++) {
			ImlComponent a = ast2.getChild(i);
			if(a.getToken()==null) {
				continue;
			}
			
			if(a.getToken().getTerminal()==Terminal.PROC) {
				genProc((ImlComposite) a);
			}
		}
	}

	private void genProc(ImlComposite a) {
		String name = ((IdentAttribute) a.getChild(Terminal.IDENT).getToken().getAttribute()).value;
		currentst = globalst.findTable(name);

		Symbol[] p=new Symbol[currentst.getNum()];
		
		for(int i=0;i<currentst.getNum();i++) {
			p[i]=currentst.get(i);
		}
		this.ProcAddr.put(name,new ProcMeta(ar.size(),p));

		genCpsCmd(a.getChild("cpsCmd"));
		ar.add(new IInstructions.Return(0));
		
		curPrStart = ar.size();
	}

	private Symbol getSymbole(ImlComponent x) {
		String name = ((IdentAttribute) x.getToken().getAttribute()).value;

		return this.currentst.get(name);
	}

	private int getStackLocation(Symbol a) {
		if (a.isGlobal||currentst==globalst) {
			throw new CodeGenerationException("Cannot get stack position from a global variable " + a);
		}
		return a.location - currentst.getSize();

	}
}