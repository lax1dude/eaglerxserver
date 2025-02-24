package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.ByteArrayInputStream;

import javax.net.ssl.SSLException;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

public class SSLContextHolder {

	protected final String name;
	protected byte[] pubKey;
	protected byte[] privKey;
	protected volatile SslContext ctx = null;

	protected SSLContextHolder(String name) {
		this.name = name;
	}

	public SslHandler newHandler(ByteBufAllocator alloc) {
		return ctx.newHandler(alloc);
	}

	protected void refresh() throws SSLException {
		ctx = SslContextBuilder.forServer(new ByteArrayInputStream(privKey), new ByteArrayInputStream(pubKey)).build();
	}

}
