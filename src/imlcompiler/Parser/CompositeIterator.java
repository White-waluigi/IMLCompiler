package imlcompiler.Parser;

import java.util.Iterator;
import java.util.Stack;

public class CompositeIterator implements Iterator {

    Stack<Iterator<ImlComponent>> stack = new Stack<>();

    public CompositeIterator(Iterator iterator){
        stack.push(iterator);
    }


    @Override
    public boolean hasNext() {
        if (stack.empty()){
            return false;
        }
        else {
            Iterator<ImlComponent> iterator = stack.peek();
            if (!iterator.hasNext()){
                stack.pop();
                return hasNext();
            }
            else {
                return true;
            }
        }
    }

    @Override
    public Object next() {
        if (hasNext()){
            Iterator<ImlComponent> iterator = stack.peek();
            ImlComponent component = iterator.next();
            stack.push(component.createIterator());
            return component;
        }
        else {
            return null;
        }
    }

}
