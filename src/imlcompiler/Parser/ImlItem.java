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

    public ImlItem toAbstract(){
        Token.Terminal _token = this.token.getTerminal();
        if (_token == Token.Terminal.LPAREN){
            System.out.println("-----> LPAREN eliminated");
            return null;

        }
        else {
            System.out.println("_token ------------ " +_token.toString());
            return this;
        }
    }
}
