package imlcompiler.Codegenerator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions.IInstr;
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

	Map<String, Integer> ProcAddr;
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
			case CALL:
				genCall(a);
				break;
			default:
				throw new CodeGenerationException("terminal " + a.getToken().getTerminal() + " not valid command");

			}
		}
	}

	private void genCall(ImlComposite a) {
		ImlComponent b = a.getChild("exprList");
		for (int i = 0; i < b.size(); i++) {
			genEvalExpression(b.getChild(i));
		}

		String name = ((IdentAttribute) a.getChild(Terminal.IDENT).getToken().getAttribute()).value;

		ar.add(new IInstructions.Call(this.ProcAddr.get(name)));
	}

	private void genDebugOut(ImlComposite a) {
		genEvalExpression(a.getChild(0));
		ar.add(new IInstructions.OutputInt(""));

	}

	private void genAssignment(ImlComposite a) {
		genStackLocation(getSymbole(a.getChild(0)));
		
		genEvalExpression(a.getChild(1));
        
		ar.add(new IInstructions.Store());
	}

	private void genStackLocation(Symbol symbole) {
		if(global(symbole)) {
			ar.add(new IInstructions.LoadImInt(symbole.location));
		}else {
			int x=getStackLocation(symbole);
			ar.add(new IInstructions.LoadAddrRel(x));
			if(symbole.isRef) {
				ar.add(new IInstructions.Deref());
			}
		}
	}
	private boolean global(Symbol e) {
		return e.isGlobal||currentst==globalst;
	}

	private void genEvalExpression(ImlComponent imlComponent) {
		if (imlComponent.getToken().getTerminal() == Terminal.IDENT) {
			genEvalIdent((ImlItem) imlComponent);
		} else if (imlComponent.getToken().getTerminal() == Terminal.LITERAL) {
			genEvalLiteral((ImlItem) imlComponent);
		} else if (imlComponent.getToken().isOperator()) {
			genOperation((ImlComposite) imlComponent);
		}else {
			throw new CodeGenerationException("Expression "+imlComponent+" cannot be evaluated. Line: "+ar.size());
		}

	}

	private void genEvalIdent(ImlItem a) {
		Symbol o = getSymbole(a);
		genEvalSymbol(o);
	}

	private void genEvalSymbol(Symbol o) {
		if (o.isGlobal||currentst==globalst) {
			ar.add(new IInstructions.LoadImInt(o.location));
			ar.add(new IInstructions.Deref());

		} else if (o.isRef) {
			int l = getStackLocation(o);
			ar.add(new IInstructions.LoadAddrRel(l));
			ar.add(new IInstructions.Deref());
			ar.add(new IInstructions.Deref());
		} else {
			int l = getStackLocation(o);
			ar.add(new IInstructions.LoadAddrRel(l));
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
			else
				throw new CodeGenerationException("unexpected operator " + a);
		} else {
			throw new CodeGenerationException("unexpected operator " + a);
		}
	}

	private void reserveSpaceGlobal(ImlComponent ast2) {
		ar.add(new IInstructions.AllocBlock(currentst.getSize()));
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

		this.ProcAddr.put(name, ar.size());

		genCpsCmd(a.getChild("cpsCmd"));

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