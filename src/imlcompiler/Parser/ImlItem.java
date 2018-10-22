package imlcompiler.Parser;

import imlcompiler.Scanner.Token;

import java.util.Iterator;

public class ImlItem extends ImlComponent{

    Token token;

    public ImlItem(Token token){
        this.token = token;
    }

    public void print(){
        System.out.println(token.getTerminal());
    }

    public Iterator<ImlComponent> createIterator(){
        return new NullIterator();
    }
}
