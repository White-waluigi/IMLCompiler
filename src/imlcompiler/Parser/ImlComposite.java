package imlcompiler.Parser;

import imlcompiler.Codegenerator.CodeGenerationException;
import imlcompiler.Parser.treeVisualisation.Wrapper;
import imlcompiler.Scanner.Token;
import imlcompiler.Scanner.Token.Terminal;

import java.util.ArrayList;
import java.util.Iterator;

public class ImlComposite extends ImlComponent {

	ArrayList<ImlComponent> imlComponents = new ArrayList<>();
	String name;

	Iterator<ImlComponent> iterator = null;;
	Wrapper wrapper;

	public ImlComposite(String name) {
		this.name = name;
	}

	public ImlComposite(String name, Wrapper wrapper) {
		this.wrapper = wrapper;
		this.name = name;
		wrapper.wrapperArray[wrapper.level] = new Node(this.name + "", this);
		wrapper.level++;
	}

	public void add(ImlComponent imlComponent) {
		imlComponents.add(imlComponent);
		for (Node n : wrapper.wrapperArray) {
			if (n != null && n.getKey().equals(this.name)) {
				n.imlNodesList.add(new Node(imlComponent.getName(), imlComponent));
			}
		}
	}

	public void remove(ImlComponent imlComponent) {
		imlComponents.remove(imlComponent);
	}

	public ImlComponent getChild(int i) {
		return imlComponents.get(i);
	}	
	public ImlComponent getChild(Terminal t) {
		for(ImlComponent x:imlComponents) {
			if(x.getToken()!=null&& x.getToken().getTerminal()==t) {
				return x;
			}
		}
		throw new CodeGenerationException("Terminal "+t+" not found in "+getName());
	}
	public ImlComponent getChild(String s) {
		for(ImlComponent a:imlComponents) {
			if(a.getName().equals(s)) {
				return a;
			}
		}
		throw new CodeGenerationException(s+" not found in  composite "+getName());
	}
	public ArrayList<ImlComponent> getImlComponents() {
		return imlComponents;
	}

	public void print() {
		System.out.println(this.name);
	}

	public void setName(String s) {
		this.name = s;
	}

	public String getName() {
		return this.name;
	}

	public Iterator<ImlComponent> createIterator() {
		if (iterator == null) {
			iterator = new CompositeIterator(imlComponents.iterator());
		}
		return iterator;
	}

//public boolean destructable() {
//	switch (getName()) {
//	case "typedIdent":
//	case "term1":
//	case "term2":
//	case "term3":
//	case "factor":
//	case "expr":
//		return true;
//	}
//
//	return false;
//}
	public boolean destructable() {

		switch (getName()) {
		case "cpsCmd":
		case "exprList":	
			return false;
		}
		return true;
	}
	public ImlComponent toAbstract() {

		ImlComponent abstractComposite = new ImlComposite(this.getName(), wrapper);
		Iterator<ImlComponent> iterator = this.createIterator();
		int ctr = 0;

		ImlComponent last = null;
		while (iterator.hasNext()) {
			ImlComponent next = iterator.next();


			ImlComponent component = next.toAbstract();

			if (component != null) {
				abstractComposite.add(component);
				last = component;
				ctr++;

			}

		}
//
        iterator =  new CompositeIterator(imlComponents.iterator());
        boolean merge=false;
        while(iterator.hasNext()) {
            ImlComponent c = iterator.next();
            if(c instanceof ImlItem&& (         ((ImlItem)c).token.isOperator()  ||((ImlItem)c).token.isDecl()      )   && (((ImlItem)c).token.getTerminal()!=Terminal.INIT)) {
            	((ImlComposite)abstractComposite).setName(((ImlItem)c).token.toShortString());
//            	if(((ImlItem)c).token==null)
//            		throw new ParserErrorException("missing Token");
//            		
            	abstractComposite.token=((ImlItem)c).token;
            	merge=true;
            }   
        }
        
        if(ctr==1&&((ImlComposite)abstractComposite).destructable()) {
        		if(merge ) {
        			if(last instanceof ImlComposite && ((ImlComposite)last).getName()==null) {
        				//((ImlComposite)last).setName(((ImlComposite)abstractComposite).getName());
                    	
        				last.token=abstractComposite.token;
        				abstractComposite= last;
        			}
        			
        		}else {

        			abstractComposite= last;

        		}
        	
        }else if(merge) {
        	
        }
        if((abstractComposite).getName()==null)
        	throw new ParserErrorException("AST Element not properly identifiable");

		
		return abstractComposite;
	}
	@Override
	public int size() {
		return imlComponents.size(); 
	}
}
