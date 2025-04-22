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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@SuppressWarnings("unchecked")
public class Collectors3 {

	public static final Collector<Object, ?, List<Object>> IMMUTABLE_LIST;
	public static final Collector<Object, ?, Set<Object>> IMMUTABLE_SET;

	static {
		Collector<Object, ?, List<Object>> c1;
		try {
			c1 = (Collector<Object, ?, List<Object>>) ImmutableList.class.getMethod("toImmutableList").invoke(null);
		}catch(ReflectiveOperationException ex) {
			c1 = Collectors.toUnmodifiableList();
		}
		Collector<Object, ?, Set<Object>> c2;
		try {
			c2 = (Collector<Object, ?, Set<Object>>) ImmutableSet.class.getMethod("toImmutableSet").invoke(null);
		}catch(ReflectiveOperationException ex) {
			c2 = Collectors.toUnmodifiableSet();
		}
		IMMUTABLE_LIST = c1;
		IMMUTABLE_SET = c2;
	}

	@SuppressWarnings("rawtypes")
	public static <E> Collector<E, ?, List<E>> toImmutableList() {
		return (Collector) IMMUTABLE_LIST;
	}

	@SuppressWarnings("rawtypes")
	public static <E> Collector<E, ?, Set<E>> toImmutableSet() {
		return (Collector) IMMUTABLE_SET;
	}

}
