package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

public interface IBaseLoginConnection extends IBasePendingConnection {

	UUID getUniqueId();

	String getUsername();

	boolean isOnlineMode();

	IEaglerLoginConnection asEaglerPlayer();

}
