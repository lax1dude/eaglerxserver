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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class RPCAttributeKey<T> {

	private static final Cache<String, RPCAttributeKey<?>> globalAttrs = CacheBuilder.newBuilder().build();

	@Nonnull
	public static <T> RPCAttributeKey<T> createGlobal(@Nonnull String name, @Nonnull Class<T> type) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		if(type == null) {
			throw new NullPointerException("type");
		}
		RPCAttributeKey<?> ret;
		try {
			ret = globalAttrs.get(name, () -> {
				return new RPCAttributeKey<>(type);
			});
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(e.getCause());
		}
		if(ret.type != type) {
			throw new ClassCastException("Existing global attribute \"" + name + "\" registered type "
					+ ret.type.getName() + " does not match requested type " + type.getName());
		}
		return (RPCAttributeKey<T>) ret;
	}

	@Nonnull
	public static <T> RPCAttributeKey<T> createLocal(@Nonnull Class<T> type) {
		if(type == null) {
			throw new NullPointerException("type");
		}
		return new RPCAttributeKey<>(type);
	}

	private final Class<T> type;

	private RPCAttributeKey(Class<T> type) {
		this.type = type;
	}

	@Nonnull
	public Class<T> getType() {
		return type;
	}

}
