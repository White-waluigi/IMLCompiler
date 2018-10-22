package imlcompiler.Parser;

import java.util.Iterator;

public class NullIterator implements Iterator {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public ImlComponent next() {
        return null;
    }
}
