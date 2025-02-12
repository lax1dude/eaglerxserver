package net.lax1dude.eaglercraft.backend.server.api.query;

import java.util.List;

public interface IMOTDConnection {

	IQueryConnection getSocket();

	void sendToUser();

	int[] getBitmap();

	void getBitmap(int[] bitmap);

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
