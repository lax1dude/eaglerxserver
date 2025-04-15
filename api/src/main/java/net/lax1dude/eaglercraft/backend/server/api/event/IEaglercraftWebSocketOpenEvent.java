package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IEaglercraftWebSocketOpenEvent<PlayerObject> extends IBaseServerEvent<PlayerObject>, ICancellableEvent {

	@Nonnull
	IEaglerConnection getConnection();

	@Nullable
	String getRawHeader(@Nonnull String name);

	@Nonnull
	List<String> getRawHeaders(@Nonnull String name);

	@Nonnull
	NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		FullHttpRequest getHttpRequest();

	}

}
