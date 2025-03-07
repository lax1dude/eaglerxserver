package net.lax1dude.eaglercraft.backend.server.api;

public interface IBasePendingConnection extends IBaseConnection {

	int getMinecraftProtocol();

	boolean isEaglerPlayer();

	IEaglerPendingConnection asEaglerPlayer();

}
