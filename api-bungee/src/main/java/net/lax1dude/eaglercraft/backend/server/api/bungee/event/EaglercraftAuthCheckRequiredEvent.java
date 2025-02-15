package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public abstract class EaglercraftAuthCheckRequiredEvent
		extends AsyncEvent<IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent> {

	protected EaglercraftAuthCheckRequiredEvent(
			Callback<IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent>> done) {
		super(done);
	}

}
