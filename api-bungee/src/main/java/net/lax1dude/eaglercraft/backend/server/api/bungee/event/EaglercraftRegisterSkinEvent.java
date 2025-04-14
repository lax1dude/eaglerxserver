package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public final class EaglercraftRegisterSkinEvent extends AsyncEvent<IEaglercraftRegisterSkinEvent<ProxiedPlayer>>
		implements IEaglercraftRegisterSkinEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerLoginConnection loginConnection;
	private final IRegisterSkinDelegate delegate;

	public EaglercraftRegisterSkinEvent(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerLoginConnection loginConnection,
			IRegisterSkinDelegate delegate, Callback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> cb) {
		super(cb);
		this.api = api;
		this.loginConnection = loginConnection;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
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

	public interface IRegisterSkinDelegate {

		IEaglerPlayerSkin getEaglerSkin();

		IEaglerPlayerCape getEaglerCape();

		void forceFromVanillaTexturesProperty(String value);

		void forceFromVanillaLoginProfile();

		void forceSkinFromURL(String url, EnumSkinModel skinModel);

		void forceSkinFromVanillaTexturesProperty(String value);

		void forceSkinFromVanillaLoginProfile();

		void forceCapeFromURL(String url);

		void forceCapeFromVanillaTexturesProperty(String value);

		void forceCapeFromVanillaLoginProfile();

		void forceSkinEagler(IEaglerPlayerSkin skin);

		void forceCapeEagler(IEaglerPlayerCape skin);

	}

}
