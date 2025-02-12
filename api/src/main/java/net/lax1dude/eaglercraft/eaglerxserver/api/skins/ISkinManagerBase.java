package net.lax1dude.eaglercraft.eaglerxserver.api.skins;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.eaglerxserver.api.players.IBasePlayer;

public interface ISkinManagerBase<PlayerObject> {

	IBasePlayer<PlayerObject> getPlayer();

	boolean isEaglerPlayer();

	IEaglerPlayerSkin getPlayerSkinIfLoaded();

	IEaglerPlayerCape getPlayerCapeIfLoaded();

	void resolvePlayerSkin(Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCape(Consumer<IEaglerPlayerCape> callback);

}
