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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;

public class EaglerAttributeManager implements IAttributeManager {

	private abstract class AttributeKeyBase<T> implements IAttributeKey<T> {

		protected final Class<T> type;

		protected AttributeKeyBase(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		protected final void validate(EaglerAttributeManager mgr) {
			if(mgr != EaglerAttributeManager.this) {
				throw new IllegalStateException();
			}
		}

	}

	private class AttributeKeyGlobal<T> extends AttributeKeyBase<T> {

		protected final String name;

		protected AttributeKeyGlobal(Class<T> type, String name) {
			super(type);
			this.name = name;
		}

		@Override
		public boolean isGlobal() {
			return true;
		}

		@Override
		public String getName() {
			return name;
		}

		public String toString() {
			return "global:" + name;
		}

	}

	private class AttributeKeyPrivate<T> extends AttributeKeyBase<T> {

		protected final String name;

		protected AttributeKeyPrivate(Class<T> type, String name) {
			super(type);
			this.name = name;
		}

		@Override
		public boolean isGlobal() {
			return false;
		}

		@Override
		public String getName() {
			return name;
		}

		public String toString() {
			return "private:" + name + "#" + Integer.toHexString(hashCode());
		}

	}

	private final Map<String, IAttributeKey<?>> globals = new HashMap<>();

	@Override
	public <T> IAttributeKey<T> initGlobalAttribute(String name, Class<T> type) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		if(type == null) {
			throw new NullPointerException("type");
		}
		IAttributeKey<?> ret;
		synchronized(globals) {
			ret = globals.get(name);
			if(ret == null) {
				IAttributeKey<T> tmp;
				globals.put(name, tmp = new AttributeKeyGlobal<>(type, name));
				return tmp;
			}
		}
		if (ret.getType() != type) {
			throw new IllegalStateException("Attribute type conflict for \"" + name + "\": " + type.getName()
					+ " != " + ret.getType().getName());
		}
		return (IAttributeKey<T>) ret;
	}

	@Override
	public <T> IAttributeKey<T> initPrivateAttribute(String name, Class<T> type) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		if(type == null) {
			throw new NullPointerException("type");
		}
		return new AttributeKeyPrivate<>(type, name);
	}

	@Override
	public <T> IAttributeKey<T> initPrivateAttribute(Class<T> type) {
		if(type == null) {
			throw new NullPointerException("type");
		}
		return new AttributeKeyPrivate<>(type, "unnamed");
	}

	public class EaglerAttributeHolder implements IAttributeHolder {

		private final ConcurrentMap<AttributeKeyBase<?>, Object> attribMap = new ConcurrentHashMap<>(4);

		@Override
		public final <T> T get(IAttributeKey<T> key) {
			if(key == null) {
				throw new NullPointerException("key");
			}
			AttributeKeyBase<?> checkCast = (AttributeKeyBase<?>) key;
			checkCast.validate(EaglerAttributeManager.this);
			return (T) attribMap.get(checkCast);
		}

		@Override
		public final <T> void set(IAttributeKey<T> key, T value) {
			if(key == null) {
				throw new NullPointerException("key");
			}
			AttributeKeyBase<?> checkCast = (AttributeKeyBase<?>) key;
			checkCast.validate(EaglerAttributeManager.this);
			if(value != null) {
				attribMap.put(checkCast, value);
			}else {
				attribMap.remove(checkCast);
			}
		}

	}

	public IAttributeHolder createHolder() {
		return new EaglerAttributeHolder();
	}

	public EaglerAttributeHolder createEaglerHolder() {
		return new EaglerAttributeHolder();
	}

}
