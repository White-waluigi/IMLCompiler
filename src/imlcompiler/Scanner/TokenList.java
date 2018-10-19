package imlcompiler.Scanner;

import java.util.LinkedList;

public class TokenList {

    LinkedList<Token> list = new LinkedList<>();

    int index = 0;

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

    public Token nextToken(){
        return list.get(index++);
    }

    public void reset(){
        this.index = 0;
    }

}
