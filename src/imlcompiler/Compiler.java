package imlcompiler;

import imlcompiler.Parser.Parser;
import imlcompiler.Scanner.Scanner;
import imlcompiler.Scanner.TokenList;

public class Compiler {


    // Usage: add path to iml file to run configuration

    public static void main(String[] args){
    	
 
//        Parser parser = new Parser(tokenList);

        //parser.parse();

        //System.out.println(tokenList.toString());
       	String file;
        if (args.length < 1) {
        	System.out.println("No iml program provided");
        	file="examplePrograms/Cube.iml";
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
            if(printtokenlist)
            	System.out.println(tokenList);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    
    
}
