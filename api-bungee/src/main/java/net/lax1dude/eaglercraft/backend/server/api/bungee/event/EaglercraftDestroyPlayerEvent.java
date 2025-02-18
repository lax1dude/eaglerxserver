package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public abstract class EaglercraftDestroyPlayerEvent extends Event
		implements IEaglercraftDestroyPlayerEvent<ProxiedPlayer> {

}
