package net.lax1dude.eaglercraft.backend.server.api.query;

import java.net.SocketAddress;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;

public interface IMOTDConnection extends IAttributeHolder {

	boolean isClosed();

	void close();

	SocketAddress getRemoteAddress();

	String getRealAddress();

	IEaglerListenerInfo getListenerInfo();

	String getAccept();

	String getHeader(EnumWebSocketHeader header);

	void sendToUser();

	long getAge();

	void setMaxAge(long millis);

	long getMaxAge();

	default boolean shouldKeepAlive() {
		return getMaxAge() > 0l;
	}

	int[] getBitmap();

	void setBitmap(int[] bitmap);

	String getLine1();

	void setLine1(String str);

	String getLine2();

	void setLine2(String str);

	int getPlayerTotal();

	void setPlayerTotal(int total);

	int getPlayerMax();

	void setPlayerMax(int total);

	List<String> getPlayerList();

	void setPlayerList(List<String> list);

}
