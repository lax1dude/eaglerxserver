package net.lax1dude.eaglercraft.backend.server.api;

import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITLSManager {

	@Nonnull
	IEaglerListenerInfo getListener();

	default void setCertificate(@Nonnull File fullChain, @Nonnull File privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull File fullChain, @Nonnull File privateKey, @Nullable String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(@Nonnull InputStream fullChain, @Nonnull InputStream privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull InputStream fullChain, @Nonnull InputStream privateKey, @Nullable String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(@Nonnull byte[] fullChain, @Nonnull byte[] privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull byte[] fullChain, @Nonnull byte[] privateKey, @Nullable String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(@Nonnull X509Certificate[] fullChain, @Nonnull PrivateKey privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull X509Certificate[] fullChain, @Nonnull PrivateKey privateKey, @Nullable String privateKeyPassword) throws TLSManagerException;

}
