package net.lax1dude.eaglercraft.backend.server.api.collect;

import javax.annotation.Nonnull;

public interface HPPC {

	int getDefaultExpectedElements();

	@Nonnull
	IntIndexedContainer createIntArrayList();

	@Nonnull
	IntIndexedContainer createIntArrayList(int expectedElements);

	@Nonnull
	IntIndexedContainer createIntArrayList(@Nonnull IntContainer source);

	@Nonnull
	IntSet createIntHashSet();

	@Nonnull
	IntSet createIntHashSet(int expectedElements);

	@Nonnull
	IntSet createIntHashSet(@Nonnull IntContainer source);

	@Nonnull
	<T> ObjectIndexedContainer<T> createObjectArrayList();

	@Nonnull
	<T> ObjectIndexedContainer<T> createObjectArrayList(int expectedElements);

	@Nonnull
	<T> ObjectIndexedContainer<T> createObjectArrayList(@Nonnull ObjectContainer<? extends T> source);

	@Nonnull
	<T> ObjectSet<T> createObjectHashSet();

	@Nonnull
	<T> ObjectSet<T> createObjectHashSet(int expectedElements);

	@Nonnull
	<T> ObjectSet<T> createObjectHashSet(@Nonnull ObjectContainer<? extends T> source);

	@Nonnull
	<T> ObjectSet<T> createObjectIdentityHashSet();

	@Nonnull
	<T> ObjectSet<T> createObjectIdentityHashSet(int expectedElements);

	@Nonnull
	<T> ObjectSet<T> createObjectIdentityHashSet(@Nonnull ObjectContainer<? extends T> source);

	@Nonnull
	<V> IntObjectMap<V> createIntObjectHashMap();

	@Nonnull
	<V> IntObjectMap<V> createIntObjectHashMap(int expectedElements);

	@Nonnull
	<V> IntObjectMap<V> createIntObjectHashMap(@Nonnull IntObjectAssociativeContainer<? extends V> source);

	@Nonnull
	<K> ObjectIntMap<K> createObjectIntHashMap();

	@Nonnull
	<K> ObjectIntMap<K> createObjectIntHashMap(int expectedElements);

	@Nonnull
	<K> ObjectIntMap<K> createObjectIntHashMap(@Nonnull ObjectIntAssociativeContainer<? extends K> source);

	@Nonnull
	<K> ObjectIntMap<K> createObjectIntIdentityHashMap();

	@Nonnull
	<K> ObjectIntMap<K> createObjectIntIdentityHashMap(int expectedElements);

	@Nonnull
	<K> ObjectIntMap<K> createObjectIntIdentityHashMap(@Nonnull ObjectIntAssociativeContainer<? extends K> source);

	@Nonnull
	IntIntMap createIntIntHashMap();

	@Nonnull
	IntIntMap createIntIntHashMap(int expectedElements);

	@Nonnull
	IntIntMap createIntIntHashMap(@Nonnull IntIntAssociativeContainer source);

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap();

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(int expectedElements);

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(@Nonnull ObjectObjectAssociativeContainer<? extends K, ? extends V> source);

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap();

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(int expectedElements);

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(@Nonnull ObjectObjectAssociativeContainer<? extends K, ? extends V> source);

}
