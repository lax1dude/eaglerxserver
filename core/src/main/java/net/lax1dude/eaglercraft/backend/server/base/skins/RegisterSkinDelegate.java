package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

abstract class RegisterSkinDelegate implements IRegisterSkinDelegate {

	final IEaglerPlayerSkin skinOriginal;
	final IEaglerPlayerCape capeOriginal;
	IEaglerPlayerSkin skin;
	IEaglerPlayerCape cape;
	EnumSkinModel skinModel;
	String skinURL;
	String capeURL;

	RegisterSkinDelegate(IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		this.skinOriginal = this.skin = skin;
		this.capeOriginal = this.cape = cape;
	}

	protected abstract String resolveTexturesProperty();

	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return skinOriginal;
	}

	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return capeOriginal;
	}

	@Override
	public void forceFromVanillaTexturesProperty(String value) {
		GameProfileUtil profile = GameProfileUtil.extractSkinAndCape(value);
		if(profile == null) {
			throw new IllegalArgumentException("Textures property data is invalid!");
		}
		if(profile.skinURL != null) {
			skin = null;
			skinModel = "slim".equals(profile.skinModel) ? EnumSkinModel.ALEX : EnumSkinModel.STEVE;
			skinURL = profile.skinURL;
		}
		if(profile.capeURL != null) {
			cape = null;
			capeURL = profile.capeURL;
		}
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		String prop = resolveTexturesProperty();
		if(prop != null) {
			try {
				forceFromVanillaTexturesProperty(prop);
			}catch(IllegalArgumentException ex) {
			}
		}
	}

	@Override
	public void forceSkinFromURL(String value, EnumSkinModel model) {
		if(value != null) {
			skin = null;
			skinModel = model;
			skinURL = value;
		}
	}

	@Override
	public void forceSkinFromVanillaTexturesProperty(String value) {
		GameProfileUtil profile = GameProfileUtil.extractSkinAndCape(value);
		if(profile == null) {
			throw new IllegalArgumentException("Textures property data is invalid!");
		}
		if(profile.skinURL != null) {
			skin = null;
			skinModel = "slim".equals(profile.skinModel) ? EnumSkinModel.ALEX : EnumSkinModel.STEVE;
			skinURL = profile.skinURL;
		}
	}

	@Override
	public void forceSkinFromVanillaLoginProfile() {
		String prop = resolveTexturesProperty();
		if(prop != null) {
			try {
				forceSkinFromVanillaTexturesProperty(prop);
			}catch(IllegalArgumentException ex) {
			}
		}
	}

	@Override
	public void forceCapeFromURL(String value) {
		if(value != null) {
			cape = null;
			capeURL = value;
		}
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(String value) {
		GameProfileUtil profile = GameProfileUtil.extractSkinAndCape(value);
		if(profile == null) {
			throw new IllegalArgumentException("Textures property data is invalid!");
		}
		if(profile.capeURL != null) {
			cape = null;
			capeURL = profile.capeURL;
		}
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		String prop = resolveTexturesProperty();
		if(prop != null) {
			try {
				forceCapeFromVanillaTexturesProperty(prop);
			}catch(IllegalArgumentException ex) {
			}
		}
	}

	@Override
	public void forceSkinEagler(IEaglerPlayerSkin skin) {
		if(skin != null) {
			skinURL = null;
			this.skin = skin;
		}
	}

	@Override
	public void forceCapeEagler(IEaglerPlayerCape cape) {
		if(cape != null) {
			capeURL = null;
			this.cape = cape;
		}
	}

}
