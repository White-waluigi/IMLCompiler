package imlcompiler.Symboltable;

import java.util.ArrayList;

public class Symbol {

    //public enum Type{ BOOL, INT32, TUP};

    public String name;
    public String type;
    public int tupSize;
    public int location;
    public boolean isRef;

    public Symbol(String name, String type, int tupSize, int location, boolean isRef) {
        this.name = name;
        this.type = type;
        this.tupSize = tupSize;  //default: -1
        this.location = location;
        this.isRef = isRef;
    }

    public String toString(){
        return "{" + name + " , type: " + type + " , tupSize: " + tupSize + " , location: " + location + "}";
    }

}
