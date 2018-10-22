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

    public ImlComponent parse() throws ScannerErrorException {
        // parsing the start symbol ...
        ImlComponent program = program();
        // ... and then consuming the SENTINEL
        consume(Terminal.SENTINEL);
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
            throw new ParserErrorException("terminal expected: " + terminal +
                    ", terminal found: " + this.tokenTerminal);
        }
    }


    /*
    *   program ::= PROGRAM IDENT progParamList [GLOBAL cpsDecl] DO cpsCmd ENDPROGRAM
    */
    private ImlComponent program(){

        ImlComponent program = new ImlComposite("program");

        program.add(new ImlItem(consume(Terminal.PROGRAM)));
        program.add(new ImlItem(consume(Terminal.IDENT)));
        //progParamList
        //[GLOBAL cpsDecl]
        program.add(new ImlItem(consume(Terminal.DO)));
        program.add(cpsCmd());
        program.add(new ImlItem(consume(Terminal.ENDPROGRAM)));

        return program;
    }

    //cpsCmd ::= cmd {SEMICOLON cmd}
    private ImlComponent cpsCmd(){
        ImlComponent cpsCmd = new ImlComposite("cpsCmd");
        cpsCmd.add(cmd());
        cpsCmd.add(new ImlItem(consume(Terminal.SEMICOLON)));
        cpsCmd.add(cmd());
        return cpsCmd;
    }

    /*
        cmd ::= SKIP
            | expr BECOMES expr
            | IF expr THEN cpsCmd ELSE cpsCmd ENDIF | WHILE expr DO cpsCmd ENDWHILE
            | CALL IDENT exprList [globInits]
            | DEBUGIN expr
            | DEBUGOUT expr
    */
    private ImlComponent cmd(){
        ImlComponent cmd = new ImlComposite("cmd");
        cmd.add(new ImlItem(consume(Terminal.SKIP)));

        return cmd;
    }
}
