package net.lax1dude.eaglercraft.backend.server.api.skins;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface ISkinManagerEagler<PlayerObject> extends ISkinManagerBase<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IEaglerPlayerSkin getEaglerSkin();

	IEaglerPlayerCape getEaglerCape();

	EnumEnableFNAW getEnableFNAWSkins();

	void setEnableFNAWSkins(EnumEnableFNAW enabled);

	void resetEnableFNAWSkins();

	boolean isFNAWSkinsServerManaged();

	void setFNAWSkinsServerManaged(boolean managed);

}
