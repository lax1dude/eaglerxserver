package net.lax1dude.eaglercraft.backend.server.api.query;

import java.net.SocketAddress;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IQueryConnection extends IAttributeHolder {

	boolean isClosed();

	void close();

	SocketAddress getRemoteAddress();

	String getRealAddress();

	IEaglerListenerInfo getListenerInfo();

	String getAccept();

	String getHeader(EnumWebSocketHeader header);

	void setStringHandler(Consumer<String> handler);

	void setBinaryHandler(Consumer<byte[]> handler);

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
