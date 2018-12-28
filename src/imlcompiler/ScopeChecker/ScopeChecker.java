package imlcompiler.ScopeChecker;

import imlcompiler.Parser.ImlComponent;
import imlcompiler.Parser.ImlItem;
import imlcompiler.Scanner.Token;
import imlcompiler.Symboltable.Symbol;
import imlcompiler.Symboltable.SymbolMap;
import imlcompiler.Symboltable.Type;
import java.util.ArrayList;
import java.util.Iterator;

import static imlcompiler.Scanner.Token.EnumAttribute.REF;
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
        boolean isRef = false;

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
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, true);
                            else
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, false);

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
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, type, tupSize, location++, isRef, true);
                            else
                                currentMap.addSymbol(identifier, type, tupSize, location++, isRef, false);
                            type = null;
                            isRef = false;
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
                    case MECHMODE:
                        if (((Token.OtherAttribute)token.getAttribute()).value == REF){
                            isRef = true;
                        }
                        break;
                    default:
                        if (isTuple && previousToken == TYPE && token.getTerminal() != TYPE){
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, true);
                            else
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, false);
                            isTuple = false;
                            location += currentType.size();
                            currentType = null;
                            isRef = false;
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
