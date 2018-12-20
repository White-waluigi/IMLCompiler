package imlcompiler.Symboltable;

public class Symbol {

    //public enum Type{ BOOL, INT32, TUP};

    String name;
    String type;
    int tupSize;
    int location;

    public Symbol(String name, String type, int tupSize, int location) {
        this.name = name;
        this.type = type;
        this.tupSize = tupSize;  //default: -1
        this.location = location;
    }

    public String toString(){
        return "{" + name + " , type: " + type + " , tupSize: " + tupSize + " , location: " + location + "}";
    }
}
