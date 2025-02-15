package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public abstract class EaglercraftRevokeSessionQueryEvent
		extends AsyncEvent<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>>
		implements IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer> {

	protected EaglercraftRevokeSessionQueryEvent(Callback<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>> done) {
		super(done);
	}

}
