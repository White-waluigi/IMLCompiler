package imlcompiler.Parser;

import java.util.Iterator;

public abstract class ImlComponent {

    public void add(ImlComponent imlComponent){
        throw new UnsupportedOperationException();
    }

    public void remove(ImlComponent imlComponent){
        throw new UnsupportedOperationException();
    }

    public ImlComponent getChild(int i){
        throw new UnsupportedOperationException();
    }

    public ImlComponent toAbstract() { throw new UnsupportedOperationException(); }

    //other methods
    public String getName(){
        throw new UnsupportedOperationException();
    }

    public void print(){
        throw new UnsupportedOperationException();
    }

    public Iterator<ImlComponent> createIterator(){
        throw new UnsupportedOperationException();
    }
}
