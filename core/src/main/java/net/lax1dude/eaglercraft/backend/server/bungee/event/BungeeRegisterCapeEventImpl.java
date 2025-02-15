package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftRegisterCapeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterCapeEvent;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.event.IRegisterCapeDelegate;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeRegisterCapeEventImpl extends EaglercraftRegisterCapeEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPendingConnection pendingConnection;
	private final IRegisterCapeDelegate delegate;

	BungeeRegisterCapeEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPendingConnection pendingConnection,
			IRegisterCapeDelegate delegate, Callback<IEaglercraftRegisterCapeEvent<?>> cb) {
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
	public IEaglerPlayerCape getEaglerCape() {
		return delegate.getEaglerCape();
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
	public void forceCapeEagler(IEaglerPlayerCape cape) {
		delegate.forceCapeEagler(cape);
	}

}
