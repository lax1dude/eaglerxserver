package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public interface ISkinManagerImpl {

	IEaglerPlayerSkin getPlayerSkinIfLoaded();

	IEaglerPlayerCape getPlayerCapeIfLoaded();

	void resolvePlayerSkinKeyed(UUID requester, Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCapeKeyed(UUID requester, Consumer<IEaglerPlayerCape> callback);

	void resolvePlayerTexturesKeyed(UUID requester, BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback);

}
