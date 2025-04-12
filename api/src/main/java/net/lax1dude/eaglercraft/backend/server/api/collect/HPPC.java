package net.lax1dude.eaglercraft.backend.server.api.collect;

public interface HPPC {

	int getDefaultExpectedElements();

	IntIndexedContainer createIntArrayList();

	IntIndexedContainer createIntArrayList(int expectedElements);

	IntIndexedContainer createIntArrayList(IntContainer source);

	IntSet createIntHashSet();

	IntSet createIntHashSet(int expectedElements);

	IntSet createIntHashSet(IntContainer source);

	<T> ObjectIndexedContainer<T> createObjectArrayList();

	<T> ObjectIndexedContainer<T> createObjectArrayList(int expectedElements);

	<T> ObjectIndexedContainer<T> createObjectArrayList(ObjectContainer<? extends T> source);

	<T> ObjectSet<T> createObjectHashSet();

	<T> ObjectSet<T> createObjectHashSet(int expectedElements);

	<T> ObjectSet<T> createObjectHashSet(ObjectContainer<? extends T> source);

	<T> ObjectSet<T> createObjectIdentityHashSet();

	<T> ObjectSet<T> createObjectIdentityHashSet(int expectedElements);

	<T> ObjectSet<T> createObjectIdentityHashSet(ObjectContainer<? extends T> source);

	<V> IntObjectMap<V> createIntObjectHashMap();

	<V> IntObjectMap<V> createIntObjectHashMap(int expectedElements);

	<V> IntObjectMap<V> createIntObjectHashMap(IntObjectAssociativeContainer<? extends V> source);

	<K> ObjectIntMap<K> createObjectIntHashMap();

	<K> ObjectIntMap<K> createObjectIntHashMap(int expectedElements);

	<K> ObjectIntMap<K> createObjectIntHashMap(ObjectIntAssociativeContainer<? extends K> source);

	<K> ObjectIntMap<K> createObjectIntIdentityHashMap();

	<K> ObjectIntMap<K> createObjectIntIdentityHashMap(int expectedElements);

	<K> ObjectIntMap<K> createObjectIntIdentityHashMap(ObjectIntAssociativeContainer<? extends K> source);

	IntIntMap createIntIntHashMap();

	IntIntMap createIntIntHashMap(int expectedElements);

	IntIntMap createIntIntHashMap(IntIntAssociativeContainer source);

	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap();

	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(int expectedElements);

	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(ObjectObjectAssociativeContainer<? extends K, ? extends V> source);

	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap();

	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(int expectedElements);

	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(ObjectObjectAssociativeContainer<? extends K, ? extends V> source);

}
