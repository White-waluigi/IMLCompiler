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
import imlcompiler.Symboltable.Symbol;
import imlcompiler.Symboltable.SymbolMap;

public class Codegenerator_old {

    ImlComponent ast;
    CodeArray codeArray;
    int SIZE = 12;
    ArrayList<IInstructions.IInstr> ar;
    private SymbolMap currentst;
    private SymbolMap globalst;

    Map<String, Integer> ProcAddr;
    long freeProcSpace=0;
	private int curPrStart=1;
	
    public Codegenerator_old(ImlComponent ast, SymbolMap symbolTables) throws ICodeArray.CodeTooSmallError {
        
    	ProcAddr=new HashMap<>();
    	
    	this.ast = ast;
        this.globalst = symbolTables;

        ar = new ArrayList<>();
        
        //PLACE HOLDER WILL BE DELETED
        ar.add(new IInstructions.Stop());

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

        ar.remove(0);
        this.codeArray = new CodeArray(ar.size()+1);
        int i = 0;
        codeArray.put(i++,new IInstructions.UncondJump(curPrStart));
        for (IInstr a : ar) {
            codeArray.put(i++, a);
        }

    }

    private void genRoot(ImlComponent ast) {
    	currentst=globalst;

        for(int i=0;i<ast.size();i++) {
        	if(ast.getChild(i).getToken()!=null&&ast.getChild(i).getToken().getTerminal()==Terminal.PROC) {
        		genProc((ImlComposite) ast.getChild(i));
        	}
        	
        }
        
    	currentst=globalst;

    	genCpsCmd(ast);
        ar.add(new IInstructions.Stop());
        
        


    }
    private void genCpsCmd(ImlComponent ast) {
    	
    	
    	
        reserveSymbolTable();
        ImlComposite m = (ImlComposite) ast.getChild("cpsCmd");
        ImlComposite g = null;
        for (int i = 0; i < m.size(); i++) {
            g = (ImlComposite) m.getChild(i);
            if(g.getToken()==null) {
            	throw new CodeGenerationException("empty command");
            }else if (g.getToken().getTerminal() == Terminal.BECOMES) {
                genAssign(g);
            }
            else if (g.getToken().getTerminal() == Terminal.DEBUGOUT) {
                genDebugout(g);
            }else if (g.getToken().getTerminal() == Terminal.CALL) {
            	genCallProc(g);
            }
        }
    }

    private void genCallProc(ImlComposite g) {
    	
    	String procName=((IdentAttribute)g.getChild(Terminal.IDENT).getToken().getAttribute()).value;
		 
    	int offset=0;
		 if(currentst!=globalst)
			 offset=3;	
    	
    	
    	for (int i = 0; i < currentst.getSize(); i++) {
			 Symbol array_element = currentst.get(i);
			 if(array_element.isGlobal) {
				 ar.add(new IInstructions.LoadAddrRel( array_element.location  +offset));				 
				 
				 if(offset!=0)
					 ar.add(new IInstructions.Deref());		 
				 
			 }
		}    	
    	ImlComponent m = g.getChild("exprList");
    	for(int i=0;i<m.size();i++) {
    			genExpr(m.getChild(i));
    			
    	}

    	
    	
    	ar.add(new IInstructions.Call( ProcAddr.get(   procName  )  ));


	}

	private void genDebugout(ImlComposite g) {
        genExpr(g.getChild(0));
        ar.add(new IInstructions.OutputInt("Out:"));
	}

    
	private void reserveSymbolTable() {

		ar.add(new IInstructions.AllocBlock(currentst.getSize()));
		
		
		if(currentst==globalst) {
			return;
		}
        for(int i=0;i<currentst.getSize();i++) {
        	System.out.println("directing "+currentst.get(i).name);
        	ar.add(new IInstructions.LoadAddrRel(currentst.get(i).location+3));
        	ar.add(new IInstructions.LoadAddrRel(currentst.get(i).location-currentst.getSize()));
        	ar.add(new IInstructions.Deref());
        	ar.add(new IInstructions.Store());
        	
        }
    }

    private void genAssign(ImlComposite g) {
    	

        ar.add(new IInstructions.LoadAddrRel(getLocation(g.getChild(0))));
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
        ar.add(new IInstructions.LoadAddrRel(getLocation(child)));
        ar.add(new IInstructions.Deref());
    }

    
    
    private void genLiteral(ImlComponent child) {
        int val = ((Token.IntAttribute) child.getToken().getAttribute()).value;
        ar.add(new IInstructions.LoadImInt(val));

    }

    private void genOperation(ImlComposite child) {

        IInstructions.IInstr in = null;

        int parameterid=globalst.getSize();
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
    	String name=((IdentAttribute)c.getChild(Terminal.IDENT).getToken().getAttribute()).value;
    	
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
    	
    	ProcAddr.put(name, (int) ar.size());

    	genCpsCmd(c);
    	
        ar.add(new IInstructions.Return(0));

        curPrStart=ar.size();
    }
    
    public CodeArray getCode() {
        return codeArray;
    }

    public int getStoreSize() {
        return 100;
    }

    private int getLocation(ImlComponent child) {
    	
    	int offset=currentst==globalst?0:3;
    	IdentAttribute x = ((Token.IdentAttribute) child.getToken().getAttribute());
    	String p = x.value;
    	System.out.println("finding "+p+" in "+currentst.tableName);
    	System.out.println( currentst.get(p));
    	
    	return currentst.get(p).location+offset;
    }
}