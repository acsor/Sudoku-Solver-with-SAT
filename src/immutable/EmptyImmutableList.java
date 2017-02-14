/**
 * An immutable list
 * Designed for illustrating reasoning about immutable types
 * <p>
 * Copyright 2007 Daniel Jackson and MIT
 */
package immutable;

import java.util.Iterator;

public class EmptyImmutableList<E> implements ImmutableList<E> {

	/**
	 * abstraction function A(this) = <>, the empty list
	 *
	 * rep invariant = true
	 */

	public EmptyImmutableList () {
	}

	public ImmutableList<E> add (E e) {
		if (e != null) {
			return new NonEmptyImmutableList<E>(e);
		}
		return this;
	}

	public ImmutableList<E> remove (E e) {
		assert e != null : "EmptyList.remove(null)";
		return this;
	}

	public E first () {
		assert false : "EmptyList.first";
		return null;
	}

	public ImmutableList<E> rest () {
		assert false : "EmptyList.rest";
		return null;
	}

	public boolean contains (E e) {
		assert e != null : "EmptyList.contains(null)";
		return false;
	}

	public int size () {
		return 0;
	}

	public boolean isEmpty () {
		return true;
	}

	public Iterator<E> iterator () {
		return new ImmutableListIterator<E>(this);
	}

	@Override
	public boolean equals (Object o) {
		return o instanceof EmptyImmutableList;
	}

	@Override
	public int hashCode () {
		return 0;
	}

	@Override
	public String toString () {
		return "[]";
	}

}
