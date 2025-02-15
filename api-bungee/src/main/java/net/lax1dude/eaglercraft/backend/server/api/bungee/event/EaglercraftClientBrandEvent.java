package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public abstract class EaglercraftClientBrandEvent extends AsyncEvent<IEaglercraftClientBrandEvent<?, ?>>
		implements IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>, Cancellable {

	protected EaglercraftClientBrandEvent(Callback<IEaglercraftClientBrandEvent<?, ?>> done) {
		super(done);
	}

}
