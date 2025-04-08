package net.lax1dude.eaglercraft.backend.eaglermotd.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class GsonUtil {

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

	public static JsonObject loadJSONFile(File phile) throws IOException {
		JsonElement el;
		try(Reader reader = new InputStreamReader(new FileInputStream(phile), StandardCharsets.UTF_8)) {
			el = JsonParser.parseReader(reader);
		}
		if(!el.isJsonObject()) {
			throw new IOException("Root node is not a JsonObject!");
		}
		return el.getAsJsonObject();
	}

}
