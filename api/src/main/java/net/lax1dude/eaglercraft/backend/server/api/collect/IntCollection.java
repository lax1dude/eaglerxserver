/*
 * HPPC
 *
 * Copyright (C) 2010-2024 Carrot Search s.c. and contributors
 * All rights reserved.
 *
 * Refer to the full license file "LICENSE.txt":
 * https://github.com/carrotsearch/hppc/blob/master/LICENSE.txt
 */
package net.lax1dude.eaglercraft.backend.server.api.collect;

/**
 * A collection allows basic, efficient operations on sets of elements
 * (difference and intersection).
 */
public interface IntCollection extends IntContainer {
	/**
	 * Removes all occurrences of <code>e</code> from this collection.
	 *
	 * @param e Element to be removed from this collection, if present.
	 * @return The number of removed elements as a result of this call.
	 */
	public int removeAll(int e);

	/**
	 * Removes all elements in this collection that are present in <code>c</code>.
	 *
	 * @return Returns the number of removed elements.
	 */
	public int removeAll(IntLookupContainer c);

	/**
	 * Removes all elements in this collection for which the given predicate returns
	 * <code>true</code> .
	 *
	 * @return Returns the number of removed elements.
	 */
	public int removeAll(IntPredicate predicate);

	/**
	 * Keeps all elements in this collection that are present in <code>c</code>.
	 * Runs in time proportional to the number of elements in this collection.
	 * Equivalent of sets intersection.
	 *
	 * @return Returns the number of removed elements.
	 */
	public int retainAll(IntLookupContainer c);

	/**
	 * Keeps all elements in this collection for which the given predicate returns
	 * <code>true</code>.
	 *
	 * @return Returns the number of removed elements.
	 */
	public int retainAll(IntPredicate predicate);

	/**
	 * Removes all elements from this collection.
	 *
	 * @see #release()
	 */
	public void clear();

	/**
	 * Removes all elements from the collection and additionally releases any
	 * internal buffers. Typically, if the object is to be reused, a simple
	 * {@link #clear()} should be a better alternative since it'll avoid
	 * reallocation.
	 *
	 * @see #clear()
	 */
	public void release();
}
