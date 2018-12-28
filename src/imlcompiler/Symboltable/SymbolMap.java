package imlcompiler.Symboltable;

import imlcompiler.ScopeChecker.ScopeCheckerErrorException;

import java.util.*;

public class SymbolMap {

    private HashMap<String, Symbol> hashMap;
    public String tableName;

    protected SymbolMap prev;
    public ArrayList<SymbolMap> next;

    public SymbolMap(String tableName, SymbolMap prev) {
        this.tableName = tableName;
        this.hashMap = new HashMap<>();
        this.prev = prev;
        this.next = new ArrayList<>();
    }

    public void addSymbol(String name, String type, int tupSize, int location) {
        if (this.hashMap.containsKey(name)) {
            System.out.println("ERROR: " +  name + " already in Symboltable " + tableName );
            throw new ScopeCheckerErrorException(name + " already in Symboltable " + tableName);
        }
        else this.hashMap.put(name, new Symbol(name, type, tupSize, location));
    }
    
    public int getSize() {
    	int r=0;
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            r+=((Symbol)pair.getValue()).tupSize!=-1?((Symbol)pair.getValue()).tupSize:1;
          
        }
    	return r;
    }
    public Symbol get(String s) {
    	return hashMap.get(s);
    }
    public void print(){
        System.out.println("Symboltable: " + tableName);
        Comparator<Symbol> valueComparator = new Comparator<Symbol>() {
            @Override
            public int compare(Symbol e1, Symbol e2) {
                int v1 = e1.location;
                int v2 = e2.location;
                return v1 >= v2 ? 1 : - 1;
            }
        };
        List<Symbol> listOfEntries = new ArrayList<Symbol>(hashMap.values());
        Collections.sort(listOfEntries, valueComparator);
        for(Symbol s : listOfEntries){
            System.out.println("{ " + s.name + " :  type: " + s.type + ", tupelsize: " + s.tupSize + " ,location:  " + s.location + " }");
        }
        for (SymbolMap map : next)
            if(map != null) map.print();
    }

}
