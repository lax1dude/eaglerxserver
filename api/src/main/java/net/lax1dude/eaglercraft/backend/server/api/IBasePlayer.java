package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;

public interface IBasePlayer<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	PlayerObject getPlayerObject();

	String getUsername();

	UUID getUniqueId();

	SocketAddress getRemoteAddress();

	boolean isEaglerPlayer();

	int getMinecraftProtocol();

	String getMinecraftBrand();

	boolean isConnected();

	boolean isOnlineMode();

	ISkinManagerBase<PlayerObject> getSkinManager();

}
