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

import static net.lax1dude.eaglercraft.backend.server.base.collect.Containers.*;
import static net.lax1dude.eaglercraft.backend.server.base.collect.HashContainers.*;

import net.lax1dude.eaglercraft.backend.server.api.collect.*;

/** An identity hash map of <code>Object</code> to <code>Object</code>. */
public class ObjectObjectIdentityHashMap<KType, VType> extends ObjectObjectHashMap<KType, VType> {
	/** New instance with sane defaults. */
	public ObjectObjectIdentityHashMap() {
		this(DEFAULT_EXPECTED_ELEMENTS);
	}

	/**
	 * New instance with sane defaults.
	 *
	 * @param expectedElements The expected number of elements guaranteed not to
	 *                         cause buffer expansion (inclusive).
	 */
	public ObjectObjectIdentityHashMap(int expectedElements) {
		this(expectedElements, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * New instance with the provided defaults.
	 *
	 * @param expectedElements The expected number of elements guaranteed not to
	 *                         cause a rehash (inclusive).
	 * @param loadFactor       The load factor for internal buffers. Insane load
	 *                         factors (zero, full capacity) are rejected by
	 *                         {@link #verifyLoadFactor(double)}.
	 */
	public ObjectObjectIdentityHashMap(int expectedElements, double loadFactor) {
		super(expectedElements, loadFactor);
	}

	/** Create a hash map from all key-value pairs of another container. */
	public ObjectObjectIdentityHashMap(ObjectObjectAssociativeContainer<? extends KType, ? extends VType> container) {
		this(container.size());
		putAll(container);
	}

	@Override
	public int hashKey(KType key) {
		assert !((key) == null); // Handled as a special case (empty slot marker).
		return BitMixer.mixPhi(System.identityHashCode(key));
	}

	@Override
	public boolean equals(Object v1, Object v2) {
		return v1 == v2;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean equalElements(ObjectObjectHashMap<?, ?> other) {
		if (other.size() != size()) {
			return false;
		}

		for (ObjectObjectCursor<?, ?> c : other) {
			KType key = (KType) c.key;
			if (!containsKey(key) || !equals(c.value, get(key))) { // Compare values using the same function as keys.
				return false;
			}
		}

		return true;
	}

	/** Creates a hash map from two index-aligned arrays of key-value pairs. */
	public static <KType, VType> ObjectObjectIdentityHashMap<KType, VType> from(KType[] keys, VType[] values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
		}

		ObjectObjectIdentityHashMap<KType, VType> map = new ObjectObjectIdentityHashMap<>(keys.length);
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}

		return map;
	}
}
