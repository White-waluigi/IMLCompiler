package imlcompiler.Parser;

import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.Token.Terminal;

import java.util.Iterator;

public class ImlItem extends ImlComponent{


    Node<String, ImlComponent > element;

    public ImlItem(Token token){

        this.token = token;
        element = new Node(this.token.getTerminal().toString(), this.token);
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
    public boolean destructable() {
        Token.Terminal _token = this.token.getTerminal();

      if(token.isOperator()) {
      	return true;
      
      }
      switch(_token) {
          case PROGRAM:
          case LPAREN:
          case RPAREN:
          case COMMA:
          case SEMICOLON:
          case COLON:
          case LBRACK:
          case RBRACK:
          case ENDFUN:
          case ENDPROC:
          case ENDPROGRAM:
          case ENDWHILE:
          case ENDIF:
          case PROC:
          case DO:
          case INIT:	  
              return true;
          default: return false;
      }
    }
    public ImlComponent toAbstract(){
    	if(destructable()) {
            System.out.println(this.token.getTerminal().toString() + " eliminated");

    		return null;
    	}
    	return this;
    }


}
