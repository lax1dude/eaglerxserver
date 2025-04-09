package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;

public interface ISupervisorResolverImpl extends ISupervisorResolver {

	void resolvePlayerSkinKeyed(UUID requester, UUID playerUUID, Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCapeKeyed(UUID requester, UUID playerUUID, Consumer<IEaglerPlayerCape> callback);

	void resolvePlayerBrandKeyed(UUID requester, Consumer<UUID> callback);

	boolean onForeignSkinReceived(UUID playerUUID, IEaglerPlayerSkin skin);

	boolean onForeignCapeReceived(UUID playerUUID, IEaglerPlayerCape cape);

}
