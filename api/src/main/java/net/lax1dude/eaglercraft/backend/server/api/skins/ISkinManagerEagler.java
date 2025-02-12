package net.lax1dude.eaglercraft.backend.server.api.skins;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface ISkinManagerEagler<PlayerObject> extends ISkinManagerBase<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IEaglerPlayerSkin getEaglerSkin();

	IEaglerPlayerCape getEaglerCape();

	void changeEaglerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers);

	void changeEaglerCape(IEaglerPlayerCape newCape, boolean notifyOthers);

	void resetEaglerSkin(boolean notifyOthers);

	void resetEaglerCape(boolean notifyOthers);

	void resetEaglerSkinAndCape(boolean notifyOthers);

	void setClientFNAWSkinsEnabled(boolean enabled);

	void setClientFNAWSkinsForced(boolean forced);

	void setClientFNAWSkinsEnabledForced(boolean enabled, boolean forced);

	void resetClientFNAWSkins();

	void resetEaglerSkinAndCapeAndClientFNAWSkins(boolean notifyOthers);

}
