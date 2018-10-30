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
                    ", terminal found: " + this.tokenTerminal + "  " + this.token.toString());
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
    cpsCmd ::= cmd {SEMICOLON cmd}
     */
    private ImlComponent cpsCmd(){ // [[DONE]]
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
        else if (this.tokenTerminal == Terminal.CALL) {
            cmd.add(new ImlItem(consume(Terminal.CALL)));
            cmd.add(new ImlItem(consume(Terminal.IDENT)));
            cmd.add(exprList());
            //todo [globInits]
            return cmd;
        }
        else if (this.tokenTerminal == Terminal.DEBUGIN){
            cmd.add(new ImlItem(consume(Terminal.DEBUGIN)));
            cmd.add(expr());
            return cmd;
        }
        else if (this.tokenTerminal == Terminal.DEBUGOUT){
            cmd.add(new ImlItem(consume(Terminal.DEBUGOUT)));
            cmd.add(expr());
            return cmd;
        }
        else {
            cmd.add(expr());
            cmd.add(new ImlItem(consume(Terminal.BECOMES)));
            cmd.add(expr());
            return cmd;
        }
    }


























    /*
        expr    ::= term1 {BOOLOPR term1}
        term1  ::= term2 [RELOPR term2]
        term2  ::= term3 {ADDOPR term3}
        term3  ::= factor {MULTOPR factor}
        factor  ::= LITERAL
                    | IDENT [INIT | exprList]
                    | monadicOpr factor
                    | LPAREN expr RPAREN

        exprList ::= LPAREN [expr {COMMA expr}] RPAREN
        monadicOpr ::= NOT | ADDOPR
        */
    private ImlComponent expr(){   // [[DONE]]
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

    private ImlComponent term1(){  // [[DONE]]
        ImlComponent term1 = new ImlComposite("term1");
        term1.add(term2());
        if (this.tokenTerminal == Terminal.RELOPR){
            term1.add(new ImlItem(consume(Terminal.RELOPR)));
            term1.add(term2());
        }
        return term1;
    }

    private ImlComponent term2(){ //[[DONE]]
        ImlComponent term2 = new ImlComposite("term2");
        term2.add(term3());
        if (this.tokenTerminal == Terminal.ADDOPR){
            while(this.tokenTerminal == Terminal.ADDOPR){
                term2.add(new ImlItem(consume(Terminal.ADDOPR)));
                term2.add(term3());
            }
        }
        return term2;
    }

    private ImlComponent term3(){ //[[DONE]]
        ImlComponent term3 = new ImlComposite("term3");
        term3.add(factor());
        if (this.tokenTerminal == Terminal.MULTOPR){
            while(this.tokenTerminal == Terminal.MULTOPR){
                term3.add(new ImlItem(consume(Terminal.MULTOPR)));
                term3.add(factor());
            }
        }
        return factor();
    }

    private ImlComponent factor(){ //[[DONE]]
        ImlComponent factor = new ImlComposite("factor");
        if (this.tokenTerminal == Terminal.LITERAL){
            factor.add(new ImlItem(consume(Terminal.LITERAL)));
        }
        else if(this.tokenTerminal == Terminal.IDENT){
            factor.add(new ImlItem(consume(Terminal.IDENT)));
            if (this.tokenTerminal == Terminal.INIT){
                factor.add(new ImlItem(consume(Terminal.INIT)));
            }
            else if (this.tokenTerminal == Terminal.LPAREN){
                factor.add(exprList());
            }
            else {}
        }
        else if (this.tokenTerminal == Terminal.LPAREN){
            factor.add(new ImlItem(consume(Terminal.LPAREN)));
            factor.add(expr());
            factor.add(new ImlItem(consume(Terminal.RPAREN)));
        }
        else if (this.tokenTerminal == Terminal.NOTOPR || this.tokenTerminal == Terminal.ADDOPR){
            factor.add(monadicOpr());
            factor.add(factor());
        }
        else{}
        return factor;
    }
    //exprList ::= LPAREN [expr {COMMA expr}] RPAREN
    private ImlComponent exprList(){ //[[DONE]]
        ImlComponent exprList = new ImlComposite("exprList");
        exprList.add(new ImlItem(consume(Terminal.LPAREN)));

        if (this.tokenTerminal != Terminal.RPAREN){
            exprList.add(expr());
            if(this.tokenTerminal == Terminal.COMMA) {
                while (this.tokenTerminal == Terminal.COMMA) {
                    exprList.add(new ImlItem(consume(Terminal.COMMA)));
                    exprList.add(expr());
                }
            }
            //else throw new ParserErrorException("error in exprList");
        }
        exprList.add(new ImlItem(consume(Terminal.RPAREN)));
        return exprList;
    }

    private ImlComponent monadicOpr(){ //[[DONE]]
        ImlComponent monadicOpr = new ImlComposite("monadicOpr");
        if (this.tokenTerminal == Terminal.NOTOPR) {   //should be Terminal.NOT
            monadicOpr.add(new ImlItem(consume(Terminal.NOTOPR)));
        }
        else if (this.tokenTerminal == Terminal.ADDOPR){
            monadicOpr.add(new ImlItem(consume(Terminal.ADDOPR)));

        }
        else {
            throw new ParserErrorException("Error at monadicOpr");
        }
        return monadicOpr;
    }



}
