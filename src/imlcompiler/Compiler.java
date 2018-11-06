package imlcompiler;

import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlComposite;
import imlcompiler.Parser.Parser;
import imlcompiler.Scanner.Scanner;
import imlcompiler.Scanner.TokenList;

import java.util.Iterator;

public class Compiler {


    // Usage: add path to iml file to run configuration

    public static void main(String[] args){
    	
        String file;
        if (args.length < 1) {
        	System.out.println("No iml program provided");
        	file="examplePrograms/00_EuclidExtendedNat.iml";
        }else {
        	file=args[0];
        }
        parse(file,true);
    }
    public static void parse(String file,boolean printtokenlist){

        
        Scanner scanner = new Scanner(file);

        TokenList tokenList = new TokenList();

        try {
            tokenList = scanner.run();
            //if(printtokenlist)
            	//System.out.println(tokenList);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("---> Started Parsing the Program");

        Parser parser = new Parser(tokenList);

        ImlComponent concreteSyntaxTree = parser.parse();

        Iterator<ImlComponent> iterator = concreteSyntaxTree.createIterator();

        while (iterator.hasNext()){
            ImlComponent imlComponent = iterator.next();
            //imlComponent.print();
        }

    }
    
    
}
