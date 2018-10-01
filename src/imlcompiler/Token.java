package imlcompiler;

public class Token {

    String lexeme;

    public Token(String input){

        this.lexeme = input;

    }


    public String toString(){
        return this.lexeme;
    }


}
