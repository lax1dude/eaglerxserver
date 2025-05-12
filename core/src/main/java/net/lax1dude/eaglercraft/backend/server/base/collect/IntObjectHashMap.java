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

import java.util.*;

import net.lax1dude.eaglercraft.backend.server.api.collect.*;

import static net.lax1dude.eaglercraft.backend.server.base.collect.Containers.*;
import static net.lax1dude.eaglercraft.backend.server.base.collect.HashContainers.*;

/**
 * A hash map of <code>int</code> to <code>Object</code>, implemented using open
 * addressing with linear probing for collision resolution. Supports null
 * values.
 *
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces
 *      diagram</a>
 */
@SuppressWarnings("unchecked")
public class IntObjectHashMap<VType> implements IntObjectMap<VType>, Preallocable, Cloneable, Accountable {
	/** The array holding keys. */
	public int[] keys;

	/** The array holding values. */
	public Object[] values;

	/**
	 * The number of stored keys (assigned key slots), excluding the special "empty"
	 * key, if any (use {@link #size()} instead).
	 *
	 * @see #size()
	 */
	protected int assigned;

	/** Mask for slot scans in {@link #keys}. */
	protected int mask;

	/** Expand (rehash) {@link #keys} when {@link #assigned} hits this value. */
	protected int resizeAt;

	/** Special treatment for the "empty slot" key marker. */
	protected boolean hasEmptyKey;

	/** The load factor for {@link #keys}. */
	protected double loadFactor;

	/**
	 * Seed used to ensure the hash iteration order is different from an iteration
	 * to another.
	 */
	protected int iterationSeed;

	/** New instance with sane defaults. */
	public IntObjectHashMap() {
		this(DEFAULT_EXPECTED_ELEMENTS);
	}

	/**
	 * New instance with sane defaults.
	 *
	 * @param expectedElements The expected number of elements guaranteed not to
	 *                         cause buffer expansion (inclusive).
	 */
	public IntObjectHashMap(int expectedElements) {
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
	public IntObjectHashMap(int expectedElements, double loadFactor) {
		this.loadFactor = verifyLoadFactor(loadFactor);
		iterationSeed = HashContainers.nextIterationSeed();
		ensureCapacity(expectedElements);
	}

	/** Create a hash map from all key-value pairs of another container. */
	public IntObjectHashMap(IntObjectAssociativeContainer<? extends VType> container) {
		this(container.size());
		putAll(container);
	}

	/** {@inheritDoc} */
	@Override
	public VType put(int key, VType value) {
		assert assigned < mask + 1;

		final int mask = this.mask;
		if (((key) == 0)) {
			VType previousValue = hasEmptyKey ? (VType) values[mask + 1] : null;
			hasEmptyKey = true;
			values[mask + 1] = value;
			return previousValue;
		} else {
			final int[] keys = this.keys;
			int slot = hashKey(key) & mask;

			int existing;
			while (!((existing = keys[slot]) == 0)) {
				if (((key) == (existing))) {
					final VType previousValue = (VType) values[slot];
					values[slot] = value;
					return previousValue;
				}
				slot = (slot + 1) & mask;
			}

			if (assigned == resizeAt) {
				allocateThenInsertThenRehash(slot, key, value);
			} else {
				keys[slot] = key;
				values[slot] = value;
			}

			assigned++;
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int putAll(IntObjectAssociativeContainer<? extends VType> container) {
		final int count = size();
		for (IntObjectCursor<? extends VType> c : container) {
			put(c.key, c.value);
		}
		return size() - count;
	}

	/** Puts all key/value pairs from a given iterable into this map. */
	@Override
	public int putAll(Iterable<? extends IntObjectCursor<? extends VType>> iterable) {
		final int count = size();
		for (IntObjectCursor<? extends VType> c : iterable) {
			put(c.key, c.value);
		}
		return size() - count;
	}

	/** {@inheritDoc} */
	@Override
	public VType remove(int key) {
		final int mask = this.mask;
		if (((key) == 0)) {
			if (!hasEmptyKey) {
				return null;
			}
			hasEmptyKey = false;
			VType previousValue = (VType) values[mask + 1];
			values[mask + 1] = null;
			return previousValue;
		} else {
			final int[] keys = this.keys;
			int slot = hashKey(key) & mask;

			int existing;
			while (!((existing = keys[slot]) == 0)) {
				if (((key) == (existing))) {
					final VType previousValue = (VType) values[slot];
					shiftConflictingKeys(slot);
					return previousValue;
				}
				slot = (slot + 1) & mask;
			}

			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(IntContainer other) {
		final int before = size();

		// Try to iterate over the smaller set of values or
		// over the container that isn't implementing
		// efficient contains() lookup.

		if (other.size() >= size() && other instanceof IntLookupContainer) {
			if (hasEmptyKey && other.contains(0)) {
				hasEmptyKey = false;
				values[mask + 1] = null;
			}

			final int[] keys = this.keys;
			for (int slot = 0, max = this.mask; slot <= max;) {
				int existing;
				if (!((existing = keys[slot]) == 0) && other.contains(existing)) {
					// Shift, do not increment slot.
					shiftConflictingKeys(slot);
				} else {
					slot++;
				}
			}
		} else {
			for (IntCursor c : other) {
				remove(c.value);
			}
		}

		return before - size();
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(IntObjectPredicate<? super VType> predicate) {
		final int before = size();

		final int mask = this.mask;

		if (hasEmptyKey) {
			if (predicate.apply(0, (VType) values[mask + 1])) {
				hasEmptyKey = false;
				values[mask + 1] = null;
			}
		}

		final int[] keys = this.keys;
		final VType[] values = (VType[]) this.values;
		for (int slot = 0; slot <= mask;) {
			int existing;
			if (!((existing = keys[slot]) == 0) && predicate.apply(existing, values[slot])) {
				// Shift, do not increment slot.
				shiftConflictingKeys(slot);
			} else {
				slot++;
			}
		}

		return before - size();
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(IntPredicate predicate) {
		final int before = size();

		if (hasEmptyKey) {
			if (predicate.apply(0)) {
				hasEmptyKey = false;
				values[mask + 1] = null;
			}
		}

		final int[] keys = this.keys;
		for (int slot = 0, max = this.mask; slot <= max;) {
			int existing;
			if (!((existing = keys[slot]) == 0) && predicate.apply(existing)) {
				// Shift, do not increment slot.
				shiftConflictingKeys(slot);
			} else {
				slot++;
			}
		}

		return before - size();
	}

	/** {@inheritDoc} */
	@Override
	public VType get(int key) {
		if (((key) == 0)) {
			return hasEmptyKey ? (VType) values[mask + 1] : null;
		} else {
			final int[] keys = this.keys;
			final int mask = this.mask;
			int slot = hashKey(key) & mask;

			int existing;
			while (!((existing = keys[slot]) == 0)) {
				if (((key) == (existing))) {
					return (VType) values[slot];
				}
				slot = (slot + 1) & mask;
			}

			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public VType getOrDefault(int key, VType defaultValue) {
		if (((key) == 0)) {
			return hasEmptyKey ? (VType) values[mask + 1] : defaultValue;
		} else {
			final int[] keys = this.keys;
			final int mask = this.mask;
			int slot = hashKey(key) & mask;

			int existing;
			while (!((existing = keys[slot]) == 0)) {
				if (((key) == (existing))) {
					return (VType) values[slot];
				}
				slot = (slot + 1) & mask;
			}

			return defaultValue;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsKey(int key) {
		if (((key) == 0)) {
			return hasEmptyKey;
		} else {
			final int[] keys = this.keys;
			final int mask = this.mask;
			int slot = hashKey(key) & mask;

			int existing;
			while (!((existing = keys[slot]) == 0)) {
				if (((key) == (existing))) {
					return true;
				}
				slot = (slot + 1) & mask;
			}

			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(int key) {
		final int mask = this.mask;
		if (((key) == 0)) {
			return hasEmptyKey ? mask + 1 : ~(mask + 1);
		} else {
			final int[] keys = this.keys;
			int slot = hashKey(key) & mask;

			int existing;
			while (!((existing = keys[slot]) == 0)) {
				if (((key) == (existing))) {
					return slot;
				}
				slot = (slot + 1) & mask;
			}

			return ~slot;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean indexExists(int index) {
		assert index < 0 || (index >= 0 && index <= mask) || (index == mask + 1 && hasEmptyKey);

		return index >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public VType indexGet(int index) {
		assert index >= 0 : "The index must point at an existing key.";
		assert index <= mask || (index == mask + 1 && hasEmptyKey);

		return (VType) values[index];
	}

	/** {@inheritDoc} */
	@Override
	public VType indexReplace(int index, VType newValue) {
		assert index >= 0 : "The index must point at an existing key.";
		assert index <= mask || (index == mask + 1 && hasEmptyKey);

		VType previousValue = (VType) values[index];
		values[index] = newValue;
		return previousValue;
	}

	/** {@inheritDoc} */
	@Override
	public void indexInsert(int index, int key, VType value) {
		assert index < 0 : "The index must not point at an existing key.";

		index = ~index;
		if (((key) == 0)) {
			assert index == mask + 1;
			values[index] = value;
			hasEmptyKey = true;
		} else {
			assert ((keys[index]) == 0);

			if (assigned == resizeAt) {
				allocateThenInsertThenRehash(index, key, value);
			} else {
				keys[index] = key;
				values[index] = value;
			}

			assigned++;
		}
	}

	/** {@inheritDoc} */
	@Override
	public VType indexRemove(int index) {
		assert index >= 0 : "The index must point at an existing key.";
		assert index <= mask || (index == mask + 1 && hasEmptyKey);

		VType previousValue = (VType) values[index];
		if (index > mask) {
			assert index == mask + 1;
			hasEmptyKey = false;
			values[index] = null;
		} else {
			shiftConflictingKeys(index);
		}
		return previousValue;
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		assigned = 0;
		hasEmptyKey = false;

		Arrays.fill(keys, 0);

		Arrays.fill(values, null);
	}

	/** {@inheritDoc} */
	@Override
	public void release() {
		assigned = 0;
		hasEmptyKey = false;

		keys = null;
		values = null;
		ensureCapacity(Containers.DEFAULT_EXPECTED_ELEMENTS);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return assigned + (hasEmptyKey ? 1 : 0);
	}

	/** {@inheritDoc} */
	public boolean isEmpty() {
		return size() == 0;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int h = hasEmptyKey ? 0xDEADBEEF : 0;
		for (IntObjectCursor<VType> c : this) {
			h += BitMixer.mix(c.key) + BitMixer.mix(c.value);
		}
		return h;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null && getClass() == obj.getClass() && equalElements(getClass().cast(obj)));
	}

	/**
	 * Return true if all keys of some other container exist in this container.
	 * Values are compared using {@link Objects#equals(Object)} method.
	 */
	protected boolean equalElements(IntObjectHashMap<?> other) {
		if (other.size() != size()) {
			return false;
		}

		for (IntObjectCursor<?> c : other) {
			int key = c.key;
			if (!containsKey(key) || !java.util.Objects.equals(c.value, get(key))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Ensure this container can hold at least the given number of keys (entries)
	 * without resizing its buffers.
	 *
	 * @param expectedElements The total number of keys, inclusive.
	 */
	@Override
	public void ensureCapacity(int expectedElements) {
		if (expectedElements > resizeAt || keys == null) {
			final int[] prevKeys = this.keys;
			final VType[] prevValues = (VType[]) this.values;
			allocateBuffers(minBufferSize(expectedElements, loadFactor));
			if (prevKeys != null && !isEmpty()) {
				rehash(prevKeys, prevValues);
			}
		}
	}

	@Override
	public long ramBytesAllocated() {
		// int: iterationSeed, assigned, mask, resizeAt
		// double: loadFactor
		// boolean: hasEmptyKey
		return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4 * Integer.BYTES + Double.BYTES + 1
				+ RamUsageEstimator.shallowSizeOfArray(keys) + RamUsageEstimator.shallowSizeOfArray(values);
	}

	@Override
	public long ramBytesUsed() {
		// int: iterationSeed, assigned, mask, resizeAt
		// double: loadFactor
		// boolean: hasEmptyKey
		return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4 * Integer.BYTES + Double.BYTES + 1
				+ RamUsageEstimator.shallowUsedSizeOfArray(keys, size())
				+ RamUsageEstimator.shallowUsedSizeOfArray(values, size());
	}

	/**
	 * Provides the next iteration seed used to build the iteration starting slot
	 * and offset increment. This method does not need to be synchronized, what
	 * matters is that each thread gets a sequence of varying seeds.
	 */
	protected int nextIterationSeed() {
		return iterationSeed = BitMixer.mixPhi(iterationSeed);
	}

	/** An iterator implementation for {@link #iterator}. */
	private final class EntryIterator extends AbstractIterator<IntObjectCursor<VType>> {
		private final IntObjectCursor<VType> cursor;
		private final int increment;
		private int index;
		private int slot;

		public EntryIterator() {
			cursor = new IntObjectCursor<VType>();
			int seed = nextIterationSeed();
			increment = iterationIncrement(seed);
			slot = seed & mask;
		}

		@Override
		protected IntObjectCursor<VType> fetch() {
			final int mask = IntObjectHashMap.this.mask;
			while (index <= mask) {
				int existing;
				index++;
				slot = (slot + increment) & mask;
				if (!((existing = keys[slot]) == 0)) {
					cursor.index = slot;
					cursor.key = existing;
					cursor.value = (VType) values[slot];
					return cursor;
				}
			}

			if (index == mask + 1 && hasEmptyKey) {
				cursor.index = index;
				cursor.key = 0;
				cursor.value = (VType) values[index++];
				return cursor;
			}

			return done();
		}
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<IntObjectCursor<VType>> iterator() {
		return new EntryIterator();
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IntObjectProcedure<? super VType>> T forEach(T procedure) {
		final int[] keys = this.keys;
		final VType[] values = (VType[]) this.values;

		if (hasEmptyKey) {
			procedure.apply(0, (VType) values[mask + 1]);
		}

		int seed = nextIterationSeed();
		int inc = iterationIncrement(seed);
		for (int i = 0, mask = this.mask, slot = seed & mask; i <= mask; i++, slot = (slot + inc) & mask) {
			if (!((keys[slot]) == 0)) {
				procedure.apply(keys[slot], values[slot]);
			}
		}

		return procedure;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IntObjectPredicate<? super VType>> T forEach(T predicate) {
		final int[] keys = this.keys;
		final VType[] values = (VType[]) this.values;

		if (hasEmptyKey) {
			if (!predicate.apply(0, (VType) values[mask + 1])) {
				return predicate;
			}
		}

		int seed = nextIterationSeed();
		int inc = iterationIncrement(seed);
		for (int i = 0, mask = this.mask, slot = seed & mask; i <= mask; i++, slot = (slot + inc) & mask) {
			if (!((keys[slot]) == 0)) {
				if (!predicate.apply(keys[slot], values[slot])) {
					break;
				}
			}
		}

		return predicate;
	}

	/**
	 * Returns a specialized view of the keys of this associated container. The view
	 * additionally implements {@link ObjectLookupContainer}.
	 */
	public KeysContainer keys() {
		return new KeysContainer();
	}

	/** A view of the keys inside this hash map. */
	public final class KeysContainer extends AbstractIntCollection implements IntLookupContainer {
		private final IntObjectHashMap<VType> owner = IntObjectHashMap.this;

		@Override
		public boolean contains(int e) {
			return owner.containsKey(e);
		}

		@Override
		public <T extends IntProcedure> T forEach(final T procedure) {
			owner.forEach((IntObjectProcedure<VType>) (k, v) -> procedure.apply(k));
			return procedure;
		}

		@Override
		public <T extends IntPredicate> T forEach(final T predicate) {
			owner.forEach((IntObjectPredicate<VType>) (key, value) -> predicate.apply(key));
			return predicate;
		}

		@Override
		public boolean isEmpty() {
			return owner.isEmpty();
		}

		@Override
		public Iterator<IntCursor> iterator() {
			return new KeysIterator();
		}

		@Override
		public int size() {
			return owner.size();
		}

		@Override
		public void clear() {
			owner.clear();
		}

		@Override
		public void release() {
			owner.release();
		}

		@Override
		public int removeAll(IntPredicate predicate) {
			return owner.removeAll(predicate);
		}

		@Override
		public int removeAll(final int e) {
			if (owner.containsKey(e)) {
				owner.remove(e);
				return 1;
			} else {
				return 0;
			}
		}
	};

	/** An iterator over the set of assigned keys. */
	private final class KeysIterator extends AbstractIterator<IntCursor> {
		private final IntCursor cursor;
		private final int increment;
		private int index;
		private int slot;

		public KeysIterator() {
			cursor = new IntCursor();
			int seed = nextIterationSeed();
			increment = iterationIncrement(seed);
			slot = seed & mask;
		}

		@Override
		protected IntCursor fetch() {
			final int mask = IntObjectHashMap.this.mask;
			while (index <= mask) {
				int existing;
				index++;
				slot = (slot + increment) & mask;
				if (!((existing = keys[slot]) == 0)) {
					cursor.index = slot;
					cursor.value = existing;
					return cursor;
				}
			}

			if (index == mask + 1 && hasEmptyKey) {
				cursor.index = index++;
				cursor.value = 0;
				return cursor;
			}

			return done();
		}
	}

	/**
	 * @return Returns a container with all values stored in this map.
	 */
	@Override
	public ObjectCollection<VType> values() {
		return new ValuesContainer();
	}

	/** A view over the set of values of this map. */
	private final class ValuesContainer extends AbstractObjectCollection<VType> {
		private final IntObjectHashMap<VType> owner = IntObjectHashMap.this;

		@Override
		public int size() {
			return owner.size();
		}

		@Override
		public boolean isEmpty() {
			return owner.isEmpty();
		}

		@Override
		public boolean contains(VType value) {
			for (IntObjectCursor<VType> c : owner) {
				if (java.util.Objects.equals(value, c.value)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public <T extends ObjectProcedure<? super VType>> T forEach(T procedure) {
			for (IntObjectCursor<VType> c : owner) {
				procedure.apply(c.value);
			}
			return procedure;
		}

		@Override
		public <T extends ObjectPredicate<? super VType>> T forEach(T predicate) {
			for (IntObjectCursor<VType> c : owner) {
				if (!predicate.apply(c.value)) {
					break;
				}
			}
			return predicate;
		}

		@Override
		public Iterator<ObjectCursor<VType>> iterator() {
			return new ValuesIterator();
		}

		@Override
		public int removeAll(final VType e) {
			return owner.removeAll((key, value) -> java.util.Objects.equals(e, value));
		}

		@Override
		public int removeAll(final ObjectPredicate<? super VType> predicate) {
			return owner.removeAll((key, value) -> predicate.apply(value));
		}

		@Override
		public void clear() {
			owner.clear();
		}

		@Override
		public void release() {
			owner.release();
		}
	}

	/** An iterator over the set of assigned values. */
	private final class ValuesIterator extends AbstractIterator<ObjectCursor<VType>> {
		private final ObjectCursor<VType> cursor;
		private final int increment;
		private int index;
		private int slot;

		public ValuesIterator() {
			cursor = new ObjectCursor<VType>();
			int seed = nextIterationSeed();
			increment = iterationIncrement(seed);
			slot = seed & mask;
		}

		@Override
		protected ObjectCursor<VType> fetch() {
			final int mask = IntObjectHashMap.this.mask;
			while (index <= mask) {
				index++;
				slot = (slot + increment) & mask;
				if (!((keys[slot]) == 0)) {
					cursor.index = slot;
					cursor.value = (VType) values[slot];
					return cursor;
				}
			}

			if (index == mask + 1 && hasEmptyKey) {
				cursor.index = index;
				cursor.value = (VType) values[index++];
				return cursor;
			}

			return done();
		}
	}

	/** {@inheritDoc} */
	@Override
	public IntObjectHashMap<VType> clone() {
		try {

			IntObjectHashMap<VType> cloned = (IntObjectHashMap<VType>) super.clone();
			cloned.keys = keys.clone();
			cloned.values = values.clone();
			cloned.hasEmptyKey = hasEmptyKey;
			cloned.iterationSeed = HashContainers.nextIterationSeed();
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/** Convert the contents of this map to a human-friendly string. */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("[");

		boolean first = true;
		for (IntObjectCursor<VType> cursor : this) {
			if (!first) {
				buffer.append(", ");
			}
			buffer.append(cursor.key);
			buffer.append("=>");
			buffer.append(cursor.value);
			first = false;
		}
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public String visualizeKeyDistribution(int characters) {
		return IntBufferVisualizer.visualizeKeyDistribution(keys, mask, characters);
	}

	/** Creates a hash map from two index-aligned arrays of key-value pairs. */
	public static <VType> IntObjectHashMap<VType> from(int[] keys, VType[] values) {
		if (keys.length != values.length) {
			throw new IllegalArgumentException("Arrays of keys and values must have an identical length.");
		}

		IntObjectHashMap<VType> map = new IntObjectHashMap<>(keys.length);
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}

		return map;
	}

	/**
	 * Returns a hash code for the given key.
	 *
	 * <p>
	 * The output from this function should evenly distribute keys across the entire
	 * integer range.
	 */
	protected int hashKey(int key) {
		assert !((key) == 0); // Handled as a special case (empty slot marker).
		return BitMixer.mixPhi(key);
	}

	/**
	 * Validate load factor range and return it. Override and suppress if you need
	 * insane load factors.
	 */
	protected double verifyLoadFactor(double loadFactor) {
		checkLoadFactor(loadFactor, MIN_LOAD_FACTOR, MAX_LOAD_FACTOR);
		return loadFactor;
	}

	/** Rehash from old buffers to new buffers. */
	protected void rehash(int[] fromKeys, VType[] fromValues) {
		assert fromKeys.length == fromValues.length && HashContainers.checkPowerOfTwo(fromKeys.length - 1);

		// Rehash all stored key/value pairs into the new buffers.
		final int[] keys = this.keys;
		final VType[] values = (VType[]) this.values;
		final int mask = this.mask;
		int existing;

		// Copy the zero element's slot, then rehash everything else.
		int from = fromKeys.length - 1;
		keys[keys.length - 1] = fromKeys[from];
		values[values.length - 1] = fromValues[from];
		while (--from >= 0) {
			if (!((existing = fromKeys[from]) == 0)) {
				int slot = hashKey(existing) & mask;
				while (!((keys[slot]) == 0)) {
					slot = (slot + 1) & mask;
				}
				keys[slot] = existing;
				values[slot] = fromValues[from];
			}
		}
	}

	/**
	 * Allocate new internal buffers. This method attempts to allocate and assign
	 * internal buffers atomically (either allocations succeed or not).
	 */
	protected void allocateBuffers(int arraySize) {
		assert Integer.bitCount(arraySize) == 1;

		// Ensure no change is done if we hit an OOM.
		int[] prevKeys = this.keys;
		VType[] prevValues = (VType[]) this.values;
		try {
			int emptyElementSlot = 1;
			this.keys = (new int[arraySize + emptyElementSlot]);
			this.values = ((VType[]) new Object[arraySize + emptyElementSlot]);
		} catch (OutOfMemoryError e) {
			this.keys = prevKeys;
			this.values = prevValues;
			throw new BufferAllocationException("Not enough memory to allocate buffers for rehashing: %,d -> %,d", e,
					this.mask + 1, arraySize);
		}

		this.resizeAt = expandAtCount(arraySize, loadFactor);
		this.mask = arraySize - 1;
	}

	/**
	 * This method is invoked when there is a new key/ value pair to be inserted
	 * into the buffers but there is not enough empty slots to do so.
	 *
	 * <p>
	 * New buffers are allocated. If this succeeds, we know we can proceed with
	 * rehashing so we assign the pending element to the previous buffer (possibly
	 * violating the invariant of having at least one empty slot) and rehash all
	 * keys, substituting new buffers at the end.
	 */
	protected void allocateThenInsertThenRehash(int slot, int pendingKey, VType pendingValue) {
		assert assigned == resizeAt && ((keys[slot]) == 0) && !((pendingKey) == 0);

		// Try to allocate new buffers first. If we OOM, we leave in a consistent state.
		final int[] prevKeys = this.keys;
		final VType[] prevValues = (VType[]) this.values;
		allocateBuffers(nextBufferSize(mask + 1, size(), loadFactor));
		assert this.keys.length > prevKeys.length;

		// We have succeeded at allocating new data so insert the pending key/value at
		// the free slot in the old arrays before rehashing.
		prevKeys[slot] = pendingKey;
		prevValues[slot] = pendingValue;

		// Rehash old keys, including the pending key.
		rehash(prevKeys, prevValues);
	}

	/**
	 * Shift all the slot-conflicting keys and values allocated to (and including)
	 * <code>slot</code>.
	 */
	protected void shiftConflictingKeys(int gapSlot) {
		final int[] keys = this.keys;
		final VType[] values = (VType[]) this.values;
		final int mask = this.mask;

		// Perform shifts of conflicting keys to fill in the gap.
		int distance = 0;
		while (true) {
			final int slot = (gapSlot + (++distance)) & mask;
			final int existing = keys[slot];
			if (((existing) == 0)) {
				break;
			}

			final int idealSlot = hashKey(existing);
			final int shift = (slot - idealSlot) & mask;
			if (shift >= distance) {
				// Entry at this position was originally at or before the gap slot.
				// Move the conflict-shifted entry to the gap's position and repeat the
				// procedure
				// for any entries to the right of the current position, treating it
				// as the new gap.
				keys[gapSlot] = existing;
				values[gapSlot] = values[slot];
				gapSlot = slot;
				distance = 0;
			}
		}

		// Mark the last found gap slot without a conflict as empty.
		keys[gapSlot] = 0;
		values[gapSlot] = null;
		assigned--;
	}
}
