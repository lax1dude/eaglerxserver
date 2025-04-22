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

package net.lax1dude.eaglercraft.backend.server.base.collect;

import net.lax1dude.eaglercraft.backend.server.api.collect.HPPC;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntIndexedContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntIntAssociativeContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntIntMap;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntObjectAssociativeContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntObjectMap;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntSet;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectIndexedContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectIntAssociativeContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectIntMap;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectObjectAssociativeContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectObjectMap;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectSet;

public final class HPPCFactory implements HPPC {

	public static final HPPCFactory INSTANCE = new HPPCFactory();

	private HPPCFactory() {
	}

	@Override
	public int getDefaultExpectedElements() {
		return Containers.DEFAULT_EXPECTED_ELEMENTS;
	}

	@Override
	public IntIndexedContainer createIntArrayList() {
		return new IntArrayList();
	}

	@Override
	public IntIndexedContainer createIntArrayList(int expectedElements) {
		return new IntArrayList(expectedElements);
	}

	@Override
	public IntIndexedContainer createIntArrayList(IntContainer source) {
		return new IntArrayList(source);
	}

	@Override
	public IntSet createIntHashSet() {
		return new IntHashSet();
	}

	@Override
	public IntSet createIntHashSet(int expectedElements) {
		return new IntHashSet(expectedElements);
	}

	@Override
	public IntSet createIntHashSet(IntContainer source) {
		return new IntHashSet(source);
	}

	@Override
	public <T> ObjectIndexedContainer<T> createObjectArrayList() {
		return new ObjectArrayList<>();
	}

	@Override
	public <T> ObjectIndexedContainer<T> createObjectArrayList(int expectedElements) {
		return new ObjectArrayList<>(expectedElements);
	}

	@Override
	public <T> ObjectIndexedContainer<T> createObjectArrayList(ObjectContainer<? extends T> source) {
		return new ObjectArrayList<>(source);
	}

	@Override
	public <T> ObjectSet<T> createObjectHashSet() {
		return new ObjectHashSet<>();
	}

	@Override
	public <T> ObjectSet<T> createObjectHashSet(int expectedElements) {
		return new ObjectHashSet<>(expectedElements);
	}

	@Override
	public <T> ObjectSet<T> createObjectHashSet(ObjectContainer<? extends T> source) {
		return new ObjectHashSet<>(source);
	}

	@Override
	public <T> ObjectSet<T> createObjectIdentityHashSet() {
		return new ObjectIdentityHashSet<>();
	}

	@Override
	public <T> ObjectSet<T> createObjectIdentityHashSet(int expectedElements) {
		return new ObjectIdentityHashSet<>(expectedElements);
	}

	@Override
	public <T> ObjectSet<T> createObjectIdentityHashSet(ObjectContainer<? extends T> source) {
		return new ObjectIdentityHashSet<>(source);
	}

	@Override
	public <V> IntObjectMap<V> createIntObjectHashMap() {
		return new IntObjectHashMap<>();
	}

	@Override
	public <V> IntObjectMap<V> createIntObjectHashMap(int expectedElements) {
		return new IntObjectHashMap<>(expectedElements);
	}

	@Override
	public <V> IntObjectMap<V> createIntObjectHashMap(IntObjectAssociativeContainer<? extends V> source) {
		return new IntObjectHashMap<>(source);
	}

	@Override
	public <K> ObjectIntMap<K> createObjectIntHashMap() {
		return new ObjectIntHashMap<>();
	}

	@Override
	public <K> ObjectIntMap<K> createObjectIntHashMap(int expectedElements) {
		return new ObjectIntHashMap<>(expectedElements);
	}

	@Override
	public <K> ObjectIntMap<K> createObjectIntHashMap(ObjectIntAssociativeContainer<? extends K> source) {
		return new ObjectIntHashMap<>(source);
	}

	@Override
	public <K> ObjectIntMap<K> createObjectIntIdentityHashMap() {
		return new ObjectIntIdentityHashMap<>();
	}

	@Override
	public <K> ObjectIntMap<K> createObjectIntIdentityHashMap(int expectedElements) {
		return new ObjectIntIdentityHashMap<>(expectedElements);
	}

	@Override
	public <K> ObjectIntMap<K> createObjectIntIdentityHashMap(ObjectIntAssociativeContainer<? extends K> source) {
		return new ObjectIntIdentityHashMap<>(source);
	}

	@Override
	public IntIntMap createIntIntHashMap() {
		return new IntIntHashMap();
	}

	@Override
	public IntIntMap createIntIntHashMap(int expectedElements) {
		return new IntIntHashMap(expectedElements);
	}

	@Override
	public IntIntMap createIntIntHashMap(IntIntAssociativeContainer source) {
		return new IntIntHashMap(source);
	}

	@Override
	public <K, V> ObjectObjectMap<K, V> createObjectObjectHashMap() {
		return new ObjectObjectHashMap<>();
	}

	@Override
	public <K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(int expectedElements) {
		return new ObjectObjectHashMap<>(expectedElements);
	}

	@Override
	public <K, V> ObjectObjectMap<K, V> createObjectObjectHashMap(
			ObjectObjectAssociativeContainer<? extends K, ? extends V> source) {
		return new ObjectObjectHashMap<>(source);
	}

	@Override
	public <K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap() {
		return new ObjectObjectIdentityHashMap<>();
	}

	@Override
	public <K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(int expectedElements) {
		return new ObjectObjectIdentityHashMap<>(expectedElements);
	}

	@Override
	public <K, V> ObjectObjectMap<K, V> createObjectObjectIdentityHashMap(
			ObjectObjectAssociativeContainer<? extends K, ? extends V> source) {
		return new ObjectObjectIdentityHashMap<>(source);
	}

}
