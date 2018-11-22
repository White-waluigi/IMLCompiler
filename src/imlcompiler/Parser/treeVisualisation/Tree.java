package imlcompiler.Parser.treeVisualisation;

public interface Tree<K extends Comparable<? super K>, E> {
	/**
	 * Immutable interface to represent nodes of binary trees
	 */
	interface Node<K extends Comparable<? super K>, E> {

		K getKey();

		Node<K, E> getNext();

		int getNumberOfChildren();

	}

}
