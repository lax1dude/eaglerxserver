/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

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
	<K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(
			@Nonnull ObjectObjectAssociativeContainer<? extends K, ? extends V> source);

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap();

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(int expectedElements);

	@Nonnull
	<K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(
			@Nonnull ObjectObjectAssociativeContainer<? extends K, ? extends V> source);

}
