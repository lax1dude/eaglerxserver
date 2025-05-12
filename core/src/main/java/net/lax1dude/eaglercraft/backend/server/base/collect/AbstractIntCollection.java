/*
 * HPPC
 *
 * Copyright (C) 2010-2024 Carrot Search s.c. and contributors
 * All rights reserved.
 *
 * Refer to the full license file "LICENSE.txt":
 * https://github.com/carrotsearch/hppc/blob/master/LICENSE.txt
 */
package net.lax1dude.eaglercraft.backend.server.base.collect;

import java.util.Arrays;

import net.lax1dude.eaglercraft.backend.server.api.collect.*;

/** Common superclass for collections. */
abstract class AbstractIntCollection implements IntCollection {
	/** Default implementation uses a predicate for removal. */
	@Override
	public int removeAll(final IntLookupContainer c) {
		return this.removeAll(c::contains);
	}

	/** Default implementation uses a predicate for retaining. */
	@Override
	public int retainAll(final IntLookupContainer c) {
		// We know c holds sub-types of int and we're not modifying c, so go unchecked.
		return this.removeAll(k -> !c.contains(k));
	}

	/**
	 * Default implementation redirects to {@link #removeAll(IntPredicate)} and
	 * negates the predicate.
	 */
	@Override
	public int retainAll(final IntPredicate predicate) {
		return removeAll(value -> !predicate.apply(value));
	}

	/** Default implementation of copying to an array. */
	@Override
	public int[] toArray() {

		int[] array = (new int[size()]);
		int i = 0;
		for (IntCursor c : this) {
			array[i++] = c.value;
		}
		return array;
	}

	/** Convert the contents of this container to a human-friendly string. */
	@Override
	public String toString() {
		return Arrays.toString(this.toArray());
	}
}
