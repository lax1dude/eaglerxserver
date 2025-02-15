package net.lax1dude.eaglercraft.backend.server.adapter.event;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;

public interface IRegisterCapeDelegate {

	IEaglerPlayerCape getEaglerCape();

	void forceCapeFromVanillaTexturesProperty(String value);

	void forceCapeFromVanillaLoginProfile();

	void forceCapeEagler(IEaglerPlayerCape cape);

}
