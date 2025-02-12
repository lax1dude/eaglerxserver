package net.lax1dude.eaglercraft.eaglerxserver.api.players;

import java.net.SocketAddress;
import java.util.UUID;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.eaglerxserver.api.skins.ISkinManagerBase;

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
