package net.lax1dude.eaglercraft.backend.server.event;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public interface IRegisterSkinDelegate {

	IEaglerPlayerSkin getEaglerSkin();

	void forceSkinFromVanillaTexturesProperty(String value);

	void forceSkinFromVanillaLoginProfile();

	void forceSkinEagler(IEaglerPlayerSkin skin);

}
