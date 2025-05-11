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

package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class NightConfigSection implements IEaglerConfSection {

	private final NightConfigBase owner;
	final CommentedConfig config;
	private final Consumer<String> commentSetter;
	private final boolean exists;
	private boolean initialized;

	public NightConfigSection(NightConfigBase owner, CommentedConfig config, Consumer<String> commentSetter,
			boolean exists) {
		this.owner = owner;
		this.config = config;
		this.commentSetter = commentSetter;
		this.exists = this.initialized = exists;
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	@Override
	public void setComment(String comment) {
		if (commentSetter != null) {
			commentSetter.accept(NightConfigLoader.createComment(comment));
			owner.modified = true;
		}
	}

	@Override
	public IEaglerConfSection getIfSection(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof CommentedConfig o2)
				? new NightConfigSection(owner, o2, (str) -> config.setComment(k, str), true)
				: null;
	}

	@Override
	public IEaglerConfSection getSection(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof CommentedConfig o2) {
			return new NightConfigSection(owner, o2, (str) -> config.setComment(k, str), true);
		} else {
			CommentedConfig sub = config.createSubConfig();
			config.set(k, sub);
			owner.modified = true;
			initialized = true;
			return new NightConfigSection(owner, sub, (str) -> config.setComment(k, str), false);
		}
	}

	private NightConfigList.IContext bindListContext(List<String> key) {
		return new NightConfigList.IContext() {
			@Override
			public void setComment(String comment) {
				config.setComment(key, comment);
			}

			@Override
			public CommentedConfig genSection() {
				return config.createSubConfig();
			}
		};
	}

	@Override
	public IEaglerConfList getIfList(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof List) ? new NightConfigList(owner, (List<Object>) o, bindListContext(k), true) : null;
	}

	@Override
	public IEaglerConfList getList(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof List) {
			return new NightConfigList(owner, (List<Object>) o, bindListContext(k), true);
		} else {
			List<Object> sub = new ArrayList<>();
			config.set(k, sub);
			owner.modified = true;
			initialized = true;
			return new NightConfigList(owner, sub, bindListContext(k), false);
		}
	}

	@Override
	public List<String> getKeys() {
		return ImmutableList.copyOf(config.valueMap().keySet());
	}

	@Override
	public boolean isBoolean(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof Boolean);
	}

	@Override
	public boolean getBoolean(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof Boolean o2) && o2;
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue, String comment) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof Boolean o2) {
			return o2;
		} else {
			config.set(k, defaultValue);
			if (comment != null) {
				config.setComment(k, NightConfigLoader.createComment(comment));
			}
			owner.modified = true;
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof Boolean o2) {
			return o2;
		} else {
			Boolean d = defaultValue.get();
			config.set(k, d);
			if (comment != null) {
				config.setComment(k, NightConfigLoader.createComment(comment));
			}
			owner.modified = true;
			initialized = true;
			return d;
		}
	}

	@Override
	public boolean isInteger(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof Number);
	}

	@Override
	public int getInteger(String name, int defaultValue, String comment) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof Number o2) {
			return o2.intValue();
		} else {
			config.set(k, defaultValue);
			if (comment != null) {
				config.setComment(k, NightConfigLoader.createComment(comment));
			}
			owner.modified = true;
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public int getInteger(String name, Supplier<Integer> defaultValue, String comment) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof Number o2) {
			return o2.intValue();
		} else {
			Integer d = defaultValue.get();
			config.set(k, d);
			if (comment != null) {
				config.setComment(k, NightConfigLoader.createComment(comment));
			}
			owner.modified = true;
			initialized = true;
			return d;
		}
	}

	@Override
	public boolean isString(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof String);
	}

	@Override
	public String getIfString(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof String str) ? str : null;
	}

	@Override
	public String getString(String name, String defaultValue, String comment) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof String str) {
			return str;
		} else {
			config.set(k, defaultValue);
			if (comment != null) {
				config.setComment(k, NightConfigLoader.createComment(comment));
			}
			owner.modified = true;
			initialized = true;
			return defaultValue;
		}
	}

	@Override
	public String getString(String name, Supplier<String> defaultValue, String comment) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if (o instanceof String str) {
			return str;
		} else {
			String d = defaultValue.get();
			config.set(k, d);
			if (comment != null) {
				config.setComment(k, NightConfigLoader.createComment(comment));
			}
			owner.modified = true;
			initialized = true;
			return d;
		}
	}

}
