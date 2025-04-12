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
 * A hash set of <code>Object</code>s, implemented using open addressing with
 * linear probing for collision resolution.
 *
 * @see <a href="{@docRoot}/overview-summary.html#interfaces">HPPC interfaces
 *      diagram</a>
 */
@SuppressWarnings("unchecked")
public class ObjectHashSet<KType> extends AbstractObjectCollection<KType>
		implements ObjectLookupContainer<KType>, ObjectSet<KType>, Preallocable, Cloneable, Accountable {
	/** The hash array holding keys. */
	public Object[] keys;

	/**
	 * The number of stored keys (assigned key slots), excluding the special "empty"
	 * key, if any.
	 *
	 * @see #size()
	 * @see #hasEmptyKey
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

	/**
	 * New instance with sane defaults.
	 *
	 * @see #ObjectHashSet(int, double)
	 */
	public ObjectHashSet() {
		this(DEFAULT_EXPECTED_ELEMENTS, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * New instance with sane defaults.
	 *
	 * @see #ObjectHashSet(int, double)
	 */
	public ObjectHashSet(int expectedElements) {
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
	public ObjectHashSet(int expectedElements, double loadFactor) {
		this.loadFactor = verifyLoadFactor(loadFactor);
		iterationSeed = HashContainers.nextIterationSeed();
		ensureCapacity(expectedElements);
	}

	/** New instance copying elements from another {@link ObjectContainer}. */
	public ObjectHashSet(ObjectContainer<? extends KType> container) {
		this(container.size());
		addAll(container);
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(KType key) {
		if (((key) == null)) {
			assert ((keys[mask + 1]) == null);
			boolean added = !hasEmptyKey;
			hasEmptyKey = true;
			return added;
		} else {
			final KType[] keys = (KType[]) this.keys;
			final int mask = this.mask;
			int slot = hashKey(key) & mask;

			KType existing;
			while (!((existing = keys[slot]) == null)) {
				if (this.equals(key, existing)) {
					return false;
				}
				slot = (slot + 1) & mask;
			}

			if (assigned == resizeAt) {
				allocateThenInsertThenRehash(slot, key);
			} else {
				keys[slot] = key;
			}

			assigned++;
			return true;
		}
	}

	/**
	 * Adds all elements from the given list (vararg) to this set.
	 *
	 * @return Returns the number of elements actually added as a result of this
	 *         call (not previously present in the set).
	 */
	@SafeVarargs
	public final int addAll(KType... elements) {
		ensureCapacity(elements.length);
		int count = 0;
		for (KType e : elements) {
			if (add(e)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Adds all elements from the given {@link ObjectContainer} to this set.
	 *
	 * @return Returns the number of elements actually added as a result of this
	 *         call (not previously present in the set).
	 */
	public int addAll(ObjectContainer<? extends KType> container) {
		ensureCapacity(container.size());
		return addAll((Iterable<? extends ObjectCursor<? extends KType>>) container);
	}

	/**
	 * Adds all elements from the given iterable to this set.
	 *
	 * @return Returns the number of elements actually added as a result of this
	 *         call (not previously present in the set).
	 */
	public int addAll(Iterable<? extends ObjectCursor<? extends KType>> iterable) {
		int count = 0;
		for (ObjectCursor<? extends KType> cursor : iterable) {
			if (add(cursor.value)) {
				count++;
			}
		}
		return count;
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {

		final KType[] cloned = ((KType[]) new Object[size()]);
		int j = 0;
		if (hasEmptyKey) {
			cloned[j++] = null;
		}

		final KType[] keys = (KType[]) this.keys;
		int seed = nextIterationSeed();
		int inc = iterationIncrement(seed);
		for (int i = 0, mask = this.mask, slot = seed & mask; i <= mask; i++, slot = (slot + inc) & mask) {
			KType existing;
			if (!((existing = keys[slot]) == null)) {
				cloned[j++] = existing;
			}
		}

		return cloned;
	}

	/** An alias for the (preferred) {@link #removeAll}. */
	public boolean remove(KType key) {
		if (((key) == null)) {
			boolean hadEmptyKey = hasEmptyKey;
			hasEmptyKey = false;
			return hadEmptyKey;
		} else {
			final KType[] keys = (KType[]) this.keys;
			final int mask = this.mask;
			int slot = hashKey(key) & mask;

			KType existing;
			while (!((existing = keys[slot]) == null)) {
				if (this.equals(key, existing)) {
					shiftConflictingKeys(slot);
					return true;
				}
				slot = (slot + 1) & mask;
			}
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(KType key) {
		return remove(key) ? 1 : 0;
	}

	/**
	 * Removes all keys present in a given container.
	 *
	 * @return Returns the number of elements actually removed as a result of this
	 *         call.
	 */
	public int removeAll(ObjectContainer<? super KType> other) {
		final int before = size();

		// Try to iterate over the smaller set or over the container that isn't
		// implementing
		// efficient contains() lookup.

		if (other.size() >= size() && other instanceof ObjectLookupContainer<?>) {
			if (hasEmptyKey && other.contains(null)) {
				hasEmptyKey = false;
			}

			final KType[] keys = (KType[]) this.keys;
			for (int slot = 0, max = this.mask; slot <= max;) {
				KType existing;
				if (!((existing = keys[slot]) == null) && other.contains(existing)) {
					// Shift, do not increment slot.
					shiftConflictingKeys(slot);
				} else {
					slot++;
				}
			}
		} else {
			for (ObjectCursor<?> c : other) {
				remove((KType) c.value);
			}
		}

		return before - size();
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(ObjectPredicate<? super KType> predicate) {
		int before = size();

		if (hasEmptyKey) {
			if (predicate.apply(null)) {
				hasEmptyKey = false;
			}
		}

		final KType[] keys = (KType[]) this.keys;
		for (int slot = 0, max = this.mask; slot <= max;) {
			KType existing;
			if (!((existing = keys[slot]) == null)) {
				if (predicate.apply(existing)) {
					shiftConflictingKeys(slot);
					continue; // Repeat the check for the same slot i (shifted).
				}
			}
			slot++;
		}

		return before - size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(KType key) {
		if (((key) == null)) {
			return hasEmptyKey;
		} else {
			final KType[] keys = (KType[]) this.keys;
			final int mask = this.mask;
			int slot = hashKey(key) & mask;
			KType existing;
			while (!((existing = keys[slot]) == null)) {
				if (this.equals(key, existing)) {
					return true;
				}
				slot = (slot + 1) & mask;
			}
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		assigned = 0;
		hasEmptyKey = false;
		Arrays.fill(keys, null);
	}

	/** {@inheritDoc} */
	@Override
	public void release() {
		assigned = 0;
		hasEmptyKey = false;
		keys = null;
		ensureCapacity(Containers.DEFAULT_EXPECTED_ELEMENTS);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Ensure this container can hold at least the given number of elements without
	 * resizing its buffers.
	 *
	 * @param expectedElements The total number of elements, inclusive.
	 */
	@Override
	public void ensureCapacity(int expectedElements) {
		if (expectedElements > resizeAt || keys == null) {
			final KType[] prevKeys = (KType[]) this.keys;
			allocateBuffers(minBufferSize(expectedElements, loadFactor));
			if (prevKeys != null && !isEmpty()) {
				rehash(prevKeys);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return assigned + (hasEmptyKey ? 1 : 0);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int h = hasEmptyKey ? 0xDEADBEEF : 0;
		final KType[] keys = (KType[]) this.keys;
		for (int slot = mask; slot >= 0; slot--) {
			KType existing;
			if (!((existing = keys[slot]) == null)) {
				h += BitMixer.mix(existing);
			}
		}
		return h;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null && getClass() == obj.getClass() && sameKeys(getClass().cast(obj)));
	}

	/**
	 * Return true if all keys of some other container exist in this container.
	 * Equality comparison is performed with this object's
	 * {@link #equals(Object, Object)} method.
	 */
	private boolean sameKeys(ObjectSet<?> other) {
		if (other.size() != size()) {
			return false;
		}

		for (ObjectCursor<?> c : other) {
			if (!contains((KType) c.value)) {
				return false;
			}
		}

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public ObjectHashSet<KType> clone() {
		try {

			ObjectHashSet<KType> cloned = (ObjectHashSet<KType>) super.clone();
			cloned.keys = keys.clone();
			cloned.hasEmptyKey = hasEmptyKey;
			cloned.iterationSeed = HashContainers.nextIterationSeed();
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<ObjectCursor<KType>> iterator() {
		return new EntryIterator();
	}

	@Override
	public long ramBytesAllocated() {
		// int: assigned, mask, keyMixer, resizeAt
		// double: loadFactor
		// boolean: hasEmptyKey
		return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4 * Integer.BYTES + Double.BYTES + 1
				+ RamUsageEstimator.shallowSizeOfArray(keys);
	}

	@Override
	public long ramBytesUsed() {
		// int: assigned, mask, keyMixer, resizeAt
		// double: loadFactor
		// boolean: hasEmptyKey
		return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 4 * Integer.BYTES + Double.BYTES + 1
				+ RamUsageEstimator.shallowUsedSizeOfArray(keys, size());
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
	protected final class EntryIterator extends AbstractIterator<ObjectCursor<KType>> {
		private final ObjectCursor<KType> cursor;
		private final int increment;
		private int index;
		private int slot;

		public EntryIterator() {
			cursor = new ObjectCursor<KType>();
			int seed = nextIterationSeed();
			increment = iterationIncrement(seed);
			slot = seed & mask;
		}

		@Override
		protected ObjectCursor<KType> fetch() {
			final int mask = ObjectHashSet.this.mask;
			while (index <= mask) {
				KType existing;
				index++;
				slot = (slot + increment) & mask;
				if (!((existing = (KType) keys[slot]) == null)) {
					cursor.index = slot;
					cursor.value = existing;
					return cursor;
				}
			}

			if (index == mask + 1 && hasEmptyKey) {
				cursor.index = index++;
				cursor.value = null;
				return cursor;
			}

			return done();
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T extends ObjectProcedure<? super KType>> T forEach(T procedure) {
		if (hasEmptyKey) {
			procedure.apply(null);
		}

		final KType[] keys = (KType[]) this.keys;
		int seed = nextIterationSeed();
		int inc = iterationIncrement(seed);
		for (int i = 0, mask = this.mask, slot = seed & mask; i <= mask; i++, slot = (slot + inc) & mask) {
			KType existing;
			if (!((existing = keys[slot]) == null)) {
				procedure.apply(existing);
			}
		}

		return procedure;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends ObjectPredicate<? super KType>> T forEach(T predicate) {
		if (hasEmptyKey) {
			if (!predicate.apply(null)) {
				return predicate;
			}
		}

		final KType[] keys = (KType[]) this.keys;
		int seed = nextIterationSeed();
		int inc = iterationIncrement(seed);
		for (int i = 0, mask = this.mask, slot = seed & mask; i <= mask; i++, slot = (slot + inc) & mask) {
			KType existing;
			if (!((existing = keys[slot]) == null)) {
				if (!predicate.apply(existing)) {
					break;
				}
			}
		}

		return predicate;
	}

	/**
	 * Create a set from a variable number of arguments or an array of
	 * <code>Object</code>. The elements are copied from the argument to the
	 * internal buffer.
	 */
	@SafeVarargs
	public static <KType> ObjectHashSet<KType> from(KType... elements) {
		final ObjectHashSet<KType> set = new ObjectHashSet<KType>(elements.length);
		set.addAll(elements);
		return set;
	}

	/**
	 * Returns a hash code for the given key.
	 *
	 * <p>
	 * The output from this function should evenly distribute keys across the entire
	 * integer range.
	 */
	protected int hashKey(KType key) {
		assert !((key) == null); // Handled as a special case (empty slot marker).
		return BitMixer.mixPhi(key);
	}

	/**
	 * Returns a logical "index" of a given key that can be used to speed up
	 * follow-up logic in certain scenarios (conditional logic).
	 *
	 * <p>
	 * The semantics of "indexes" are not strictly defined. Indexes may (and
	 * typically won't be) contiguous.
	 *
	 * <p>
	 * The index is valid only between modifications (it will not be affected by
	 * read-only operations).
	 *
	 * @see #indexExists
	 * @see #indexGet
	 * @see #indexInsert
	 * @see #indexReplace
	 * @param key The key to locate in the set.
	 * @return A non-negative value of the logical "index" of the key in the set or
	 *         a negative value if the key did not exist.
	 */
	public int indexOf(KType key) {
		final int mask = this.mask;
		if (((key) == null)) {
			return hasEmptyKey ? mask + 1 : ~(mask + 1);
		} else {
			final KType[] keys = (KType[]) this.keys;
			int slot = hashKey(key) & mask;

			KType existing;
			while (!((existing = keys[slot]) == null)) {
				if (this.equals(key, existing)) {
					return slot;
				}
				slot = (slot + 1) & mask;
			}

			return ~slot;
		}
	}

	/**
	 * @see #indexOf
	 * @param index The index of a given key, as returned from {@link #indexOf}.
	 * @return Returns <code>true</code> if the index corresponds to an existing key
	 *         or false otherwise. This is equivalent to checking whether the index
	 *         is a positive value (existing keys) or a negative value (non-existing
	 *         keys).
	 */
	public boolean indexExists(int index) {
		assert index < 0 || index <= mask || (index == mask + 1 && hasEmptyKey);

		return index >= 0;
	}

	/**
	 * Returns the exact value of the existing key. This method makes sense for sets
	 * of objects which define custom key-equality relationship.
	 *
	 * @see #indexOf
	 * @param index The index of an existing key.
	 * @return Returns the equivalent key currently stored in the set.
	 * @throws AssertionError If assertions are enabled and the index does not
	 *                        correspond to an existing key.
	 */
	public KType indexGet(int index) {
		assert index >= 0 : "The index must point at an existing key.";
		assert index <= mask || (index == mask + 1 && hasEmptyKey);

		return (KType) keys[index];
	}

	/**
	 * Replaces the existing equivalent key with the given one and returns any
	 * previous value stored for that key.
	 *
	 * @see #indexOf
	 * @param index         The index of an existing key.
	 * @param equivalentKey The key to put in the set as a replacement. Must be
	 *                      equivalent to the key currently stored at the provided
	 *                      index.
	 * @return Returns the previous key stored in the set.
	 * @throws AssertionError If assertions are enabled and the index does not
	 *                        correspond to an existing key.
	 */
	public KType indexReplace(int index, KType equivalentKey) {
		assert index >= 0 : "The index must point at an existing key.";
		assert index <= mask || (index == mask + 1 && hasEmptyKey);
		assert this.equals(keys[index], equivalentKey);

		KType previousValue = (KType) keys[index];
		keys[index] = equivalentKey;
		return previousValue;
	}

	/**
	 * Inserts a key for an index that is not present in the set. This method may
	 * help in avoiding double recalculation of the key's hash.
	 *
	 * @see #indexOf
	 * @param index The index of a previously non-existing key, as returned from
	 *              {@link #indexOf}.
	 * @throws AssertionError If assertions are enabled and the index does not
	 *                        correspond to an existing key.
	 */
	public void indexInsert(int index, KType key) {
		assert index < 0 : "The index must not point at an existing key.";

		index = ~index;
		if (((key) == null)) {
			assert index == mask + 1;
			assert ((keys[index]) == null);
			hasEmptyKey = true;
		} else {
			assert ((keys[index]) == null);

			if (assigned == resizeAt) {
				allocateThenInsertThenRehash(index, key);
			} else {
				keys[index] = key;
			}

			assigned++;
		}
	}

	/**
	 * Removes a key at an index previously acquired from {@link #indexOf}.
	 *
	 * @see #indexOf
	 * @param index The index of the key to remove, as returned from
	 *              {@link #indexOf}.
	 * @throws AssertionError If assertions are enabled and the index does not
	 *                        correspond to an existing key.
	 */
	public void indexRemove(int index) {
		assert index >= 0 : "The index must point at an existing key.";
		assert index <= mask || (index == mask + 1 && hasEmptyKey);

		if (index > mask) {
			hasEmptyKey = false;
		} else {
			shiftConflictingKeys(index);
		}
	}

	@Override
	public String visualizeKeyDistribution(int characters) {
		return ObjectBufferVisualizer.visualizeKeyDistribution(keys, mask, characters);
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
	protected void rehash(KType[] fromKeys) {
		assert HashContainers.checkPowerOfTwo(fromKeys.length - 1);

		// Rehash all stored keys into the new buffers.
		final KType[] keys = (KType[]) this.keys;
		final int mask = this.mask;
		KType existing;
		for (int i = fromKeys.length - 1; --i >= 0;) {
			if (!((existing = fromKeys[i]) == null)) {
				int slot = hashKey(existing) & mask;
				while (!((keys[slot]) == null)) {
					slot = (slot + 1) & mask;
				}
				keys[slot] = existing;
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
		KType[] prevKeys = (KType[]) this.keys;
		try {
			int emptyElementSlot = 1;
			this.keys = ((KType[]) new Object[arraySize + emptyElementSlot]);
		} catch (OutOfMemoryError e) {
			this.keys = prevKeys;
			throw new BufferAllocationException("Not enough memory to allocate buffers for rehashing: %,d -> %,d", e,
					this.keys == null ? 0 : size(), arraySize);
		}

		this.resizeAt = expandAtCount(arraySize, loadFactor);
		this.mask = arraySize - 1;
	}

	/**
	 * This method is invoked when there is a new key to be inserted into the buffer
	 * but there is not enough empty slots to do so.
	 *
	 * <p>
	 * New buffers are allocated. If this succeeds, we know we can proceed with
	 * rehashing so we assign the pending element to the previous buffer (possibly
	 * violating the invariant of having at least one empty slot) and rehash all
	 * keys, substituting new buffers at the end.
	 */
	protected void allocateThenInsertThenRehash(int slot, KType pendingKey) {
		assert assigned == resizeAt && (((KType) keys[slot]) == null) && !((pendingKey) == null);

		// Try to allocate new buffers first. If we OOM, we leave in a consistent state.
		final KType[] prevKeys = (KType[]) this.keys;
		allocateBuffers(nextBufferSize(mask + 1, size(), loadFactor));
		assert this.keys.length > prevKeys.length;

		// We have succeeded at allocating new data so insert the pending key/value at
		// the free slot in the old arrays before rehashing.
		prevKeys[slot] = pendingKey;

		// Rehash old keys, including the pending key.
		rehash(prevKeys);
	}

	/**
	 * Shift all the slot-conflicting keys allocated to (and including)
	 * <code>slot</code>.
	 */
	protected void shiftConflictingKeys(int gapSlot) {
		final KType[] keys = (KType[]) this.keys;
		final int mask = this.mask;

		// Perform shifts of conflicting keys to fill in the gap.
		int distance = 0;
		while (true) {
			final int slot = (gapSlot + (++distance)) & mask;
			final KType existing = keys[slot];
			if (((existing) == null)) {
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
				gapSlot = slot;
				distance = 0;
			}
		}

		// Mark the last found gap slot without a conflict as empty.
		keys[gapSlot] = null;
		assigned--;
	}
}
