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

    public void check() throws TypeCheckerException, ScopeCheckerErrorException {

        int location = 0;
        String identifier = null;
        String type = null;

        SymbolMap currentMap = symbolTableGlobal;

        Token.Terminal previousToken = null;
        boolean isTuple = false;
        int tupSize = - 1;

        Type currentType = null;
        boolean isRef = false;

        Iterator<ImlComponent> Tempiterator = null;

        boolean directCpsDecl=false;
        
        
        while (iterator.hasNext()|| directCpsDecl) {

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
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, true, currentType);
                            else
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, false, currentType);

                            isTuple = false;
                            location += currentType.size();
                            currentType = null;
                           //System.out.println("--->" + token.getTerminal());
                            isRef = false;
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
                        /*if (previousToken == TUP){
                            isTuple = true;
                        }*/
                        previousToken = IDENT;
                        break;
                    case TYPE:
                        type = token.getDebugString();
                        if (!isTuple) {
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, type, tupSize, location++, isRef, true, currentType);
                            else
                                currentMap.addSymbol(identifier, type, tupSize, location++, isRef, false, currentType);
                            type = null;
                            isRef = false;
                        }
                        else {
                            currentType.add(type);
                        }
                        Symbol s = currentMap.get(identifier);
                        if (s==null && !isTuple){
                            throw new ScopeCheckerErrorException("identifier: "+ identifier + " not in scope");
                        }
                        previousToken = TYPE;
                        break;
                    case LITERAL:

                        Symbol sym = currentMap.get(identifier);
                        boolean IsBool = false;
                        if (token.has(Token.EnumAttribute.FALSE) || token.has(Token.EnumAttribute.TRUE)){
                            IsBool = true;
                            if (isTuple) currentType.add("bool");
                        }
                        boolean isInt = false;
                        if (token.getAttribute() instanceof Token.IntAttribute){
                            isInt = true;
                            if (isTuple) currentType.add("int32");
                        }
                        //if (currentType != null) System.out.println(currentType.toString());
                        if (identifier.equals(currentMap.tableName)){
                            continue;
                        }

                        if (sym.type.equals("bool") && IsBool || sym.type.equals("int32") && isInt ){
                            // type ok for not-tuples
                        }

                        else if(currentType != null && isTuple && currentType.get(currentType.size()-1).equals(sym.tupelTypes.get(currentType.size()-1))){
                            // type ok for tuples
                            if (currentType.size() == sym.tupelTypes.size()) {
                                currentType = new Type();
                                isTuple = false;
                            }
                        }
                        else if( !isTuple && currentType == null || !isTuple && currentType.size() == 0){
                            //skip indexes of tuples
                        }
                        else {
                            throw new TypeCheckerException("identifier: " + identifier + " has wrong type");
                        }
                        previousToken = LITERAL;
                        break;
                    case PROC:
                        previousToken = PROC;
                        break;
                    case FUN:
                        previousToken = FUN;
                        break;
                    case TUP:
                        if (!isTuple) currentType = new Type();
                        if (isTuple && previousToken == TYPE){
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, true, currentType);
                            else
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, false, currentType);

                            location += currentType.size();
                            currentType = new Type();
                            isRef = false;
                        }
                        isTuple = true;
                        previousToken = TUP;
                        break;
                    case MECHMODE:
                        if (((Token.OtherAttribute)token.getAttribute()).value == REF){
                            isRef = true;
                        }
                        break;
                    case DO:
                        if (isTuple && previousToken == TYPE ){
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, true, currentType);
                            else
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, false ,currentType);

                            isTuple = false;
                            location += currentType.size();
                            currentType = null;
                            //System.out.println("--->" + token.getTerminal());
                            isRef = false;
                        }
                    default:
                        if (isTuple && previousToken == TYPE && token.getTerminal() != TYPE){
                            if (currentMap.tableName.equals("global"))
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, true, currentType);
                            else
                                currentMap.addSymbol(identifier, currentType.toString(), currentType.size(), location, isRef, false, currentType);

                            location += currentType.size();
                            currentType = null;
                            isRef = false;
                            isTuple = false;
                            //System.out.println("******>" + token.getTerminal());
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
