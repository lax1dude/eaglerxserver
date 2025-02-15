package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public abstract class EaglercraftWebSocketOpenEvent extends Event
		implements IEaglercraftWebSocketOpenEvent<ProxiedPlayer>, Cancellable {

}
