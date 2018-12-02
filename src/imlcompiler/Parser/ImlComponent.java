package imlcompiler.Parser;

import imlcompiler.Parser.treeVisualisation.Tree;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class ImlComponent implements Tree {

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

    public int size(){
        throw new UnsupportedOperationException();
    }

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



    public class Node<K extends Comparable<? super K>, E> implements Tree.Node<K, E> {
        final K key;
        E element;

        ArrayList<Node> imlNodesList;

        int pointer = 0;

        Node(K key, E element) {
            this.key = key;
            this.element = element;
            imlNodesList = new ArrayList<>();
        }

        /*
         * (non-Javadoc)
         *
         * @see Tree.Node#getKey()
         */
        @Override
        public K getKey() {
            return key;
        }


        public Tree.Node<K, E> getNext() {
            if (imlNodesList.size() > pointer) {

                return imlNodesList.get(pointer++);

            }
            else {
                pointer = 0;
                return getNext();
            }
        }

        public int getNumberOfChildren(){
            return imlNodesList.size();
        }

    }




}
