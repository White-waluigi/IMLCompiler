package imlcompiler;

import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.Parser;
import imlcompiler.Parser.treeVisualisation.Wrapper;
import imlcompiler.Scanner.Scanner;
import imlcompiler.Scanner.TokenList;

import java.util.Iterator;

import imlcompiler.ScopeChecker.ScopeChecker;
import imlcompiler.Symboltable.SymbolMap;

public class Compiler {


    // Usage: add path to iml file to run configuration

    public static void main(String[] args){
    	
        String file;
        if (args.length < 1) {
        	System.out.println("No iml program provided");
        	file="examplePrograms/01_testTupel1.iml";
        }else {
        	file=args[0];
        }
        parse(file,true);
    }
    public static void parse(String file,boolean printtokenlist) {

        Scanner scanner = new Scanner(file);

        TokenList tokenList = new TokenList();

        try {
            tokenList = scanner.run();
            //if(printtokenlist)
            //System.out.println(tokenList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("---> Started Parsing the Program");

        Parser parser = new Parser(tokenList);

        Wrapper wrapper = new Wrapper();

        ImlComponent concreteSyntaxTree = parser.parse(wrapper);

        //Visualisation of Syntax Tree
        /*
        Frame f = new TreeEditor((Tree<String, ?>) concreteSyntaxTree, wrapper);
        f.setSize(800, 800);
        f.setVisible(true);
        */

        System.out.println("---> Converting to Abstract Syntax Tree");
        ImlComponent abstractSyntaxTree = concreteSyntaxTree.toAbstract();
        System.out.println("---> AST Done");
        //new Visualization of Syntax Trees
        //TreeList.startNew(concreteSyntaxTree,abstractSyntaxTree, wrapper,file);
        
        //Visualisation of Syntax Tree
//        
//        Frame f = new TreeEditor((Tree<String, ?>) abstractSyntaxTree, wrapper);
//        f.setSize(800, 800);
//        f.setVisible(true);
        
        /*
        Iterator<ImlComponent> iterator = concreteSyntaxTree.createIterator();

        while (iterator.hasNext()) {
            ImlComponent imlComponent = iterator.next().toAbstract();
            //imlComponent.print();
            if (imlComponent != null) {
                abstractSyntaxTree.add(imlComponent);
            }
        }

        */

        Iterator<ImlComponent> iterator2 = abstractSyntaxTree.createIterator();


        /*while (iterator2.hasNext()) {
            ImlComponent imlComponent = iterator2.next();
            imlComponent.print();
        }*/

        //scope checking
        SymbolMap symbolTables = new SymbolMap("global", null);
        ScopeChecker scopeChecker = new ScopeChecker(iterator2, symbolTables);
        try {
            scopeChecker.check();
        } catch (Exception e) {
            e.printStackTrace();
        }
        symbolTables.print();

        /*
        Codegenerator codegenerator = null;
        try {
            codegenerator = new Codegenerator(abstractSyntaxTree);
        } catch (ICodeArray.CodeTooSmallError codeTooSmallError) {
            codeTooSmallError.printStackTrace();
        }

        CodeArray codeArray = codegenerator.getCode();

        int storeSize = codegenerator.getStoreSize();

        try {

            VirtualMachine vm = new VirtualMachine(codeArray, storeSize,false);

        } catch (IVirtualMachine.ExecutionError executionError) {
            executionError.printStackTrace();
        }

        */
    }
}
