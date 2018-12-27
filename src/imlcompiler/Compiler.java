package imlcompiler;

import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.IVirtualMachine;
import ch.fhnw.lederer.virtualmachineFS2015.VirtualMachine;
import debugger.Debugger;
import debugger.Debugger.MemoryCell;
import debugger.Debugger.MemoryView;
import imlcompiler.Codegenerator.Codegenerator;
import imlcompiler.Parser.CompositeIterator;
import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlComposite;
import imlcompiler.Parser.Parser;
import imlcompiler.Parser.treeVisualisation.Tree;
import imlcompiler.Parser.treeVisualisation.TreeEditor;
import imlcompiler.Parser.treeVisualisation.Wrapper;
import imlcompiler.Scanner.Scanner;
import imlcompiler.Scanner.TokenList;

import java.awt.*;
import java.util.Iterator;

import TreeList.TreeList;
import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;

import static java.lang.Thread.sleep;

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

        ImlComponent abstractSyntaxTree = concreteSyntaxTree.toAbstract();

       TreeList.startNew(concreteSyntaxTree,abstractSyntaxTree, wrapper,file);
        
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

        System.out.println("---> Converting to Abstract Syntax Tree");
        while (iterator2.hasNext()) {
            ImlComponent imlComponent = iterator2.next();
            imlComponent.print();
        }


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


    }
}
