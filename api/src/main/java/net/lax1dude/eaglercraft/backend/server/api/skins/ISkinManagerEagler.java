package net.lax1dude.eaglercraft.backend.server.api.skins;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface ISkinManagerEagler<PlayerObject> extends ISkinManagerBase<PlayerObject> {

	@Nonnull
	@Override
	IEaglerPlayer<PlayerObject> getPlayer();

	@Nonnull
	IEaglerPlayerSkin getEaglerSkin();

	@Nonnull
	IEaglerPlayerCape getEaglerCape();

	@Nonnull
	EnumEnableFNAW getEnableFNAWSkins();

	void setEnableFNAWSkins(@Nonnull EnumEnableFNAW enabled);

	void resetEnableFNAWSkins();

	boolean isFNAWSkinsServerManaged();

	void setFNAWSkinsServerManaged(boolean managed);

}
