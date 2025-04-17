package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftRegisterSkinEvent;

class VelocityRegisterSkinEventImpl extends EaglercraftRegisterSkinEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerLoginConnection loginConnection;
	private final IRegisterSkinDelegate delegate;

	VelocityRegisterSkinEventImpl(IEaglerXServerAPI<Player> api, IEaglerLoginConnection loginConnection,
			IRegisterSkinDelegate delegate) {
		this.api = api;
		this.loginConnection = loginConnection;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
	}

	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return delegate.getEaglerSkin();
	}

	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return delegate.getEaglerCape();
	}

	@Override
	public void forceFromVanillaTexturesProperty(String value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		delegate.forceFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		delegate.forceFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinFromURL(String url, EnumSkinModel skinModel) {
		if(url == null) {
			throw new NullPointerException("url");
		}
		if(skinModel == null) {
			throw new NullPointerException("skinModel");
		}
		delegate.forceSkinFromURL(url, skinModel);
	}

	@Override
	public void forceSkinFromVanillaTexturesProperty(String value) {
		delegate.forceSkinFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceSkinFromVanillaLoginProfile() {
		delegate.forceSkinFromVanillaLoginProfile();
	}

	@Override
	public void forceCapeFromURL(String url) {
		if(url == null) {
			throw new NullPointerException("url");
		}
		delegate.forceCapeFromURL(url);
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(String value) {
		if(value == null) {
			throw new NullPointerException("value");
		}
		delegate.forceCapeFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		delegate.forceCapeFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinEagler(IEaglerPlayerSkin skin) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		delegate.forceSkinEagler(skin);
	}

	@Override
	public void forceCapeEagler(IEaglerPlayerCape cape) {
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		delegate.forceCapeEagler(cape);
	}

}
