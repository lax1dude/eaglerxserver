package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public interface IEaglercraftRegisterSkinEvent<PlayerObject> extends IEaglerXHandshakeEvent<PlayerObject> {

	IEaglerPlayerSkin getEaglerSkin();

	IEaglerPlayerCape getEaglerCape();

	void forceFromVanillaTexturesProperty(String value);

	void forceFromVanillaLoginProfile();

	void forceSkinFromURL(String url, EnumSkinModel model);

	void forceSkinFromVanillaTexturesProperty(String value);

	void forceSkinFromVanillaLoginProfile();

	void forceCapeFromURL(String url);

	void forceCapeFromVanillaTexturesProperty(String value);

	void forceCapeFromVanillaLoginProfile();

	void forceSkinEagler(IEaglerPlayerSkin skin);

	void forceCapeEagler(IEaglerPlayerCape cape);

}
