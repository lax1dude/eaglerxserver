package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

public interface IEaglerListenerInfo {

	String getName();

	SocketAddress getAddress();

	boolean isDualStack();

	boolean isTLSEnabled();

	boolean isTLSRequired();

	boolean isTLSManagedByPlugin();

	ITLSManager getTLSManager() throws IllegalStateException;

}
