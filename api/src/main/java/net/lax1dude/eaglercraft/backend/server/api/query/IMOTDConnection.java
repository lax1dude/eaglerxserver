package net.lax1dude.eaglercraft.backend.server.api.query;

import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IMOTDConnection extends IEaglerConnection {

	String getAccept();

	String getSubType();

	String getResponseType();

	void setResponseType(String type);

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
