package imlcompiler.Parser;

import imlcompiler.Parser.treeVisualisation.Wrapper;

import java.util.ArrayList;
import java.util.Iterator;

public class ImlComposite extends ImlComponent{

    ArrayList<ImlComponent> imlComponents = new ArrayList<>();
    String name;

    Iterator<ImlComponent> iterator = null;;
    Wrapper wrapper;

    public ImlComposite(String name){
        this.name = name;
    }

    public ArrayList<ImlComponent> getImlComponents() {
        return imlComponents;
    }

    public ImlComposite(String name, Wrapper wrapper){
        this.wrapper = wrapper;
        StringBuilder sb = new StringBuilder(name + wrapper.level);
        this.name = sb.toString();
        wrapper.wrapperArray[wrapper.level] = new Node(this.name, this);
        wrapper.level++;
    }

    public void add(ImlComponent imlComponent){
        imlComponents.add(imlComponent);
        for (Node n : wrapper.wrapperArray) {
            if (n != null && n.getKey().equals(this.name)) {
                n.imlNodesList.add(new Node(imlComponent.getName(), imlComponent));
            }
        }
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

    public String getName(){
        return this.name;
    }

    public Iterator<ImlComponent> createIterator(){
        if (iterator==null){
            iterator = new CompositeIterator(imlComponents.iterator());
        }
        return iterator;
    }

    public ImlComponent toAbstract(){
        ImlComponent abstractComposite = new ImlComposite("abstractComposite", wrapper);
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

