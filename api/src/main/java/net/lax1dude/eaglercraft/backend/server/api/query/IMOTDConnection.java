package net.lax1dude.eaglercraft.backend.server.api.query;

import java.net.SocketAddress;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IMOTDConnection extends INettyChannel, IAttributeHolder {

	boolean isClosed();

	void close();

	SocketAddress getRemoteAddress();

	String getRealAddress();

	IEaglerListenerInfo getListenerInfo();

	String getAccept();

	String getSubType();

	String getResponseType();

	void setResponseType(String type);

	String getHeader(EnumWebSocketHeader header);

	void sendToUser();

	long getAge();

	void setMaxAge(long millis);

	long getMaxAge();

	default boolean shouldKeepAlive() {
		return getMaxAge() > 0l;
	}

	byte[] getServerIcon();

	void setServerIcon(byte[] bitmap);

	List<String> getServerMOTD();

	void setServerMOTD(List<String> motd);

	int getPlayerTotal();

	void setPlayerTotal(int total);

	int getPlayerMax();

	void setPlayerMax(int total);

	default void setPlayerUnlimited() {
		setPlayerMax(-1);
	}

	List<String> getPlayerList();

	void setPlayerList(List<String> list);

}
