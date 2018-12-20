package imlcompiler.ScopeChecker;

import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlItem;
import imlcompiler.Scanner.Token;
import imlcompiler.Symboltable.SymbolMap;

import java.util.ArrayList;
import java.util.Iterator;

import static imlcompiler.Scanner.Token.Terminal.*;

public class ScopeChecker {

    Iterator<ImlComponent> iterator;
    SymbolMap symbolTableGlobal;
    ArrayList<Token> tokenList;

    public ScopeChecker(Iterator<ImlComponent> iterator, SymbolMap symbolTables) {
        this.iterator = iterator;
        this.symbolTableGlobal= symbolTables;
        this.tokenList = new ArrayList<>();
    }

    public void check(){

        int location = 0;
        String identifier = null;
        String type = null;

        SymbolMap currentMap = symbolTableGlobal;

        Token.Terminal previousToken = null;
        boolean isTuple = false;
        int tupSize = 0;

        while (iterator.hasNext()) {

            ImlComponent imlComponent = iterator.next();

            Token token = null;

            if (imlComponent instanceof ImlItem) {

                token = ((ImlItem) imlComponent).getToken();

                tokenList.add(token);

                switch (token.getTerminal()) {

                    case GLOBAL:
                        currentMap = symbolTableGlobal;
                        previousToken = GLOBAL;
                        break;
                    case IDENT:
                        identifier = token.getDebugString();
                        if (previousToken == PROC || previousToken == FUN){
                            SymbolMap newMap = new SymbolMap(identifier, symbolTableGlobal);
                            symbolTableGlobal.next.add(newMap);
                            currentMap = newMap;
                        }
                        if (previousToken == TUP){
                            isTuple = true;
                        }
                        previousToken = IDENT;
                        break;
                    case TYPE:
                        type = token.getDebugString();
                        if (!isTuple) {
                            currentMap.addSymbol(identifier, type, -1, location++);
                            type = null;
                            tupSize = 0;
                        }
                        else {
                            tupSize++;
                        }
                        previousToken = TYPE;
                        break;
                    case PROC:
                        previousToken = PROC;
                        break;
                    case FUN:
                        previousToken = FUN;
                        break;
                    case TUP:
                        isTuple = true;
                        previousToken = TUP;
                        break;
                    default:
                        if (isTuple && tupSize > 0){
                            currentMap.addSymbol(identifier, "<TUPEL>", tupSize, location);
                            isTuple = false;
                            location += tupSize;
                            tupSize = 0;
                        }
                        previousToken = token.getTerminal();
                        break;
                }
            }
        }
        //for debugging
        for (Token t : tokenList){
            System.out.println(t.toString());
        }
    }

}
