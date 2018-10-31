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
        program.add(progParamList());
        if (this.tokenTerminal == Terminal.GLOBAL){
            program.add(new ImlItem(consume(Terminal.GLOBAL)));
            program.add(cpsDecl());
        }
        program.add(new ImlItem(consume(Terminal.DO)));
        program.add(cpsCmd());
        program.add(new ImlItem(consume(Terminal.ENDPROGRAM)));

        return program;
    }


   /*
    progParamList ::= LPAREN [progParam {COMMA progParam}] RPAREN

    progParam ::= [FLOWMODE] [CHANGEMODE] typedIdent

    paramList ::= LPAREN [param {COMMA param}] RPAREN

    param ::= [FLOWMODE] [MECHMODE] [CHANGEMODE] typedIdent

    typedIdent ::= IDENT COLON ATOMTYPE

    */

    private ImlComponent progParamList(){
        ImlComponent progParamList = new ImlComposite("progParamList");
        progParamList.add(new ImlItem(consume(Terminal.LPAREN)));
        if (this.tokenTerminal == Terminal.FLOWMODE || this.tokenTerminal == Terminal.CHANGEMODE ||
                this.tokenTerminal == Terminal.IDENT) {
            progParamList.add(progParam());
            while (this.tokenTerminal == Terminal.COMMA){
                progParamList.add(new ImlItem(consume(Terminal.COMMA)));
                progParamList.add(progParam());
            }
        }
        progParamList.add(new ImlItem(consume(Terminal.RPAREN)));
        return progParamList;
    }

    private ImlComponent progParam(){
        ImlComponent progParam = new ImlComposite("progParam");
        if (this.tokenTerminal == Terminal.FLOWMODE){
            progParam.add(new ImlItem(consume(Terminal.FLOWMODE)));
        }
        if (this.tokenTerminal == Terminal.CHANGEMODE){
            progParam.add(new ImlItem(consume(Terminal.CHANGEMODE)));
        }
        progParam.add(typedIdent());
        return progParam;
    }

    private ImlComponent typedIdent(){
        ImlComponent typedIdent = new ImlComposite("typedIdent");
        typedIdent.add(new ImlItem(consume(Terminal.IDENT)));
        typedIdent.add(new ImlItem(consume(Terminal.COLON)));
        typedIdent.add(new ImlItem(consume(Terminal.TYPE)));   //shold be ATOMTYPE
        return typedIdent;
    }


    private ImlComponent paramList(){
        ImlComponent paramList = new ImlComposite("paramList");
        paramList.add(new ImlItem(consume(Terminal.LPAREN)));
        if (this.tokenTerminal == Terminal.FLOWMODE || this.tokenTerminal == Terminal.MECHMODE ||
                this.tokenTerminal == Terminal.CHANGEMODE || this.tokenTerminal == Terminal.IDENT){
            paramList.add(param());
            while(this.tokenTerminal == Terminal.COMMA){
                paramList.add(new ImlItem(consume(Terminal.COMMA)));
                paramList.add(param());

            }
        }
        paramList.add(new ImlItem(consume(Terminal.RPAREN)));
        return paramList;
    }

    private ImlComponent param(){
        ImlComponent param = new ImlComposite("param");
        if (this.tokenTerminal == Terminal.FLOWMODE ){
            param.add(new ImlItem(consume(Terminal.FLOWMODE)));
        }
        if (this.tokenTerminal == Terminal.MECHMODE ){
            param.add(new ImlItem(consume(Terminal.MECHMODE)));
        }
        if (this.tokenTerminal == Terminal.CHANGEMODE ){
            param.add(new ImlItem(consume(Terminal.CHANGEMODE)));
        }
        param.add(typedIdent());
        return param;
    }

    /*
    cpsCmd ::= cmd {SEMICOLON cmd}
     */
    private ImlComponent cpsCmd(){ // [[DONE]]
        ImlComponent cpsCmd = new ImlComposite("cpsCmd");
        cpsCmd.add(cmd());
        while (this.tokenTerminal == Terminal.SEMICOLON) {
            cpsCmd.add(new ImlItem(consume(Terminal.SEMICOLON)));
            cpsCmd.add(cmd());
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
            if (this.tokenTerminal == Terminal.INIT ){
                cmd.add(globInits());
            }
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

    //globInits ::= INIT IDENT { COMMA IDENT }

    private ImlComponent globInits(){
        ImlComponent globInits = new ImlComposite("globInits");
        globInits.add(new ImlItem(consume(Terminal.INIT)));
        globInits.add(new ImlItem(consume(Terminal.IDENT)));
        while (this.tokenTerminal == Terminal.COMMA){
            globInits.add(new ImlItem(consume(Terminal.COMMA)));
            globInits.add(new ImlItem(consume(Terminal.IDENT)));
        }
        return globInits;
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
        while (this.tokenTerminal == Terminal.BOOLOPR){
            expr.add(new ImlItem(consume(Terminal.BOOLOPR)));
            expr.add(term1());
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
        while(this.tokenTerminal == Terminal.ADDOPR){
            term2.add(new ImlItem(consume(Terminal.ADDOPR)));
            term2.add(term3());
        }
        return term2;
    }

    private ImlComponent term3(){ //[[DONE]]
        ImlComponent term3 = new ImlComposite("term3");
        term3.add(factor());
        while(this.tokenTerminal == Terminal.MULTOPR){
            term3.add(new ImlItem(consume(Terminal.MULTOPR)));
            term3.add(factor());
        }
        return factor();
    }
    /*
        factor  ::=   LITERAL
                    | IDENT [INIT | exprList]
                    | monadicOpr factor
                    | LPAREN expr RPAREN
    */
    private ImlComponent factor(){
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
            while (this.tokenTerminal == Terminal.COMMA) {
                exprList.add(new ImlItem(consume(Terminal.COMMA)));
                exprList.add(expr());
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

    /*
    decl ::= stoDecl
            | funDecl
            | procDecl
    */
    private ImlComponent decl(){
        ImlComponent decl = new ImlComposite("decl");
        if (this.tokenTerminal == Terminal.CHANGEMODE || this.tokenTerminal == Terminal.IDENT){
            decl.add(stoDecl());
        }
        else if (this.tokenTerminal == Terminal.FUN){
            decl.add(funDecl());
        }
        else if (this.tokenTerminal == Terminal.PROC){
            decl.add(procDecl());
        }
        else {
            throw new ParserErrorException("error in decl");
        }
        return decl;
    }

    /*
    stoDecl ::= [CHANGEMODE] typedIdent
    */
    private ImlComponent stoDecl(){
        ImlComponent stoDecl = new ImlComposite("stoDecl");
        if (this.tokenTerminal == Terminal.CHANGEMODE){
            stoDecl.add(new ImlItem(consume(Terminal.CHANGEMODE)));
        }
        stoDecl.add(typedIdent());
        return stoDecl;
    }

    /*
    funDecl ::= FUN IDENT paramList RETURNS stoDecl [GLOBAL globImps] [LOCAL cpsStoDecl] DO cpsCmd ENDFUN
    */
    private ImlComponent funDecl(){
        ImlComponent funDecl = new ImlComposite("funDecl");
        funDecl.add(new ImlItem(consume(Terminal.FUN)));
        funDecl.add(new ImlItem(consume(Terminal.IDENT)));
        funDecl.add(paramList());
        funDecl.add(new ImlItem(consume(Terminal.RETURNS)));
        funDecl.add(stoDecl());
        if (this.tokenTerminal == Terminal.GLOBAL){
            funDecl.add(new ImlItem(consume(Terminal.GLOBAL)));
            funDecl.add(globImps());
        }
        if (this.tokenTerminal == Terminal.LOCAL){
            funDecl.add(new ImlItem(consume(Terminal.LOCAL)));
            funDecl.add(cpsStoDecl());
        }
        funDecl.add(new ImlItem(consume(Terminal.DO)));
        funDecl.add(cpsCmd());
        funDecl.add(new ImlItem(consume(Terminal.ENDFUN)));
        return  funDecl;
    }


    /*
    procDecl::= PROC IDENT paramList [GLOBAL globImps] [LOCAL cpsStoDecl] DO cpsCmd ENDPROC
    */
    private ImlComponent procDecl() {
        ImlComponent procDecl = new ImlComposite("funDecl");
        procDecl.add(new ImlItem(consume(Terminal.PROC)));
        procDecl.add(new ImlItem(consume(Terminal.IDENT)));
        procDecl.add(paramList());
        if (this.tokenTerminal == Terminal.GLOBAL){
            procDecl.add(new ImlItem(consume(Terminal.GLOBAL)));
            procDecl.add(globImps());
        }
        if (this.tokenTerminal == Terminal.LOCAL){
            procDecl.add(new ImlItem(consume(Terminal.LOCAL)));
            procDecl.add(cpsStoDecl());
        }
        procDecl.add(new ImlItem(consume(Terminal.DO)));
        procDecl.add(cpsCmd());
        procDecl.add(new ImlItem(consume(Terminal.ENDPROC)));
        return procDecl;
    }
    /*
    globImps ::= globImp {COMMA globImp}
    */
    private ImlComponent globImps(){
        ImlComponent globImps = new ImlComposite("globImps");
        globImps.add(globImp());
        while (this.tokenTerminal == Terminal.COMMA){
            globImps.add(new ImlItem(consume(Terminal.COMMA)));
            globImps.add(globImp());
        }
        return globImps;
    }

    /*
    globImp ::= [FLOWMODE] [CHANGEMODE] IDENT
    */
    private ImlComponent globImp() {
        ImlComponent globImp = new ImlComposite("globImp");
        if (this.tokenTerminal == Terminal.FLOWMODE) {
            globImp.add(new ImlItem(consume(Terminal.FLOWMODE)));
        }
        if (this.tokenTerminal == Terminal.CHANGEMODE) {
            globImp.add(new ImlItem(consume(Terminal.CHANGEMODE)));
        }
        globImp.add(new ImlItem(consume(Terminal.IDENT)));
        return globImp;
    }

    /*
    cpsDecl ::= decl {SEMICOLON decl}
    */
    private ImlComponent cpsDecl(){
        ImlComponent cpsDecl = new ImlComposite("cpsDecl");
        cpsDecl.add(decl());
        while(this.tokenTerminal == Terminal.SEMICOLON){
            cpsDecl.add(new ImlItem(consume(Terminal.SEMICOLON)));
            cpsDecl.add(decl());
        }
        return cpsDecl;
    }

    /*
    cpsStoDecl ::= stoDecl {SEMICOLON stoDecl}
    */
    private ImlComponent cpsStoDecl(){
        ImlComponent cpsStoDecl = new ImlComposite("cpsStoDecl");
        cpsStoDecl.add(stoDecl());
        while(this.tokenTerminal == Terminal.SEMICOLON){
            cpsStoDecl.add(new ImlItem(consume(Terminal.SEMICOLON)));
            cpsStoDecl.add(stoDecl());
        }
        return cpsStoDecl;
    }
}
