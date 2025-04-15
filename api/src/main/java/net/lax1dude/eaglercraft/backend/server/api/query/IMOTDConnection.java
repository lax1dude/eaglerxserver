package net.lax1dude.eaglercraft.backend.server.api.query;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IMOTDConnection extends IEaglerConnection {

	@Nonnull
	String getAccept();

	@Nullable
	String getSubType();

	@Nonnull
	String getResponseType();

	void setResponseType(@Nonnull String type);

	void sendToUser();

	long getAge();

	void setMaxAge(long millis);

	long getMaxAge();

	default boolean shouldKeepAlive() {
		return getMaxAge() > 0l;
	}

	@Nullable
	byte[] getDefaultServerIcon();

	@Nullable
	byte[] getServerIcon();

	void setServerIcon(@Nullable byte[] bitmap);

	@Nonnull
	List<String> getDefaultServerMOTD();

	@Nonnull
	List<String> getServerMOTD();

	void setServerMOTD(@Nonnull List<String> motd);

	int getDefaultPlayerTotal();

	int getPlayerTotal();

	void setPlayerTotal(int total);

	int getDefaultPlayerMax();

	int getPlayerMax();

	void setPlayerMax(int total);

	default void setPlayerUnlimited() {
		setPlayerMax(-1);
	}

	@Nonnull
	List<String> getDefaultPlayerList();

	@Nonnull
	List<String> getPlayerList();

	void setPlayerList(@Nonnull List<String> list);

}
