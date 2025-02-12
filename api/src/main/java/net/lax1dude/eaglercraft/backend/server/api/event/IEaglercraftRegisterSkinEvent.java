package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public interface IEaglercraftRegisterSkinEvent<PlayerObject> extends IEaglerXHandshakeEvent<PlayerObject> {

	IEaglerPlayerSkin getEaglerSkin();

	void forceSkinFromVanillaTexturesProperty(String value);

	void forceSkinFromVanillaLoginProfile();

	void forceSkinEagler(IEaglerPlayerSkin skin);

}
