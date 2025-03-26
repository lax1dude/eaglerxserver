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

	void changePlayerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers);

	void changePlayerSkin(EnumPresetSkins newSkin, boolean notifyOthers);

	void changePlayerCape(IEaglerPlayerCape newCape, boolean notifyOthers);

	void changePlayerCape(EnumPresetCapes newCape, boolean notifyOthers);

	void resetPlayerSkin(boolean notifyOthers);

	void resetPlayerCape(boolean notifyOthers);

	void resetPlayerSkinAndCape(boolean notifyOthers);

}
