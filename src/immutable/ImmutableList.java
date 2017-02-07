/**
 * An immutable list interface
 * Designed for illustrating reasoning about immutable types
 * 
 * Copyright 2007 Daniel Jackson and MIT
 */
package immutable;

import java.util.Iterator;

public interface ImmutableList<E> extends Iterable<E> {
    /**
     * @param e
     *            element to add
     * @requires e != null
     * @return [e,e_0,...,e_n] where this list = [e_0,...,e_n]
     */
    public ImmutableList<E> add(E e);

    /**
     * Get first element of this list.
     * 
     * @requires this list is nonempty
     * @return e_0 where this list = [e_0,...,e_n]
     */
    public E first();

    /**
     * Get list of all elements of this list except for the first.
     * 
     * @requires this list is nonempty
     * @return [e_1,...,e_n] where this list = [e_0,...,e_n]
     */
    public ImmutableList<E> rest();

    /**
     * Remove the first occurrence of an element from the list, if present.
     * 
     * @requires e != null
     * @return [e0,..,e_{i-1], e_{i+1},..,e_n] where i is the minimum index such
     *         that e_i.equals(e); if no such i, then returns [e_0,..,e_n]
     *         unchanged.
     */
    public ImmutableList<E> remove(E e);

    /**
     * @requires e != null
     * @return exists i such that e_i.equals(e) where e_i is ith element of this
     */
    public boolean contains(E e);

    /**
     * @return number of elements in this
     */
    public int size();

    /**
     * @return true if this contains no elements
     */
    public boolean isEmpty();

    /**
     * see Iterable.iterator()
     */
    @Override
    public Iterator<E> iterator();

}
