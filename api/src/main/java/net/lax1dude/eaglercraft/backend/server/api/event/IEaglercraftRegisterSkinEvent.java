package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public interface IEaglercraftRegisterSkinEvent<PlayerObject> extends IBaseLoginEvent<PlayerObject> {

	@Nonnull
	IEaglerPlayerSkin getEaglerSkin();

	@Nonnull
	IEaglerPlayerCape getEaglerCape();

	void forceFromVanillaTexturesProperty(@Nonnull String value);

	void forceFromVanillaLoginProfile();

	void forceSkinFromURL(@Nonnull String url, @Nonnull EnumSkinModel model);

	void forceSkinFromVanillaTexturesProperty(@Nonnull String value);

	void forceSkinFromVanillaLoginProfile();

	void forceCapeFromURL(@Nonnull String url);

	void forceCapeFromVanillaTexturesProperty(@Nonnull String value);

	void forceCapeFromVanillaLoginProfile();

	void forceSkinEagler(@Nonnull IEaglerPlayerSkin skin);

	void forceCapeEagler(@Nonnull IEaglerPlayerCape cape);

}
