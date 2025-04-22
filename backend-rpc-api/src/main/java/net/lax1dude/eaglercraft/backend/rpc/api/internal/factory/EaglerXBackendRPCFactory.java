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

package net.lax1dude.eaglercraft.backend.rpc.api.internal.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public abstract class EaglerXBackendRPCFactory {

	@Nonnull
	public static final Factory INSTANCE;

	static {
		// Dependency injection? Never heard of it
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.rpc.base.APIFactoryImpl");
			Method meth = clz.getDeclaredMethod("createFactory");
			meth.setAccessible(true);
			INSTANCE = (Factory) meth.invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOperationException("Could not access the EaglerXBackendRPC factory!", e);
		}
	}

	protected EaglerXBackendRPCFactory() {
	}

	public static abstract class Factory implements IEaglerRPCFactory {

		@Nonnull
		@Override
		public abstract Set<Class<?>> getPlayerTypes();

		@Nonnull
		@Override
		public abstract <T> IEaglerXBackendRPC<T> getAPI(@Nonnull Class<T> playerClass);

		@Nonnull
		@Override
		public abstract IEaglerXBackendRPC<?> getDefaultAPI();

	}

}
