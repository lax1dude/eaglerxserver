package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonMap {

	private static final Field mapField;

	static {
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

	public static Map<String, JsonElement> asMap(JsonObject object) {
		if(mapField == null) {
			return object.asMap();
		}else {
			try {
				return (Map<String, JsonElement>) mapField.get(object);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}
	}

}
