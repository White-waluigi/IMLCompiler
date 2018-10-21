package imlcompiler.Parser;

import imlcompiler.Scanner.ScannerErrorException;
import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.Token.Terminal;
import imlcompiler.Scanner.TokenList;

public class Parser {

    TokenList tokenList;
    Token token;
    Terminal tokenTerminal;

    public Parser(TokenList tl){
        this.tokenList = tl;
        this.tokenList.reset();
        this.token = this.tokenList.nextToken();
        this.tokenTerminal = this.token.getTerminal();
    }

    public ConcreteSyntaxTree.Program parse() throws ScannerErrorException {
        // parsing the start symbol ...
        ConcreteSyntaxTree.Program program = program();
        // ... and then consuming the SENTINEL (erst ganz am Schluss :-) )
        // todo SENTINEL
        // consume(Terminals.SENTINEL);
        return program;
    }

    private Token consume(Terminal terminal) throws ScannerErrorException {
        if (this.tokenTerminal==terminal) {

            Token consumedToken = this.token;

            if (!this.token.is(Terminal.SENTINEL)) {
                this.token = tokenList.nextToken();
                this.tokenTerminal = token.getTerminal();
            }
            return consumedToken;
        }
        else
        {
            throw new ScannerErrorException("terminal expected: " + terminal +
                    ", terminal found: " + this.tokenTerminal);
        }
    }

    private ConcreteSyntaxTree.Program program(){
        System.out.println("Program Token consumed");
        //System.out.println(this.tokenTerminal);
        consume(Terminal.PROGRAM);
        consume(Terminal.ENDPROGRAM);
        return null;
    }

}
