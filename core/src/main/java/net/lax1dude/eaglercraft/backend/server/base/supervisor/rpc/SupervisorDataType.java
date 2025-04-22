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

package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.SupervisorDataVoid;

class SupervisorDataType {

	private static final LoadingCache<Class<? extends ISupervisorData>, SupervisorDataType> dataTypeCache = CacheBuilder
			.newBuilder().weakKeys().weakValues()
			.build(new CacheLoader<Class<? extends ISupervisorData>, SupervisorDataType>() {
				@Override
				public SupervisorDataType load(Class<? extends ISupervisorData> var1) throws Exception {
					return new SupervisorDataType(var1);
				}
			});

	static final SupervisorDataType VOID_TYPE = new SupervisorDataType();

	protected final Class<? extends ISupervisorData> clazz;
	protected final Constructor<? extends ISupervisorData> ctor;

	private SupervisorDataType(Class<? extends ISupervisorData> clazz) {
		this.clazz = clazz;
		try {
			this.ctor = clazz.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Data class must define a default constructor with zero arguments!");
		}
	}

	private SupervisorDataType() {
		this.clazz = SupervisorDataVoid.class;
		this.ctor = null;
	}

	static SupervisorDataType provideType(Class<? extends ISupervisorData> clazz) {
		if(clazz == SupervisorDataVoid.class) {
			return VOID_TYPE;
		}else {
			try {
				return dataTypeCache.get(clazz);
			} catch (ExecutionException e) {
				if(e.getCause() instanceof RuntimeException ee) throw ee;
				throw new RuntimeException(e.getCause());
			}
		}
	}

}
