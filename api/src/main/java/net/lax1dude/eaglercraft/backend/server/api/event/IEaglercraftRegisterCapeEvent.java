package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;

public interface IEaglercraftRegisterCapeEvent<PlayerObject> extends IEaglerXHandshakeEvent<PlayerObject> {

	IEaglerPlayerCape getEaglerCape();

	void forceCapeFromVanillaTexturesProperty(String value);

	void forceCapeFromVanillaLoginProfile();

	void forceCapeEagler(IEaglerPlayerCape cape);

}
