package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IEaglerListenerInfo extends IAttributeHolder {

	@Nonnull
	String getName();

	@Nonnull
	SocketAddress getAddress();

	boolean isDualStack();

	boolean isTLSEnabled();

	boolean isTLSRequired();

	boolean isTLSManagedByPlugin();

	@Nonnull
	ITLSManager getTLSManager() throws IllegalStateException;

	@Nullable
	byte[] getServerIcon();

	void setServerIcon(@Nullable byte[] pixels);

	@Nonnull
	List<String> getServerMOTD();

	void setServerMOTD(@Nullable List<String> motd);

	boolean isForwardIP();

}
