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

	boolean onForeignSkinReceivedPreset(UUID playerUUID, int presetId);

	boolean onForeignSkinReceivedCustom(UUID playerUUID, int modelId, byte[] pixels);

	boolean onForeignSkinReceivedError(UUID playerUUID);

	boolean onForeignCapeReceivedPreset(UUID playerUUID, int presetId);

	boolean onForeignCapeReceivedCustom(UUID playerUUID, byte[] pixels);

	boolean onForeignCapeReceivedError(UUID playerUUID);

}
