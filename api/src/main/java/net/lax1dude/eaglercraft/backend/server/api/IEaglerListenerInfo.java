package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IEaglerListenerInfo extends IAttributeHolder {

	String getName();

	SocketAddress getAddress();

	boolean isDualStack();

	boolean isTLSEnabled();

	boolean isTLSRequired();

	boolean isTLSManagedByPlugin();

	ITLSManager getTLSManager() throws IllegalStateException;

	byte[] getServerIcon();

	void setServerIcon(byte[] pixels);

	List<String> getServerMOTD();

	void setServerMOTD(List<String> motd);

	boolean isForwardIP();

}
