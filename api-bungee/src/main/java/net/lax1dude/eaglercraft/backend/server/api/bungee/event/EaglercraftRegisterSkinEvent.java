package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterSkinEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public abstract class EaglercraftRegisterSkinEvent extends AsyncEvent<IEaglercraftRegisterSkinEvent<ProxiedPlayer>>
		implements IEaglercraftRegisterSkinEvent<ProxiedPlayer> {

	protected EaglercraftRegisterSkinEvent(Callback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> done) {
		super(done);
	}

}
