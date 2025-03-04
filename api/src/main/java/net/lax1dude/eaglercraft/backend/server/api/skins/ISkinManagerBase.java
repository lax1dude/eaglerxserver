package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;

public interface ISkinManagerBase<PlayerObject> {

	IBasePlayer<PlayerObject> getPlayer();

	ISkinService<PlayerObject> getSkinService();

	boolean isEaglerPlayer();

	ISkinManagerEagler<PlayerObject> asEaglerPlayer();

	IEaglerPlayerSkin getPlayerSkinIfLoaded();

	IEaglerPlayerCape getPlayerCapeIfLoaded();

	void resolvePlayerSkin(Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCape(Consumer<IEaglerPlayerCape> callback);

}
