package net.lax1dude.eaglercraft.backend.server.config.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class GSONConfigList implements IEaglerConfList {

	private final GSONConfigBase owner;
	final JsonArray json;
	private final boolean exists;
	private boolean initialized;

	public GSONConfigList(GSONConfigBase owner, JsonArray json, boolean exists) {
		this.owner = owner;
		this.json = json;
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
	}

	@Override
	public IEaglerConfSection appendSection() {
		JsonObject object = new JsonObject();
		json.add(object);
		owner.modified = true;
		initialized = true;
		return new GSONConfigSection(owner, object, false);
	}

	@Override
	public IEaglerConfList appendList() {
		JsonArray array = new JsonArray();
		json.add(array);
		owner.modified = true;
		initialized = true;
		return new GSONConfigList(owner, array, false);
	}

	@Override
	public void appendInteger(int value) {
		json.add(value);
		owner.modified = true;
		initialized = true;
	}

	@Override
	public void appendString(String string) {
		json.add(string);
		owner.modified = true;
		initialized = true;
	}

	@Override
	public int getLength() {
		return json.size();
	}

	@Override
	public IEaglerConfSection getIfSection(int index) {
		if(index < 0 || index >= json.size()) return null;
		JsonElement el = json.get(index);
		if(el != null && el.isJsonObject()) {
			return new GSONConfigSection(owner, el.getAsJsonObject(), true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		if(index < 0 || index >= json.size()) return null;
		JsonElement el = json.get(index);
		if(el != null && el.isJsonArray()) {
			return new GSONConfigList(owner, el.getAsJsonArray(), true);
		}else {
			return null;
		}
	}

	@Override
	public boolean isInteger(int index) {
		if(index < 0 || index >= json.size()) return false;
		JsonElement el = json.get(index);
		return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber();
	}

	@Override
	public int getIfInteger(int index, int defaultVal) {
		if(index < 0 || index >= json.size()) return defaultVal;
		JsonElement el = json.get(index);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()) {
			return el.getAsInt();
		}else {
			return defaultVal;
		}
	}

	@Override
	public boolean isString(int index) {
		if(index < 0 || index >= json.size()) return false;
		JsonElement el = json.get(index);
		return el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isString();
	}

	@Override
	public String getIfString(int index, String defaultVal) {
		if(index < 0 || index >= json.size()) return defaultVal;
		JsonElement el = json.get(index);
		if(el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber()) {
			return el.getAsString();
		}else {
			return defaultVal;
		}
	}

}
