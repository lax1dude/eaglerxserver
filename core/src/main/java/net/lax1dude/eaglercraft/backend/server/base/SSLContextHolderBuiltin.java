package net.lax1dude.eaglercraft.backend.server.base;

import java.io.ByteArrayInputStream;

import javax.net.ssl.SSLException;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

public class SSLContextHolderBuiltin implements ISSLContextProvider {

	protected final String name;
	protected final String password;
	protected byte[] pubKey;
	protected byte[] privKey;
	protected volatile SslContext ctx;

	protected SSLContextHolderBuiltin(String name, String password) {
		this.name = name;
		this.password = password;
	}

	@Override
	public SslHandler newHandler(ByteBufAllocator alloc) {
		return ctx.newHandler(alloc);
	}

	protected void refresh() throws SSLException {
		ctx = SslContextBuilder.forServer(new ByteArrayInputStream(pubKey), new ByteArrayInputStream(privKey), password).build();
	}

}
