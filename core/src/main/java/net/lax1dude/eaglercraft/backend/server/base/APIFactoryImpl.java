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

package net.lax1dude.eaglercraft.backend.server.base;

import java.util.Collections;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;

class APIFactoryImpl extends EaglerXServerAPIFactory.Factory {

	static final APIFactoryImpl INSTANCE = new APIFactoryImpl();

	private Class<?> playerClass;
	private Set<Class<?>> playerClassSet;
	private final EaglerAttributeManager attributeManager = new EaglerAttributeManager();
	private IEaglerXServerAPI<?> handle;

	private APIFactoryImpl() {
	}

	@Override
	public Set<Class<?>> getPlayerTypes() {
		Set<Class<?>> classSet = this.playerClassSet;
		if(classSet == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		return classSet;
	}

	EaglerAttributeManager getEaglerAttribManager() {
		return attributeManager;
	}

	@Override
	public IAttributeManager getGlobalAttributeManager() {
		return attributeManager;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IEaglerXServerAPI<T> getAPI(Class<T> playerClass) {
		IEaglerXServerAPI<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		if(!playerClass.isAssignableFrom(this.playerClass)) {
			throw new ClassCastException("Class \"" + this.playerClass.getName() + "\" cannot be cast to \"" + playerClass.getName() + "\"");
		}
		return (IEaglerXServerAPI<T>) handle;
	}

	@Override
	public IEaglerXServerAPI<?> getDefaultAPI() {
		IEaglerXServerAPI<?> handle = this.handle;
		if(handle == null) {
			throw new IllegalStateException("EaglerXServer has not been initialized yet!");
		}
		return handle;
	}

	<T> void initialize(Class<T> playerClass, IEaglerXServerAPI<T> handle) {
		this.playerClass = playerClass;
		this.playerClassSet = Collections.singleton(playerClass);
		this.handle = handle;
	}

	static EaglerXServerAPIFactory.Factory createFactory() {
		return INSTANCE;
	}

}
