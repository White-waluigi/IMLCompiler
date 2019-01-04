package imlcompiler.Symboltable;

import java.util.ArrayList;

public class Symbol {

    //public enum Type{ BOOL, INT32, TUP};

    public String name;
    public String type;
    public int tupSize;
    public int location;
    public boolean isRef;
    public boolean isGlobal;
    public Type tupelTypes;

    public Symbol(String name, String type, int tupSize, int location, boolean isRef, boolean isGlobal, Type tupelTypes) {
        this.name = name;
        this.type = type;
        this.tupSize = tupSize;  //default: -1
        this.location = location;
        this.isRef = isRef;
        this.isGlobal = isGlobal;
        this.tupelTypes = tupelTypes;
    }

    public String toString(){
        return "{" + name + " , type: " + type + " , tupSize: " + tupSize + " , location: " + location + "}";
    }

}
