package imlcompiler.Scanner;

import java.util.LinkedList;

public class TokenList {

    LinkedList<Token> list = new LinkedList<>();

    public boolean add(Token token){

        return list.add(token);

    }



    public String toString(){

        StringBuilder sb = new StringBuilder();

        for (Token t: list){
        	sb.append(t.toString());
      
        }

        return sb.toString();
    }

}
