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
 * A cursor over entries of an associative container (Object keys and int
 * values).
 */
public final class ObjectIntCursor<KType> {
	/**
	 * The current key and value's index in the container this cursor belongs to.
	 * The meaning of this index is defined by the container (usually it will be an
	 * index in the underlying storage buffer).
	 */
	public int index;

	/** The current key. */
	public KType key;

	/** The current value. */
	public int value;

	@Override
	public String toString() {
		return "[cursor, index: " + index + ", key: " + key + ", value: " + value + "]";
	}
}
