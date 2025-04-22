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

package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.Collections;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXBackendRPCFactory;

class APIFactoryImpl extends EaglerXBackendRPCFactory.Factory {

	static final APIFactoryImpl INSTANCE = new APIFactoryImpl();

	private Class<?> playerClass;
	private Set<Class<?>> playerClassSet;
	private IEaglerXBackendRPC<?> handle;

	private APIFactoryImpl() {
	}

	@Override
	public Set<Class<?>> getPlayerTypes() {
		Set<Class<?>> classSet = this.playerClassSet;
		if(classSet == null) {
			throw new IllegalStateException("EaglerXBackendRPC has not been initialized yet!");
		}
		return classSet;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IEaglerXBackendRPC<T> getAPI(Class<T> playerClass) {
		IEaglerXBackendRPC<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXBackendRPC has not been initialized yet!");
		}
		if(!playerClass.isAssignableFrom(this.playerClass)) {
			throw new ClassCastException("Class \"" + this.playerClass.getName() + "\" cannot be cast to \"" + playerClass.getName() + "\"");
		}
		return (IEaglerXBackendRPC<T>) handle;
	}

	@Override
	public IEaglerXBackendRPC<?> getDefaultAPI() {
		IEaglerXBackendRPC<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXBackendRPC has not been initialized yet!");
		}
		return handle;
	}

	<T> void initialize(Class<T> playerClass, IEaglerXBackendRPC<T> handle) {
		this.playerClass = playerClass;
		this.playerClassSet = Collections.singleton(playerClass);
		this.handle = handle;
	}

	static EaglerXBackendRPCFactory.Factory createFactory() {
		return INSTANCE;
	}

}
