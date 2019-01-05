package imlcompiler.Symboltable;

import imlcompiler.Codegenerator.CodeGenerationException;
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

    public void addSymbol(String name, String type, int tupSize, int location, boolean isRef, boolean isGlobal, Type tupelTypes) throws ScopeCheckerErrorException {
        if (this.hashMap.containsKey(name)) {
            System.out.println("ERROR: " +  name + " already in Symboltable " + tableName );
            throw new ScopeCheckerErrorException(name + " already in Symboltable " + tableName);
        }
        else this.hashMap.put(name, new Symbol(name, type, tupSize, location, isRef, isGlobal, tupelTypes));
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
    	Symbol retval= hashMap.get(s);
    	return retval;
    }
    public Symbol get(int s) {
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
        return listOfEntries.get(s);
    }
    public void print(){
        System.out.println("Symboltable: " + tableName);
        for(int i=0;i<hashMap.size();i++){
        	Symbol s = get(i);
            System.out.println("{ " + s.name + " :  type: " + s.type + ", tupelsize: " + s.tupSize
                    + " ,location:  " + s.location + ", isRef: " +s.isRef+" }");
        }
        for (SymbolMap map : next)
            if(map != null) map.print();
    }

    public void addGlobals(SymbolMap symbolMap){
        for (Symbol s: symbolMap.getMap().values()){
            if (s.isGlobal)
                this.hashMap.put(s.name, s);
        }
    }

    private HashMap<String, Symbol> getMap(){
        return hashMap;
    }

	public SymbolMap findTable(String value) {
    	
		SymbolMap retval = null;
    	if(next.isEmpty())
    		throw new CodeGenerationException("No Symboltables generated");
    	
    	ArrayList<SymbolMap> n=next;
    	SymbolMap a=null;
    	int i=0;
    	while(a==null|| !a.next.isEmpty()) {
    		a=n.get(0);

    		if(a.tableName.equals(value)) {
    			retval=a;
    		}
    		System.out.println("not: "+a.tableName);
    		n=a.next;
    		
    	}

    	
    	if(retval==null)
    		throw new CodeGenerationException("Symboltable for: "+value +" not found");
    	
    	return retval;
	}

	public int getNum() {
		return hashMap.size();
	}
}
