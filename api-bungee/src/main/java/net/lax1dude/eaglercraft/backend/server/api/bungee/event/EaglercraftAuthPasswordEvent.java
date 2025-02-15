package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public abstract class EaglercraftAuthPasswordEvent
		extends AsyncEvent<IEaglercraftAuthPasswordEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftAuthPasswordEvent<ProxiedPlayer, BaseComponent> {

	protected EaglercraftAuthPasswordEvent(Callback<IEaglercraftAuthPasswordEvent<ProxiedPlayer, BaseComponent>> done) {
		super(done);
	}

}
