package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterCapeDelegate;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftRegisterCapeEvent;

class VelocityRegisterCapeEventImpl extends EaglercraftRegisterCapeEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPendingConnection pendingConnection;
	private final IRegisterCapeDelegate delegate;

	VelocityRegisterCapeEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
			IRegisterCapeDelegate delegate) {
		this.api = api;
		this.pendingConnection = pendingConnection;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
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
