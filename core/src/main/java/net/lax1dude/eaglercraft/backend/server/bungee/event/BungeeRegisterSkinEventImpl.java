package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeRegisterSkinEventImpl extends EaglercraftRegisterSkinEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPendingConnection pendingConnection;
	private final IRegisterSkinDelegate delegate;

	BungeeRegisterSkinEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPendingConnection pendingConnection,
			IRegisterSkinDelegate delegate, Callback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> cb) {
		super(cb);
		this.api = api;
		this.pendingConnection = pendingConnection;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
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
		delegate.forceFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		delegate.forceFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinFromURL(String value, EnumSkinModel skinModel) {
		delegate.forceSkinFromURL(value, skinModel);
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
	public void forceCapeFromURL(String value) {
		delegate.forceCapeFromURL(value);
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(String value) {
		delegate.forceCapeFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		delegate.forceCapeFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinEagler(IEaglerPlayerSkin skin) {
		delegate.forceSkinEagler(skin);
	}

	@Override
	public void forceCapeEagler(IEaglerPlayerCape cape) {
		delegate.forceCapeEagler(cape);
	}

}
