package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;

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

	public EaglercraftRegisterSkinEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerLoginConnection loginConnection, @Nonnull IRegisterSkinDelegate delegate,
			@Nonnull Callback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> cb) {
		super(cb);
		this.api = api;
		this.loginConnection = loginConnection;
		this.delegate = delegate;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
	}

	@Nonnull
	@Override
	public IEaglerPlayerSkin getEaglerSkin() {
		return delegate.getEaglerSkin();
	}

	@Nonnull
	@Override
	public IEaglerPlayerCape getEaglerCape() {
		return delegate.getEaglerCape();
	}

	@Override
	public void forceFromVanillaTexturesProperty(@Nonnull String value) {
		delegate.forceFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceFromVanillaLoginProfile() {
		delegate.forceFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinFromURL(@Nonnull String value, @Nonnull EnumSkinModel skinModel) {
		delegate.forceSkinFromURL(value, skinModel);
	}

	@Override
	public void forceSkinFromVanillaTexturesProperty(@Nonnull String value) {
		delegate.forceSkinFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceSkinFromVanillaLoginProfile() {
		delegate.forceSkinFromVanillaLoginProfile();
	}

	@Override
	public void forceCapeFromURL(@Nonnull String value) {
		delegate.forceCapeFromURL(value);
	}

	@Override
	public void forceCapeFromVanillaTexturesProperty(@Nonnull String value) {
		delegate.forceCapeFromVanillaTexturesProperty(value);
	}

	@Override
	public void forceCapeFromVanillaLoginProfile() {
		delegate.forceCapeFromVanillaLoginProfile();
	}

	@Override
	public void forceSkinEagler(@Nonnull IEaglerPlayerSkin skin) {
		delegate.forceSkinEagler(skin);
	}

	@Override
	public void forceCapeEagler(@Nonnull IEaglerPlayerCape cape) {
		delegate.forceCapeEagler(cape);
	}

	public interface IRegisterSkinDelegate {

		@Nonnull
		IEaglerPlayerSkin getEaglerSkin();

		@Nonnull
		IEaglerPlayerCape getEaglerCape();

		void forceFromVanillaTexturesProperty(@Nonnull String value);

		void forceFromVanillaLoginProfile();

		void forceSkinFromURL(@Nonnull String url, @Nonnull EnumSkinModel skinModel);

		void forceSkinFromVanillaTexturesProperty(@Nonnull String value);

		void forceSkinFromVanillaLoginProfile();

		void forceCapeFromURL(@Nonnull String url);

		void forceCapeFromVanillaTexturesProperty(@Nonnull String value);

		void forceCapeFromVanillaLoginProfile();

		void forceSkinEagler(@Nonnull IEaglerPlayerSkin skin);

		void forceCapeEagler(@Nonnull IEaglerPlayerCape skin);

	}

}
