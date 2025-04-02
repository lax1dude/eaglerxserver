package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftLoginEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public abstract class EaglercraftLoginEvent
		extends AsyncEvent<IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>, Cancellable {

	protected EaglercraftLoginEvent(Callback<IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>> done) {
		super(done);
	}

}
