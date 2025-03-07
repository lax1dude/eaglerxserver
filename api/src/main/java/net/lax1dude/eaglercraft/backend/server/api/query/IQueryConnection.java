package net.lax1dude.eaglercraft.backend.server.api.query;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IQueryConnection extends IEaglerConnection {

	String getAccept();

	void setHandlers(IDuplexBaseHandler compositeHandler);

	void setHandlers(IDuplexBaseHandler... compositeHandlers);

	void setStringHandler(IDuplexStringHandler handler);

	void setJSONHandler(IDuplexJSONHandler handler);

	void setBinaryHandler(IDuplexBinaryHandler handler);

	long getAge();

	void setMaxAge(long millis);

	long getMaxAge();

	default boolean shouldKeepAlive() {
		return getMaxAge() > 0l;
	}

	void send(String string);

	void send(byte[] bytes);

	void sendResponse(String type, String str);

	void sendResponse(String type, JsonObject jsonObject);

}
