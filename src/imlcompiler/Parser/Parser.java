package imlcompiler.Parser;

import imlcompiler.CompileErrorException;
import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.TokenList;

public class Parser {

    TokenList tokenList;
    Token token;
    String tokenClass;

    public Parser(TokenList tl){
        this.tokenList = tl;
        this.tokenList.reset();
        this.token = this.tokenList.nextToken();
        this.tokenClass = this.token.getClass().getSimpleName();
    }

    public ConcreteSyntaxTree.Program parse() throws CompileErrorException {
        // parsing the start symbol ...
        ConcreteSyntaxTree.Program program = program();
        // ... and then consuming the SENTINEL (erst ganz am Schluss :-) )
        // todo SENTINEL
        // consume(Terminals.SENTINEL);
        return program;
    }

    private Token consume(String expectedTerminal) throws CompileErrorException {
        if (this.tokenClass.equals(expectedTerminal)) {

            Token consumedToken = this.token;

            if (this.tokenClass != "SentinelToken") {
                this.token = tokenList.nextToken();
                this.tokenClass = token.getClass().getSimpleName();
            }
            return consumedToken;
        }
        else
        {
            throw new CompileErrorException("terminal expected: " + expectedTerminal +
                    ", terminal found: " + this.tokenClass);
        }
    }

    private ConcreteSyntaxTree.Program program(){
        System.out.println("Program Token consumed");
        System.out.println(this.tokenClass);
        consume("FlowControlToken");
        //consume("FlowControlToken");
        return null;
    }

}
