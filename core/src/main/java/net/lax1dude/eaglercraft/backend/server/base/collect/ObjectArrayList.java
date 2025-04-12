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
import java.util.stream.Stream;

import net.lax1dude.eaglercraft.backend.server.api.collect.*;

import static net.lax1dude.eaglercraft.backend.server.base.collect.Containers.*;

/** An array-backed list of Objects. */
@SuppressWarnings("unchecked")
public class ObjectArrayList<KType> extends AbstractObjectCollection<KType>
		implements ObjectIndexedContainer<KType>, Preallocable, Cloneable, Accountable {
	/** An immutable empty buffer (array). */
	public static final Object[] EMPTY_ARRAY = new Object[0];

	;

	/** Reuse the same strategy instance. */
	private static final BoundedProportionalArraySizingStrategy DEFAULT_SIZING_STRATEGY = BoundedProportionalArraySizingStrategy.DEFAULT_INSTANCE;

	/**
	 * Internal array for storing the list. The array may be larger than the current
	 * size ({@link #size()}).
	 */
	public Object[] buffer = EMPTY_ARRAY;

	/** Current number of elements stored in {@link #buffer}. */
	public int elementsCount;

	/** Buffer resizing strategy. */
	protected final ArraySizingStrategy resizer;

	/** New instance with sane defaults. */
	public ObjectArrayList() {
		this(DEFAULT_EXPECTED_ELEMENTS);
	}

	/**
	 * New instance with sane defaults.
	 *
	 * @param expectedElements The expected number of elements guaranteed not to
	 *                         cause buffer expansion (inclusive).
	 */
	public ObjectArrayList(int expectedElements) {
		this(expectedElements, DEFAULT_SIZING_STRATEGY);
	}

	/**
	 * New instance with sane defaults.
	 *
	 * @param expectedElements The expected number of elements guaranteed not to
	 *                         cause buffer expansion (inclusive).
	 * @param resizer          Underlying buffer sizing strategy.
	 */
	public ObjectArrayList(int expectedElements, ArraySizingStrategy resizer) {
		assert resizer != null;
		this.resizer = resizer;
		buffer = Arrays.copyOf(buffer, expectedElements);
	}

	/**
	 * Creates a new list from the elements of another container in its iteration
	 * order.
	 */
	public ObjectArrayList(ObjectContainer<? extends KType> container) {
		this(container.size());
		addAll(container);
	}

	/** {@inheritDoc} */
	@Override
	public void add(KType e1) {
		ensureBufferSpace(1);
		buffer[elementsCount++] = e1;
	}

	/**
	 * Appends two elements at the end of the list. To add more than two elements,
	 * use <code>add
	 * </code> (vararg-version) or access the buffer directly (tight loop).
	 */
	public void add(KType e1, KType e2) {
		ensureBufferSpace(2);
		buffer[elementsCount++] = e1;
		buffer[elementsCount++] = e2;
	}

	/** Add all elements from a range of given array to the list. */
	public void add(KType[] elements, int start, int length) {
		assert length >= 0 : "Length must be >= 0";

		ensureBufferSpace(length);
		System.arraycopy(elements, start, buffer, elementsCount, length);
		elementsCount += length;
	}

	/**
	 * Vararg-signature method for adding elements at the end of the list.
	 *
	 * <p>
	 * <b>This method is handy, but costly if used in tight loops (anonymous array
	 * passing)</b>
	 */
	@SafeVarargs
	public final void add(KType... elements) {
		add(elements, 0, elements.length);
	}

	/** Adds all elements from another container. */
	public int addAll(ObjectContainer<? extends KType> container) {
		final int size = container.size();
		ensureBufferSpace(size);

		for (ObjectCursor<? extends KType> cursor : container) {
			add(cursor.value);
		}

		return size;
	}

	/** Adds all elements from another iterable. */
	public int addAll(Iterable<? extends ObjectCursor<? extends KType>> iterable) {
		int size = 0;
		for (ObjectCursor<? extends KType> cursor : iterable) {
			add(cursor.value);
			size++;
		}
		return size;
	}

	/** {@inheritDoc} */
	@Override
	public void insert(int index, KType e1) {
		assert (index >= 0 && index <= size()) : "Index " + index + " out of bounds [" + 0 + ", " + size() + "].";

		ensureBufferSpace(1);
		System.arraycopy(buffer, index, buffer, index + 1, elementsCount - index);
		buffer[index] = e1;
		elementsCount++;
	}

	/** {@inheritDoc} */
	@Override
	public KType get(int index) {
		assert (index >= 0 && index < size()) : "Index " + index + " out of bounds [" + 0 + ", " + size() + ").";

		return (KType) buffer[index];
	}

	/** {@inheritDoc} */
	@Override
	public KType set(int index, KType e1) {
		assert (index >= 0 && index < size()) : "Index " + index + " out of bounds [" + 0 + ", " + size() + ").";

		final KType v = (KType) buffer[index];
		buffer[index] = e1;
		return v;
	}

	/** {@inheritDoc} */
	@Override
	public KType removeAt(int index) {
		assert (index >= 0 && index < size()) : "Index " + index + " out of bounds [" + 0 + ", " + size() + ").";

		final KType v = (KType) buffer[index];
		System.arraycopy(buffer, index + 1, buffer, index, --elementsCount - index);

		buffer[elementsCount] = null;

		return v;
	}

	/** {@inheritDoc} */
	@Override
	public KType removeLast() {
		assert elementsCount > 0;

		final KType v = (KType) buffer[--elementsCount];

		buffer[elementsCount] = null;

		return v;
	}

	/** {@inheritDoc} */
	@Override
	public void removeRange(int fromIndex, int toIndex) {
		assert (fromIndex >= 0 && fromIndex <= size())
				: "Index " + fromIndex + " out of bounds [" + 0 + ", " + size() + ").";
		assert (toIndex >= 0 && toIndex <= size()) : "Index " + toIndex + " out of bounds [" + 0 + ", " + size() + "].";
		assert fromIndex <= toIndex : "fromIndex must be <= toIndex: " + fromIndex + ", " + toIndex;

		System.arraycopy(buffer, toIndex, buffer, fromIndex, elementsCount - toIndex);
		final int count = toIndex - fromIndex;
		elementsCount -= count;

		Arrays.fill(buffer, elementsCount, elementsCount + count, null);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeElement(KType e1) {
		return removeFirst(e1) != -1;
	}

	/** {@inheritDoc} */
	@Override
	public int removeFirst(KType e1) {
		final int index = indexOf(e1);
		if (index >= 0)
			removeAt(index);
		return index;
	}

	/** {@inheritDoc} */
	@Override
	public int removeLast(KType e1) {
		final int index = lastIndexOf(e1);
		if (index >= 0)
			removeAt(index);
		return index;
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(KType e1) {
		int to = 0;
		for (int from = 0; from < elementsCount; from++) {
			if (this.equals(e1, buffer[from])) {
				continue;
			}
			if (to != from) {
				buffer[to] = buffer[from];
			}
			to++;
		}
		final int deleted = elementsCount - to;
		this.elementsCount = to;

		Arrays.fill(buffer, elementsCount, elementsCount + deleted, null);

		return deleted;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(KType e1) {
		return indexOf(e1) >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(KType e1) {
		for (int i = 0; i < elementsCount; i++) {
			if (this.equals(e1, buffer[i])) {
				return i;
			}
		}

		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(KType e1) {
		for (int i = elementsCount - 1; i >= 0; i--) {
			if (this.equals(e1, buffer[i])) {
				return i;
			}
		}

		return -1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return elementsCount == 0;
	}

	/**
	 * Ensure this container can hold at least the given number of elements without
	 * resizing its buffers.
	 *
	 * @param expectedElements The total number of elements, inclusive.
	 */
	@Override
	public void ensureCapacity(int expectedElements) {
		final int bufferLen = (buffer == null ? 0 : buffer.length);
		if (expectedElements > bufferLen) {
			ensureBufferSpace(expectedElements - size());
		}
	}

	/**
	 * Ensures the internal buffer has enough free slots to store
	 * <code>expectedAdditions</code>. Increases internal buffer size if needed.
	 */
	protected void ensureBufferSpace(int expectedAdditions) {
		final int bufferLen = (buffer == null ? 0 : buffer.length);
		if (elementsCount + expectedAdditions > bufferLen) {
			final int newSize = resizer.grow(bufferLen, elementsCount, expectedAdditions);
			assert newSize >= elementsCount + expectedAdditions : "Resizer failed to" + " return sensible new size: "
					+ newSize + " <= " + (elementsCount + expectedAdditions);

			this.buffer = Arrays.copyOf(buffer, newSize);
		}
	}

	/**
	 * Truncate or expand the list to the new size. If the list is truncated, the
	 * buffer will not be reallocated (use {@link #trimToSize()} if you need a
	 * truncated buffer), but the truncated values will be reset to the default
	 * value (zero). If the list is expanded, the elements beyond the current size
	 * are initialized with JVM-defaults (zero or <code>null</code> values).
	 */
	public void resize(int newSize) {
		if (newSize <= buffer.length) {
			if (newSize < elementsCount) {
				Arrays.fill(buffer, newSize, elementsCount, null);
			} else {
				Arrays.fill(buffer, elementsCount, newSize, null);
			}
		} else {
			ensureCapacity(newSize);
		}
		this.elementsCount = newSize;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return elementsCount;
	}

	/** Trim the internal buffer to the current size. */
	public void trimToSize() {
		if (size() != this.buffer.length) {
			this.buffer = (KType[]) toArray();
		}
	}

	/**
	 * Sets the number of stored elements to zero. Releases and initializes the
	 * internal storage array to default values. To clear the list without cleaning
	 * the buffer, simply set the {@link #elementsCount} field to zero.
	 */
	@Override
	public void clear() {
		Arrays.fill(buffer, 0, elementsCount, null);
		this.elementsCount = 0;
	}

	/**
	 * Sets the number of stored elements to zero and releases the internal storage
	 * array.
	 */
	@Override
	public void release() {
		this.buffer = (KType[]) EMPTY_ARRAY;
		this.elementsCount = 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The returned array is sized to match exactly the number of elements of the
	 * stack.
	 */
	@Override
	public Object[] toArray() {

		return Arrays.copyOf(buffer, elementsCount);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public Stream<KType> stream() {
		return (Stream<KType>) Arrays.stream(buffer, 0, size());
	}

	/** {@inheritDoc} */
	@Override
	public ObjectIndexedContainer<KType> sort() {
		Arrays.sort(buffer, 0, elementsCount);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ObjectIndexedContainer<KType> reverse() {
		for (int i = 0, mid = elementsCount >> 1, j = elementsCount - 1; i < mid; i++, j--) {
			KType tmp = (KType) buffer[i];
			buffer[i] = buffer[j];
			buffer[j] = tmp;
		}
		return this;
	}

	/**
	 * Clone this object. The returned clone will reuse the same hash function and
	 * array resizing strategy.
	 */
	@Override
	public ObjectArrayList<KType> clone() {
		try {

			final ObjectArrayList<KType> cloned = (ObjectArrayList<KType>) super.clone();
			cloned.buffer = buffer.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int h = 1, max = elementsCount;
		for (int i = 0; i < max; i++) {
			h = 31 * h + BitMixer.mix(this.buffer[i]);
		}
		return h;
	}

	/**
	 * Returns <code>true</code> only if the other object is an instance of the same
	 * class and with the same elements. Equality comparison is performed with this
	 * object's {@link #equals(Object, Object)} method.
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj != null && getClass() == obj.getClass() && equalElements(getClass().cast(obj)));
	}

	/**
	 * Compare index-aligned elements against another
	 * {@link ObjectIndexedContainer}. Equality comparison is performed with this
	 * object's {@link #equals(Object, Object)} method.
	 */
	protected boolean equalElements(ObjectArrayList<?> other) {
		int max = size();
		if (other.size() != max) {
			return false;
		}

		for (int i = 0; i < max; i++) {
			if (!this.equals(get(i), other.get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public long ramBytesAllocated() {
		// int: elementsCount
		return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + Integer.BYTES + resizer.ramBytesAllocated()
				+ RamUsageEstimator.shallowSizeOfArray(buffer);
	}

	@Override
	public long ramBytesUsed() {
		// int: elementsCount
		return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + Integer.BYTES + resizer.ramBytesUsed()
				+ RamUsageEstimator.shallowUsedSizeOfArray(buffer, elementsCount);
	}

	/** An iterator implementation for {@link ObjectArrayList#iterator}. */
	static final class ValueIterator<KType> extends AbstractIterator<ObjectCursor<KType>> {
		private final ObjectCursor<KType> cursor;

		private final KType[] buffer;
		private final int size;

		public ValueIterator(KType[] buffer, int size) {
			this.cursor = new ObjectCursor<KType>();
			this.cursor.index = -1;
			this.size = size;
			this.buffer = buffer;
		}

		@Override
		protected ObjectCursor<KType> fetch() {
			if (cursor.index + 1 == size)
				return done();

			cursor.value = buffer[++cursor.index];
			return cursor;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<ObjectCursor<KType>> iterator() {
		return new ValueIterator<KType>((KType[]) buffer, size());
	}

	/** {@inheritDoc} */
	@Override
	public <T extends ObjectProcedure<? super KType>> T forEach(T procedure) {
		return forEach(procedure, 0, size());
	}

	/**
	 * Applies <code>procedure</code> to a slice of the list,
	 * <code>fromIndex</code>, inclusive, to <code>toIndex</code>, exclusive.
	 */
	public <T extends ObjectProcedure<? super KType>> T forEach(T procedure, int fromIndex, final int toIndex) {
		assert (fromIndex >= 0 && fromIndex <= size())
				: "Index " + fromIndex + " out of bounds [" + 0 + ", " + size() + ").";

		assert (toIndex >= 0 && toIndex <= size()) : "Index " + toIndex + " out of bounds [" + 0 + ", " + size() + "].";

		assert fromIndex <= toIndex : "fromIndex must be <= toIndex: " + fromIndex + ", " + toIndex;

		final KType[] buffer = (KType[]) this.buffer;
		for (int i = fromIndex; i < toIndex; i++) {
			procedure.apply(buffer[i]);
		}

		return procedure;
	}

	/** {@inheritDoc} */
	@Override
	public int removeAll(ObjectPredicate<? super KType> predicate) {
		final KType[] buffer = (KType[]) this.buffer;
		final int elementsCount = this.elementsCount;
		int to = 0;
		int from = 0;
		try {
			for (; from < elementsCount; from++) {
				if (predicate.apply(buffer[from])) {
					buffer[from] = null;
					continue;
				}

				if (to != from) {
					buffer[to] = buffer[from];
					buffer[from] = null;
				}
				to++;
			}
		} finally {
			// Keep the list in a consistent state, even if the predicate throws an
			// exception.
			for (; from < elementsCount; from++) {
				if (to != from) {
					buffer[to] = buffer[from];
					buffer[from] = null;
				}
				to++;
			}

			this.elementsCount = to;
		}

		return elementsCount - to;
	}

	/** {@inheritDoc} */
	@Override
	public <T extends ObjectPredicate<? super KType>> T forEach(T predicate) {
		return forEach(predicate, 0, size());
	}

	/**
	 * Applies <code>predicate</code> to a slice of the list,
	 * <code>fromIndex</code>, inclusive, to <code>toIndex</code>, exclusive, or
	 * until predicate returns <code>false</code>.
	 */
	public <T extends ObjectPredicate<? super KType>> T forEach(T predicate, int fromIndex, final int toIndex) {
		assert (fromIndex >= 0 && fromIndex <= size())
				: "Index " + fromIndex + " out of bounds [" + 0 + ", " + size() + ").";
		assert (toIndex >= 0 && toIndex <= size()) : "Index " + toIndex + " out of bounds [" + 0 + ", " + size() + "].";
		assert fromIndex <= toIndex : "fromIndex must be <= toIndex: " + fromIndex + ", " + toIndex;

		final KType[] buffer = (KType[]) this.buffer;
		for (int i = fromIndex; i < toIndex; i++) {
			if (!predicate.apply(buffer[i]))
				break;
		}

		return predicate;
	}

	/**
	 * Create a list from a variable number of arguments or an array of
	 * <code>Object</code>. The elements are copied from the argument to the
	 * internal buffer.
	 */
	@SafeVarargs
	public static <KType> ObjectArrayList<KType> from(KType... elements) {
		final ObjectArrayList<KType> list = new ObjectArrayList<KType>(elements.length);
		list.add(elements);
		return list;
	}
}
