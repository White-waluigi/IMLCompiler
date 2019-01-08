package imlcompiler;

import imlcompiler.Codegenerator.CodeGenerator;
import imlcompiler.Codegenerator.Codegenerator_old;
import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.Parser;
import imlcompiler.Parser.treeVisualisation.TreeEditor;
import imlcompiler.Parser.treeVisualisation.Wrapper;
import imlcompiler.Scanner.Scanner;
import imlcompiler.Scanner.TokenList;

import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import TreeList.TreeList;
import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import debugger.Debugger;
import imlcompiler.ScopeChecker.ScopeChecker;
import imlcompiler.Symboltable.SymbolMap;

public class Compiler {


    // Usage: add path to iml file to run configuration

    public static void main(String[] args){
    	
        String file;
        if (args.length < 1) {
        	System.out.println("No iml program provided");
        	file="examplePrograms/03_wecker.iml";
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

        //parsing
        ImlComponent concreteSyntaxTree = parser.parse(wrapper);

        //Visualisation of Syntax Tree
        
//        Frame f = new TreeEditor((Tree<String, ?>) concreteSyntaxTree, wrapper);
//        f.setSize(800, 800);
//        f.setVisible(true);
        

        System.out.println("---> Converting to Abstract Syntax Tree");
        ImlComponent abstractSyntaxTree = concreteSyntaxTree.toAbstract();
        System.out.println("---> AST Done");
        //new Visualization of Syntax Trees by Marvin
        TreeList.startNew(concreteSyntaxTree,abstractSyntaxTree, wrapper,file);
        
        //Visualisation of Syntax Tree by Andreas
//        Frame f = new TreeEditor((Tree<String, ?>) abstractSyntaxTree, wrapper);
//        f.setSize(800, 800);
//        f.setVisible(true);

        Iterator<ImlComponent> iterator2 = abstractSyntaxTree.createIterator();

        //scope and type checking
        SymbolMap symbolTables = new SymbolMap("global", null);
        ScopeChecker scopeChecker = new ScopeChecker(iterator2, symbolTables);
        try {
            scopeChecker.check();
        } catch (Exception e) {
            e.printStackTrace();
            //return; //exit
        }

        symbolTables.print();

        CodeGenerator codegenerator = null;
        try {
        	codegenerator = new CodeGenerator(abstractSyntaxTree,symbolTables);
        } catch (CodeArray.CodeTooSmallError codeTooSmallError) {
            codeTooSmallError.printStackTrace();
        }

        Debugger d=new Debugger(512, codegenerator,file);
        d.setVisible(true);
        
    }
}
