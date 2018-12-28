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
import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.Token.EnumAttribute;
import imlcompiler.Scanner.Token.IdentAttribute;
import imlcompiler.Scanner.Token.OtherAttribute;
import imlcompiler.Scanner.Token.Terminal;
import imlcompiler.Symboltable.SymbolMap;

public class Codegenerator {

    ImlComponent ast;
    CodeArray codeArray;
    int SIZE = 12;
    ArrayList<IInstructions.IInstr> ar;
    private SymbolMap currentst;
    private SymbolMap globalst;

    Map<String, Long> ProcAddr;
    long freeProcSpace=0;
    public Codegenerator(ImlComponent ast, SymbolMap symbolTables) throws ICodeArray.CodeTooSmallError {
        
    	ProcAddr=new HashMap<>();
    	
    	this.ast = ast;
        this.globalst = symbolTables;

        ar = new ArrayList<>();

        if (ast != null)
            genRoot(ast);

        else {

            int r = 0;
            ar.add(new IInstructions.LoadImInt(3));
            r++;
            ar.add(new IInstructions.Call(3));
            r++;
            ar.add(new IInstructions.Stop());
            r++;
            ar.add(new IInstructions.AllocBlock(99));
            r++;
            ar.add(new IInstructions.LoadImInt(0));
            r++;
            ar.add(new IInstructions.LoadImInt(0));
            r++;
            ar.add(new IInstructions.Deref());
            r++;
            ar.add(new IInstructions.LoadImInt(1));
            r++;
            ar.add(new IInstructions.SubInt());
            r++;
            ar.add(new IInstructions.Store());
            r++;
            ar.add(new IInstructions.LoadImInt(0));
            r++;
            ar.add(new IInstructions.Deref());
            r++;
            ar.add(new IInstructions.CondJump(r + 2));
            r++;
            ar.add(new IInstructions.Call(3));
            r++;
            ar.add(new IInstructions.Return(0));
            r++;

        }

        this.codeArray = new CodeArray(ar.size());
        int i = 0;
        for (IInstr a : ar) {
            codeArray.put(i++, a);
        }

    }

    private void genRoot(ImlComponent ast) {
    	currentst=globalst;
    	genCpsCmd(ast);
        ar.add(new IInstructions.Stop());
        
        
        for(int i=0;i<ast.size();i++) {
        	if(ast.getChild(i).getToken()!=null&&ast.getChild(i).getToken().getTerminal()==Terminal.PROC) {
        		genProc((ImlComposite) ast.getChild(i));
        	}
        	
        }

    }
    private void genCpsCmd(ImlComponent ast) {
    	
    	
    	
        reserveSymbolTable();
        ImlComposite m = (ImlComposite) ast.getChild("cpsCmd");
        ImlComposite g = null;
        for (int i = 0; i < m.size(); i++) {
            g = (ImlComposite) m.getChild(i);
            if (g.getToken().getTerminal() == Terminal.BECOMES) {
                genAssign(g);
            }
            else if (g.getToken().getTerminal() == Terminal.DEBUGOUT) {
                genDebugout(g);
            }
        }
    }

    private void genDebugout(ImlComposite g) {
        genExpr(g.getChild(0));
        ar.add(new IInstructions.OutputInt("Out:"));
	}

    
	private void reserveSymbolTable() {
        ar.add(new IInstructions.AllocBlock(currentst.getSize()));
    }

    private void genAssign(ImlComposite g) {
        ar.add(new IInstructions.LoadAddrRel(
                currentst.get(((IdentAttribute) g.getChild(0).getToken().getAttribute()).value).location));
        genExpr(g.getChild(1));

        ar.add(new IInstructions.Store());
    }

    private void genExpr(ImlComponent child) {
        if (child instanceof ImlComposite)
            genOperation((ImlComposite) child);
        else if (child.getToken().getTerminal() == Terminal.LITERAL)
            genLiteral(child);
        else if (child.getToken().getTerminal() == Terminal.IDENT)
            genIdentLoad(child);
        else
            throw new CodeGenerationException("unexpected expression" + child);
    }

    private void genIdentLoad(ImlComponent child) {
        ar.add(new IInstructions.LoadAddrRel(
                currentst.get(((Token.IdentAttribute) child.getToken().getAttribute()).value).location));
        ar.add(new IInstructions.Deref());
    }

    private void genLiteral(ImlComponent child) {
        int val = ((Token.IntAttribute) child.getToken().getAttribute()).value;
        ar.add(new IInstructions.LoadImInt(val));

    }

    private void genOperation(ImlComposite child) {

        IInstructions.IInstr in = null;

        genExpr(child.getChild(0));
        for (int i = 1; i < child.size(); i++) {
            genExpr(child.getChild(i));
            genOperator(child);

        }

    }

    public void genOperator(ImlComposite child) {
        if (child.getToken().getTerminal() == Terminal.ADDOPR) {
            if (child.getToken().has(EnumAttribute.PLUS))
                ar.add(new IInstructions.AddInt());
            else if (child.getToken().has(EnumAttribute.MINUS))
                ar.add(new IInstructions.SubInt());
            else
                throw new CodeGenerationException("unexpected operator " + child);
        } else if (child.getToken().getTerminal() == Terminal.MULTOPR) {

            if (child.getToken().has(EnumAttribute.DIV_E))
                ar.add(new IInstructions.DivTruncInt());
            else if (child.getToken().has(EnumAttribute.TIMES))
                ar.add(new IInstructions.MultInt());
            else
                throw new CodeGenerationException("unexpected operator " + child);

        } else {
            throw new CodeGenerationException("unexpected operator " + child);
        }
    }

    
    public void genProc(ImlComposite c) {
    	String name=((IdentAttribute)c.getChild(0).getToken().getAttribute()).value;
    	
    	ArrayList<SymbolMap> t = currentst.next;
    	currentst=null;
    	
    	if(t.isEmpty())
    		throw new CodeGenerationException("No Symboltables generated");
    	
    	for( SymbolMap a:t) {

    		if(a.tableName.equals(name)) {
    			currentst=a;
    		}
    	}
    	if(currentst==null)
    		throw new CodeGenerationException("Symboltable for: "+name +" not found");
    	
    	ProcAddr.put(name, (long) ar.size());

    	genCpsCmd(c);
    }
    
    public CodeArray getCode() {
        return codeArray;
    }

    public int getStoreSize() {
        return 100;
    }

}