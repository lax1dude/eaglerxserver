package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public abstract class EaglercraftAuthCookieEvent
		extends AsyncEvent<IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent> {

	protected EaglercraftAuthCookieEvent(Callback<IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent>> done) {
		super(done);
	}

}
