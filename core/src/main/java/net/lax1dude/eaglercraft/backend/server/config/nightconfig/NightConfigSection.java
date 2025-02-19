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

	private final CommentedConfig config;
	private final Consumer<String> commentSetter;
	private final boolean exists;
	private boolean initialized;

	public NightConfigSection(CommentedConfig config, Consumer<String> commentSetter, boolean exists) {
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
		if(commentSetter != null) {
			commentSetter.accept(comment);
		}
	}

	@Override
	public IEaglerConfSection getIfSection(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof CommentedConfig)
				? new NightConfigSection((CommentedConfig) o, (str) -> config.setComment(k, str), true)
				: null;
	}

	@Override
	public IEaglerConfSection getSection(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if(o instanceof CommentedConfig) {
			return new NightConfigSection((CommentedConfig)o, (str) -> config.setComment(k, str), true);
		}else {
			CommentedConfig sub = config.createSubConfig();
			config.add(k, sub);
			return new NightConfigSection(sub, (str) -> config.setComment(k, str), false);
		}
	}

	@Override
	public IEaglerConfList getIfList(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		return (o instanceof List)
				? new NightConfigList((List<Object>) o, (str) -> config.setComment(k, str), true)
				: null;
	}

	@Override
	public IEaglerConfList getList(String name) {
		List<String> k = Collections.singletonList(name);
		Object o = config.get(k);
		if(o instanceof CommentedConfig) {
			return new NightConfigList((List<Object>) o, (str) -> config.setComment(k, str), true);
		}else {
			List<Object> sub = new ArrayList<>();
			config.add(k, sub);
			return new NightConfigList(sub, (str) -> config.setComment(k, str), false);
		}
	}

	@Override
	public List<String> getKeys() {
		return ImmutableList.copyOf(config.valueMap().keySet());
	}

	@Override
	public boolean isBoolean(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBoolean(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue, String comment) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInteger(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInteger(String name, int defaultValue, String comment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInteger(String name, Supplier<Integer> defaultValue, String comment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isString(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getIfString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String name, String defaultValue, String comment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String name, Supplier<String> defaultValue, String comment) {
		// TODO Auto-generated method stub
		return null;
	}

}
