package net.lax1dude.eaglercraft.backend.server.api;

import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface ITLSManager {

	IEaglerListenerInfo getListener();

	default void setCertificate(File fullChain, File privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(File fullChain, File privateKey, String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(InputStream fullChain, InputStream privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(InputStream fullChain, InputStream privateKey, String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(byte[] fullChain, byte[] privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(byte[] fullChain, byte[] privateKey, String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(X509Certificate[] fullChain, PrivateKey privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(X509Certificate[] fullChain, PrivateKey privateKey, String privateKeyPassword) throws TLSManagerException;

}
