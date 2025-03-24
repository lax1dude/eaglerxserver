package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.UUID;

public interface IBasePlayer<PlayerObject> extends IRPCAttributeHolder {

	IEaglerXServerRPC<PlayerObject> getServerAPI();

	boolean isEaglerPlayer();

	IEaglerPlayer<PlayerObject> asEaglerPlayer();

	UUID getUniqueId();

	String getUsername();

	IRPCHandle<IBasePlayerRPC<PlayerObject>> getHandleBase();

}
