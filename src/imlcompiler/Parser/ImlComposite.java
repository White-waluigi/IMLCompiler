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

    public ImlComposite(String name, Wrapper wrapper){
        this.wrapper = wrapper;
        StringBuilder sb = new StringBuilder(name +" "+ wrapper.level);
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

    public ArrayList<ImlComponent> getImlComponents() {
        return imlComponents;
    }

    public void print(){
        System.out.println(this.name);
    }
    public void setName(String s){
        this.name=s;
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
        
    	
    	ImlComponent abstractComposite = new ImlComposite(this.name, wrapper);
        Iterator<ImlComponent> iterator = this.createIterator();
        int ctr=0;

        ImlComponent last=null;
        while (iterator.hasNext()){
        	ImlComponent component = iterator.next().toAbstract();

            if (component != null){
                abstractComposite.add(component);
                last=component;
                ctr++;

            }

            
            
        }


        iterator =  new CompositeIterator(imlComponents.iterator());
        boolean merge=false;
        while(iterator.hasNext()) {
            ImlComponent c = iterator.next();
            if(c instanceof ImlItem&& (((ImlItem)c).token.isOperator()  )) {
            	((ImlComposite)abstractComposite).setName(((ImlItem)c).token.toShortString());
            	merge=true;
            	
            }   
        }
        
        if(ctr==1) {
        		if(merge) {
        			if(last instanceof ImlComposite) {
        				((ImlComposite)last).setName(((ImlComposite)abstractComposite).getName());
        				return last;
        			}
        			
        		}else {
        			return last;
        		}

        }else if(merge) {
        	
        }
    	//((ImlComposite)abstractComposite).setName(ctr+"."+((ImlComposite)abstractComposite).getName());

        return abstractComposite;
    }


}

