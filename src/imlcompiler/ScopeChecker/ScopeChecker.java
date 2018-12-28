package imlcompiler.ScopeChecker;

import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlItem;
import imlcompiler.Scanner.Token;
import imlcompiler.Symboltable.Symbol;
import imlcompiler.Symboltable.SymbolMap;
import imlcompiler.Symboltable.Type;

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
        int tupSize = - 1;

        Type currentType = null;

        while (iterator.hasNext()) {

            ImlComponent imlComponent = iterator.next();

            Token token = null;

            if (imlComponent instanceof ImlItem) {

                token = ((ImlItem) imlComponent).getToken();

                tokenList.add(token);

                switch (token.getTerminal()) {

                    case GLOBAL:
                        //currentMap = symbolTableGlobal;
                        if (isTuple && previousToken == TYPE ){
                            currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location);
                            isTuple = false;
                            location += currentType.size();
                            currentType = null;
                        }
                        previousToken = GLOBAL;
                        break;
                    case IDENT:
                        identifier = token.getDebugString();
                        if (previousToken == PROC || previousToken == FUN){
                            SymbolMap newMap = new SymbolMap(identifier, currentMap);
                            newMap.addGlobals(currentMap);
                            currentMap.next.add(newMap);
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
                            currentMap.addSymbol(identifier, type, tupSize, location++);
                            type = null;
                        }
                        else {
                            currentType.add(type);
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
                        currentType = new Type();
                        previousToken = TUP;
                        break;
                    default:
                        if (isTuple && previousToken == TYPE && token.getTerminal() != TYPE){
                            currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location);
                            isTuple = false;
                            location += currentType.size();
                            currentType = null;
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
