package net.lax1dude.eaglercraft.backend.server.config;

import java.util.List;
import java.util.function.Supplier;

public interface IEaglerConfSection {

	boolean exists();

	boolean initialized();

	void setComment(String comment);

	IEaglerConfSection getIfSection(String name);

	IEaglerConfSection getSection(String name);

	IEaglerConfList getIfList(String name);

	IEaglerConfList getList(String name);

	List<String> getKeys();

	boolean isBoolean(String name);

	boolean getBoolean(String name);

	boolean getBoolean(String name, boolean defaultValue, String comment);

	default boolean getBoolean(String name, boolean defaultValue) {
		return getBoolean(name, defaultValue, null);
	}

	boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment);

	default boolean getBoolean(String name, Supplier<Boolean> defaultValue) {
		return getBoolean(name, defaultValue, null);
	}

	boolean isInteger(String name);

	int getInteger(String name, int defaultValue, String comment);

	default int getInteger(String name, int defaultValue) {
		return getInteger(name, defaultValue, null);
	}

	int getInteger(String name, Supplier<Integer> defaultValue, String comment);

	default int getInteger(String name, Supplier<Integer> defaultValue) {
		return getInteger(name, defaultValue, null);
	}

	boolean isString(String name);

	String getIfString(String name);

	String getString(String name, String defaultValue, String comment);

	default String getString(String name, String defaultValue) {
		return getString(name, defaultValue, null);
	}

	String getString(String name, Supplier<String> defaultValue, String comment);

	default String getString(String name, Supplier<String> defaultValue) {
		return getString(name, defaultValue, null);
	}

}
