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
    program ::= PROGRAM IDENT progParamList [GLOBAL cpsDecl] DO cpsCmd ENDPROGRAM
    */
    private ImlComponent program(){

        ImlComponent program = new ImlComposite("program");

        program.add(new ImlItem(consume(Terminal.PROGRAM)));
        program.add(new ImlItem(consume(Terminal.IDENT)));
        //todo progParamList
        //todo [GLOBAL cpsDecl]
        program.add(new ImlItem(consume(Terminal.DO)));
        program.add(cpsCmd());
        program.add(new ImlItem(consume(Terminal.ENDPROGRAM)));

        return program;
    }

    /*
    cpsCmd ::= cmd {SEMICOLON cmd} [[DONE]]
     */
    private ImlComponent cpsCmd(){
        ImlComponent cpsCmd = new ImlComposite("cpsCmd");
        cpsCmd.add(cmd());
        if (this.tokenTerminal == Terminal.SEMICOLON) {
            while (this.tokenTerminal == Terminal.SEMICOLON) {
                cpsCmd.add(new ImlItem(consume(Terminal.SEMICOLON)));
                cpsCmd.add(cmd());
            }
        }
        return cpsCmd;
    }

    /*
        cmd ::= SKIP
            | expr BECOMES expr
            | IF expr THEN cpsCmd ELSE cpsCmd ENDIF
            | WHILE expr DO cpsCmd ENDWHILE
            | CALL IDENT exprList [globInits]
            | DEBUGIN expr
            | DEBUGOUT expr
    */
    private ImlComponent cmd(){
        ImlComponent cmd = new ImlComposite("cmd");
        if(this.tokenTerminal == Terminal.SKIP) {
            cmd.add(new ImlItem(consume(Terminal.SKIP)));
            return cmd;
        }
        else if(this.tokenTerminal == Terminal.IF){
            cmd.add(new ImlItem(consume(Terminal.IF)));
            cmd.add(expr());
            cmd.add(new ImlItem(consume(Terminal.THEN)));
            cmd.add(cpsCmd());
            cmd.add(new ImlItem(consume(Terminal.ELSE)));
            cmd.add(cpsCmd());
            cmd.add(new ImlItem(consume(Terminal.ENDIF)));
            return cmd;
        }
        else if (this.tokenTerminal == Terminal.WHILE){
            cmd.add(new ImlItem(consume(Terminal.WHILE)));
            cmd.add(expr());
            cmd.add(new ImlItem(consume(Terminal.DO)));
            cmd.add(cpsCmd());
            cmd.add(new ImlItem(consume(Terminal.ENDWHILE)));
            return cmd;
        }
        //todo other grammar
        else {
            throw new ParserErrorException("Error in cmd()");
        }
    }

    /*
        expr    ::= term1 {BOOLOPR term1}
        term1  ::= term2 [RELOPR term2]
        term2  ::= term3 {ADDOPR term3}
        term3  ::= factor {MULTOPR factor}
        factor  ::= LITERAL
                    | IDENT [INIT
                    | exprList]
                    | monadicOpr factor
                    | LPAREN expr RPAREN

        exprList ::= LPAREN [expr {COMMA expr}] RPAREN
        monadicOpr ::= NOT | ADDOPR
        */
    private ImlComponent expr(){
        ImlComponent expr = new ImlComposite("expr");
        expr.add(term1());
        if (this.tokenTerminal == Terminal.BOOLOPR){
            while (this.tokenTerminal == Terminal.BOOLOPR){
                expr.add(new ImlItem(consume(Terminal.BOOLOPR)));
                expr.add(term1());
            }
        }
        return expr;
    }

    private ImlComponent term1(){
        ImlComponent term1 = new ImlComposite("term1");
        //todo finish term1
        return term1;
    }


}
