package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterCapeDelegate;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftRegisterCapeEvent;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;

class BukkitRegisterCapeEventImpl extends EaglercraftRegisterCapeEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPendingConnection pendingConnection;
	private final IRegisterCapeDelegate delegate;

	BukkitRegisterCapeEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
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
