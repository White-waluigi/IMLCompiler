package imlcompiler.Parser;

import imlcompiler.Parser.treeVisualisation.Wrapper;
import imlcompiler.Scanner.ScannerErrorException;
import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.Token.Terminal;
import imlcompiler.Scanner.TokenList;

public class Parser {

    TokenList tokenList;
    Token token;
    Terminal tokenTerminal;
    Wrapper wrapper;

    public Parser(TokenList tl){
        this.tokenList = tl;
        this.tokenList.reset();
        this.token = this.tokenList.nextToken();
        this.tokenTerminal = this.token.getTerminal();
    }

    public ImlComponent parse(Wrapper wrapper) throws ScannerErrorException {
        this.wrapper = wrapper;
        // parsing the start symbol ...
        ImlComponent program = program();
        // ... and then consuming the SENTINEL
        consume(Terminal.SENTINEL);
        return program;
    }

    private Token consume(Terminal terminal) throws ScannerErrorException {
    	String s="";
    	
    	for (int i = Thread.currentThread().getStackTrace().length-4; i >=2 ; i--) {
			s+=">"+Thread.currentThread().getStackTrace()[i].getMethodName();
			
			
		}
        System.out.println("Consuming: "+ this.token+"\t"+s);
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
        
        	for(StackTraceElement a:Thread.currentThread().getStackTrace()) {
        		System.out.print(a.getMethodName()+"<-");
        	}
        	System.out.println("\n"+this.token.getDebugString());
        	
            throw new ParserErrorException("terminal expected: " + terminal +
                    ", terminal found: " + this.tokenTerminal + "  " + this.token.toString() +"\t\""+token.debugString+"\"");
        }
    }

    /*
    program ::= PROGRAM IDENT progParamList [GLOBAL cpsDecl] DO cpsCmd ENDPROGRAM
    */
    private ImlComponent program(){

        ImlComponent program = new ImlComposite("program", wrapper);

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

    */

    private ImlComponent progParamList(){
        ImlComponent progParamList = new ImlComposite("progParamList", wrapper);
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
        ImlComponent progParam = new ImlComposite("progParam", wrapper);
        if (this.tokenTerminal == Terminal.FLOWMODE){
            progParam.add(new ImlItem(consume(Terminal.FLOWMODE)));
        }
        if (this.tokenTerminal == Terminal.CHANGEMODE){
            progParam.add(new ImlItem(consume(Terminal.CHANGEMODE)));
        }
        progParam.add(typedIdent());
        return progParam;
    }

    /*
        typedIdent ::= IDENT COLON ATOMTYPE
			        |  TUP IDENT COLON tupeltype

        tupeltype ::= TUP LPAREN tupeltypelist RPAREN

        tupeltypelist ::= ATOMTYPE {COMMA tupeltypelist}
				    | tupeltype {COMMA tupeltypelist}

     */


    private ImlComponent typedIdent(){
        ImlComponent typedIdent = new ImlComposite("typedIdent", wrapper);
        if (this.tokenTerminal == Terminal.IDENT) {
            typedIdent.add(new ImlItem(consume(Terminal.IDENT)));
            typedIdent.add(new ImlItem(consume(Terminal.COLON)));
            typedIdent.add(new ImlItem(consume(Terminal.TYPE)));   //shold be ATOMTYPE
        }
        else if (this.tokenTerminal == Terminal.TUP){
            typedIdent.add(new ImlItem(consume(Terminal.TUP)));
            typedIdent.add(new ImlItem(consume(Terminal.IDENT)));
            typedIdent.add(new ImlItem(consume(Terminal.COLON)));
            typedIdent.add(tupeltype());
        }
        return typedIdent;
    }

    private ImlComponent tupeltype(){
        ImlComponent tupeltype = new ImlComposite("tupeltype", wrapper);
        //tupeltype.add(new ImlItem(consume(Terminal.TUP)));
        tupeltype.add(new ImlItem(consume(Terminal.LPAREN)));
        tupeltype.add(tupeltypelist());
        tupeltype.add(new ImlItem(consume(Terminal.RPAREN)));
        return tupeltype;
    }

    private ImlComponent tupeltypelist(){
        ImlComponent tupeltypelist = new ImlComposite("tupeltypelist", wrapper);
        if (this.tokenTerminal == Terminal.TYPE) {
            tupeltypelist.add(new ImlItem(consume(Terminal.TYPE)));
            while (this.tokenTerminal == Terminal.COMMA) {
                tupeltypelist.add(new ImlItem(consume(Terminal.COMMA)));
                tupeltypelist.add(tupeltypelist());
            }
        }
        else if (this.tokenTerminal == Terminal.LPAREN){
            tupeltypelist.add(tupeltype());
            while(this.tokenTerminal == Terminal.COMMA){
                tupeltypelist.add(new ImlItem(consume((Terminal.COMMA))));
                tupeltypelist.add(tupeltypelist());
            }
        }
        else {
            throw new ParserErrorException("Error in tupeltypelist: Syntax of Declaration");
        }
        return tupeltypelist;
    }


    private ImlComponent paramList(){
        ImlComponent paramList = new ImlComposite("paramList", wrapper);
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
        ImlComponent param = new ImlComposite("param", wrapper);
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
        ImlComponent cpsCmd = new ImlComposite("cpsCmd", wrapper);
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
        ImlComponent cmd = new ImlComposite("cmd", wrapper);
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
        ImlComponent globInits = new ImlComposite("globInits", wrapper);
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
        term3  ::= factor {MULTOPR term3}
       
        factor  ::= LITERAL
                    | IDENT [INIT | exprList]
                    | monadicOpr factor
                    | LPAREN expr RPAREN
                    | tupel
                    | index



        exprList ::= LPAREN [expr {COMMA expr}] RPAREN
        monadicOpr ::= NOT | ADDOPR
        */
    private ImlComponent expr(){   // [[DONE]]
        ImlComponent expr = new ImlComposite("expr", wrapper);
        expr.add(term1());
        while (this.tokenTerminal == Terminal.BOOLOPR){
            expr.add(new ImlItem(consume(Terminal.BOOLOPR)));
            expr.add(term1());
        }
        return expr;
    }

    private ImlComponent term1(){  // [[DONE]]
        ImlComponent term1 = new ImlComposite("term1", wrapper);
        term1.add(term2());
        if (this.tokenTerminal == Terminal.RELOPR){
            term1.add(new ImlItem(consume(Terminal.RELOPR)));
            term1.add(term2());
        }
        return term1;
    }

    private ImlComponent term2(){ //[[DONE]]
        ImlComponent term2 = new ImlComposite("term2", wrapper);

        term2.add(term3());
        
        while(this.tokenTerminal == Terminal.ADDOPR){
            term2.add(new ImlItem(consume(Terminal.ADDOPR)));
            term2.add(term3());
            //term3();
        }
        return term2;
    }

    private ImlComponent term3(){ //[[DONE]]
        ImlComponent term3 = new ImlComposite("term3", wrapper);
        term3.add(factor());

        while(this.tokenTerminal == Terminal.MULTOPR){
            term3.add(new ImlItem(consume(Terminal.MULTOPR)));
            /*term3.add(term3());
            return null;*/
            term3.add(factor());
        }
        // return null;
        return term3;
    }
    /*
        factor  ::=   LITERAL
                    | IDENT [INIT | exprList]
                    | monadicOpr factor
                    | LPAREN expr RPAREN
                    | tupel
                    | index {index}
    */
    private ImlComponent factor(){
        ImlComponent factor = new ImlComposite("factor", wrapper);
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
            else if (this.tokenTerminal == Terminal.LBRACK){
                factor.add(index());
                while(this.tokenTerminal == Terminal.LBRACK){
                    factor.add(index());
                }
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
        else if (this.tokenTerminal == Terminal.TUP){
            factor.add(tupel());
        }
        else if(this.tokenTerminal == Terminal.LBRACK) {
        	factor.add(index());
        }
        else{}
        return factor;
    }
    //exprList ::= LPAREN [expr {COMMA expr}] RPAREN
    private ImlComponent exprList(){ //[[DONE]]
        ImlComponent exprList = new ImlComposite("exprList", wrapper);
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
        ImlComponent monadicOpr = new ImlComposite("monadicOpr" ,wrapper);
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
        ImlComponent decl = new ImlComposite("decl", wrapper);
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
            throw new ParserErrorException("error in decl: " + this.tokenTerminal);
        }
        return decl;
    }

    /*
    stoDecl ::= [CHANGEMODE] typedIdent
    */
    private ImlComponent stoDecl(){
        ImlComponent stoDecl = new ImlComposite("stoDecl", wrapper);
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
        ImlComponent funDecl = new ImlComposite("funDecl" , wrapper);
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
        ImlComponent procDecl = new ImlComposite("funDecl" , wrapper);
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
        ImlComponent globImps = new ImlComposite("globImps" , wrapper);
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
        ImlComponent globImp = new ImlComposite("globImp", wrapper);
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
        ImlComponent cpsDecl = new ImlComposite("cpsDecl", wrapper);
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
        ImlComponent cpsStoDecl = new ImlComposite("cpsStoDecl", wrapper);
        cpsStoDecl.add(stoDecl());
        while(this.tokenTerminal == Terminal.SEMICOLON){
            cpsStoDecl.add(new ImlItem(consume(Terminal.SEMICOLON)));
            cpsStoDecl.add(stoDecl());
        }
        return cpsStoDecl;
    }

    //TUPEL
    /*
    tupel ::= TUP LPAREN tail RPAREN
    tail::=  element { COMMA element }
    element ::= IDENT {index}
               | LITERAL  //should also allow expr (expressions)
               | tupel
    index ::= LBRACK LITERAL RBRACK
    */
    private ImlComponent tupel(){
        ImlComponent tupel = new ImlComposite("tupel" , wrapper);
        tupel.add(new ImlItem(consume(Terminal.TUP)));
        tupel.add(new ImlItem(consume(Terminal.LPAREN)));
        tupel.add(tail());
        tupel.add(new ImlItem(consume(Terminal.RPAREN)));
        return tupel;
    }

    private ImlComponent tail(){
        ImlComponent tail = new ImlComposite("tail" , wrapper);
        tail.add(element());
        while(this.tokenTerminal == Terminal.COMMA){
            tail.add(new ImlItem(consume(Terminal.COMMA)));
            tail.add(element());
        }
        return tail;
    }

    private ImlComponent element(){
        ImlComponent element = new ImlComposite("element" ,wrapper);
        if (this.tokenTerminal == Terminal.IDENT){
            element.add(new ImlItem(consume(Terminal.IDENT)));
            while (this.tokenTerminal == Terminal.LBRACK){
                element.add(index());
            }
        }
        else if (this.tokenTerminal == Terminal.LITERAL){
            element.add(new ImlItem(consume(Terminal.LITERAL)));
        }
        else if (this.tokenTerminal == Terminal.TUP){
            element.add(tupel());
        }
        else {
            throw new ParserErrorException("error in Tupel statement");
        }
        return element;
    }

    private ImlComponent index(){
        ImlComponent index = new ImlComposite("index" , wrapper);
        index.add(new ImlItem(consume(Terminal.LBRACK)));
        index.add(new ImlItem(consume(Terminal.LITERAL)));
        index.add(new ImlItem(consume(Terminal.RBRACK)));
        return index;
    }







}
