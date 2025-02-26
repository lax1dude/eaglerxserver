package net.lax1dude.eaglercraft.backend.server.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.ITLSManager;
import net.lax1dude.eaglercraft.backend.server.api.TLSManagerException;

public class SSLContextHolderPlugin implements ISSLContextProvider, ITLSManager {

	private final EaglerListener listener;
	private SslContext ctx;

	public SSLContextHolderPlugin(EaglerListener listener) {
		this.listener = listener;
	}

	@Override
	public IEaglerListenerInfo getListener() {
		return listener;
	}

	@Override
	public void setCertificate(File fullChain, File privateKey, String privateKeyPassword) throws TLSManagerException {
		try {
			ctx = SslContextBuilder.forServer(fullChain, privateKey, privateKeyPassword).build();
		}catch(Exception ex) {
			throw propigateTLSManagerException(ex);
		}
	}

	@Override
	public void setCertificate(InputStream fullChain, InputStream privateKey, String privateKeyPassword) throws TLSManagerException {
		try {
			ctx = SslContextBuilder.forServer(fullChain, privateKey, privateKeyPassword).build();
		}catch(Exception ex) {
			throw propigateTLSManagerException(ex);
		}
	}

	@Override
	public void setCertificate(byte[] fullChain, byte[] privateKey, String privateKeyPassword) throws TLSManagerException {
		try {
			ctx = SslContextBuilder.forServer(new ByteArrayInputStream(fullChain), new ByteArrayInputStream(privateKey),
					privateKeyPassword).build();
		}catch(Exception ex) {
			throw propigateTLSManagerException(ex);
		}
	}

	@Override
	public void setCertificate(X509Certificate[] fullChain, PrivateKey privateKey, String privateKeyPassword) throws TLSManagerException {
		try {
			ctx = SslContextBuilder.forServer(privateKey, privateKeyPassword, fullChain).build();
		}catch(Exception ex) {
			throw propigateTLSManagerException(ex);
		}
	}

	private TLSManagerException propigateTLSManagerException(Exception ex) {
		if((ex instanceof IllegalArgumentException) || (ex instanceof CertificateException)) {
			return new TLSManagerException(ex.getMessage(), ex.getCause());
		}else {
			return new TLSManagerException("Uncaught exception creating TLS context!", ex.getCause());
		}
	}

	@Override
	public SslHandler newHandler(ByteBufAllocator alloc) {
		return ctx != null ? ctx.newHandler(alloc) : null;
	}

}
