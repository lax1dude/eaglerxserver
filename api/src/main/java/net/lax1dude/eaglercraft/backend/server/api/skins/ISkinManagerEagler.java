package net.lax1dude.eaglercraft.backend.server.api.skins;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface ISkinManagerEagler<PlayerObject> extends ISkinManagerBase<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IEaglerPlayerSkin getEaglerSkin();

	IEaglerPlayerCape getEaglerCape();

	void changeEaglerSkin(IEaglerPlayerSkin newSkin, boolean notifyOthers);

	void changeEaglerSkin(EnumPresetSkins newSkin, boolean notifyOthers);

	void changeEaglerCape(IEaglerPlayerCape newCape, boolean notifyOthers);

	void changeEaglerCape(EnumPresetCapes newCape, boolean notifyOthers);

	void resetEaglerSkin(boolean notifyOthers);

	void resetEaglerCape(boolean notifyOthers);

	void resetEaglerSkinAndCape(boolean notifyOthers);

	EnumEnableFNAW getEnableFNAWSkins();

	void setEnableFNAWSkins(EnumEnableFNAW enabled);

	void resetEnableFNAWSkins();

	boolean isFNAWSkinsServerManaged();

	void setFNAWSkinsServerManaged(boolean managed);

}
