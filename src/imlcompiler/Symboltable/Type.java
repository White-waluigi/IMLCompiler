package imlcompiler.Symboltable;

import java.util.ArrayList;

public class Type{
    ArrayList<String> typeList;
    public Type(){
        typeList = new ArrayList<>();
    }
    public void add(String s){
        typeList.add(s);
    }
    public String toString(){
        return typeList.toString();
    }
    public int size(){
        return typeList.size();
    }
}