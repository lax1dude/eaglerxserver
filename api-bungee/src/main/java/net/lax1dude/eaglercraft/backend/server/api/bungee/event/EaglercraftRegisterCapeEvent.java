package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterCapeEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public abstract class EaglercraftRegisterCapeEvent extends AsyncEvent<IEaglercraftRegisterCapeEvent<ProxiedPlayer>>
		implements IEaglercraftRegisterCapeEvent<ProxiedPlayer> {

	protected EaglercraftRegisterCapeEvent(Callback<IEaglercraftRegisterCapeEvent<ProxiedPlayer>> done) {
		super(done);
	}

}
