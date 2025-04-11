package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.List;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IEaglercraftWebSocketOpenEvent<PlayerObject> extends IBaseServerEvent<PlayerObject>, ICancellableEvent {

	IEaglerConnection getConnection();

	String getRawHeader(String name);

	List<String> getRawHeaders(String name);

	NettyUnsafe netty();

	public interface NettyUnsafe {

		FullHttpRequest getHttpRequest();

	}

}
