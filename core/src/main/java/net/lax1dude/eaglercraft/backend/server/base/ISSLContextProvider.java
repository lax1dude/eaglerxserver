package net.lax1dude.eaglercraft.backend.server.base;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslHandler;

public interface ISSLContextProvider {

	SslHandler newHandler(ByteBufAllocator alloc);

}
