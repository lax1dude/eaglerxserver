package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.event.IRegisterSkinDelegate;

class VelocityRegisterSkinEventImpl extends EaglercraftRegisterSkinEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPendingConnection pendingConnection;
	private final IRegisterSkinDelegate delegate;

	VelocityRegisterSkinEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection,
			IRegisterSkinDelegate delegate) {
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
	public IEaglerPlayerSkin getEaglerSkin() {
		return delegate.getEaglerSkin();
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
	public void forceSkinEagler(IEaglerPlayerSkin cape) {
		delegate.forceSkinEagler(cape);
	}

}
