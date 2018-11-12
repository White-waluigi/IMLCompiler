package imlcompiler.Parser;

import java.util.ArrayList;
import java.util.Iterator;

public class ImlComposite extends ImlComponent{

    ArrayList<ImlComponent> imlComponents = new ArrayList<>();
    String name;
    Iterator<ImlComponent> iterator = null;

    public ImlComposite(String name){
        this.name = name;
    }

    public void add(ImlComponent imlComponent){
        imlComponents.add(imlComponent);
    }

    public void remove(ImlComponent imlComponent){
        imlComponents.remove(imlComponent);
    }

    public ImlComponent getChild(int i){
        return imlComponents.get(i);
    }

    public void print(){
        System.out.println(this.name);
    }

    public Iterator<ImlComponent> createIterator(){
        if (iterator==null){
            iterator = new CompositeIterator(imlComponents.iterator());
        }
        return iterator;
    }

    public ImlComponent toAbstract(){
        ImlComponent abstractComposite = new ImlComposite("abstractComposite");
        Iterator<ImlComponent> iterator = this.createIterator();
        while (iterator.hasNext()){
            ImlComponent component = iterator.next().toAbstract();
            if (component != null){
                abstractComposite.add(component);
            }
        }
        return abstractComposite;
    }
}
