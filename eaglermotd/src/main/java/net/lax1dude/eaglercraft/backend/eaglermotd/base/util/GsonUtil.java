package net.lax1dude.eaglercraft.backend.eaglermotd.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GsonUtil {

	private static final Gson GSON;
	private static final Field mapField;

	static {
		try {
			GSON = (Gson) Class.forName("net.lax1dude.eaglercraft.backend.server.base.EaglerXServer")
					.getField("GSON_PRETTY").get(null);
		}catch(ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
		}
		Field _mapField = null;
		try {
			JsonObject.class.getMethod("asMap");
		}catch(ReflectiveOperationException ex) {
			try {
				_mapField = JsonObject.class.getDeclaredField("members");
				_mapField.setAccessible(true);
			}catch(ReflectiveOperationException exx) {
				throw new ExceptionInInitializerError(exx);
			}
		}
		mapField = _mapField;
	}

	public static JsonObject loadJSONFile(File phile) throws IOException {
		try(Reader reader = new InputStreamReader(new FileInputStream(phile), StandardCharsets.UTF_8)) {
			return GSON.fromJson(reader, JsonObject.class);
		}
	}

	public static String optString(JsonElement el, String def) {
		return (el != null && el.isJsonPrimitive() && ((JsonPrimitive)el).isString()) ? el.getAsString() : def;
	}

	public static int optInt(JsonElement el, int def) {
		return (el != null && el.isJsonPrimitive() && ((JsonPrimitive)el).isNumber()) ? el.getAsInt() : def;
	}

	public static boolean optBoolean(JsonElement el, boolean def) {
		if(el != null && el.isJsonPrimitive()) {
			JsonPrimitive prim = el.getAsJsonPrimitive();
			return prim.isBoolean() ? prim.getAsBoolean() : def;
		}else {
			return def;
		}
	}

	public static float optFloat(JsonElement el, float def) {
		if(el != null && el.isJsonPrimitive()) {
			JsonPrimitive prim = el.getAsJsonPrimitive();
			return prim.isNumber() ? prim.getAsFloat() : def;
		}else {
			return def;
		}
	}

	public static JsonArray optJSONArray(JsonElement el) {
		return (el != null && el instanceof JsonArray ell) ? ell : null;
	}

	public static Map<String, JsonElement> asMap(JsonObject object) {
		if(mapField == null) {
			return object.asMap();
		}else {
			try {
				return (Map<String, JsonElement>) mapField.get(object);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
