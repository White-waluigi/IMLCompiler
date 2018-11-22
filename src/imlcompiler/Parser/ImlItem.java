package imlcompiler.Parser;

import imlcompiler.Scanner.Token;

import java.util.Iterator;

public class ImlItem extends ImlComponent{

    Token token;

    Node<String, ImlComponent > element;

    public ImlItem(Token token){

        this.token = token;
        element = new Node(this.token.getTerminal().toString(), this.token);
    }

    public Token getToken(){
        return this.token;
    }

    public void print(){
        System.out.println(token.getTerminal());
    }

    public String getName(){
        return this.token.getTerminal().toString();
    }

    public int size(){
        return 1;
    }

    public Iterator<ImlComponent> createIterator(){
        return new NullIterator();
    }

    public ImlComponent toAbstract(){
        Token.Terminal _token = this.token.getTerminal();
        switch(_token) {
            case PROGRAM:
            case LPAREN:
            case RPAREN:
            case COMMA:
            case SEMICOLON:
            case COLON:
            case LBRACK:
            case RBRACK:
                System.out.println(_token.toString() + " eliminated");
                return null;
            default: return this;
        }
    }


}
