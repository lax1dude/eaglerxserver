package net.lax1dude.eaglercraft.backend.server.api.query;

import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.IAttributeHolder;
import net.lax1dude.eaglercraft.backend.server.api.IAttributeKey;

public interface IMOTDConnection extends IAttributeHolder {

	IQueryConnection getSocket();

	default <T> T get(IAttributeKey<T> key) {
		return getSocket().get(key);
	}

	default <T> void set(IAttributeKey<T> key, T value) {
		getSocket().set(key, value);
	}

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
