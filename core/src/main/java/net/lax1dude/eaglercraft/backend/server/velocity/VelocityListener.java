package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ListenerBoundEvent;
import com.velocitypowered.api.network.ListenerType;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSink;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.GameProfile.Property;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.velocity.PlatformPluginVelocity.PluginMessageHandler;

class VelocityListener {

	private final PlatformPluginVelocity plugin;

	private static final Property isEaglerPlayer = new Property("isEaglerPlayer", "true", "");
	private static final Predicate<Property> isEaglerPlayerPredicate = (prop) -> "isEaglerPlayer".equals(prop.getName());
	private static final Predicate<Property> texturesPredicate = (prop) -> "textures".equals(prop.getName());

	VelocityListener(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onListenerBound(ListenerBoundEvent bindEvent) {
		if(bindEvent.getListenerType() == ListenerType.MINECRAFT) {
			VelocityUnsafe.injectListenerAttr(plugin.proxy(), bindEvent.getAddress(), plugin.listenersToInit);
		}
	}

	@Subscribe(priority = Short.MAX_VALUE)
	public void onPreLoginEvent(PreLoginEvent handshakeEvent) {
		InboundConnection conn = handshakeEvent.getConnection();
		Channel channel = VelocityUnsafe.getInboundChannel(conn);
		Object pipelineData = channel.attr(PipelineAttributes.<Object>pipelineData()).getAndSet(null);
		plugin.initializeConnection(conn, handshakeEvent.getUsername(), handshakeEvent.getUniqueId(), pipelineData,
				channel.attr(PipelineAttributes.<VelocityConnection>connectionData())::set);
	}

	@Subscribe(priority = Short.MAX_VALUE)
	public void onGameProfileRequestEvent(GameProfileRequestEvent gameProfileEvent) {
		VelocityConnection conn = VelocityUnsafe.getInboundChannel(gameProfileEvent.getConnection())
				.attr(PipelineAttributes.<VelocityConnection>connectionData()).get();
		GameProfile gameProfile = gameProfileEvent.getGameProfile();
		boolean changed = false;
		if(!conn.uuid.equals(gameProfileEvent.getGameProfile().getId())) {
			gameProfile = gameProfile.withId(conn.uuid);
			changed = true;
		}
		if(conn.texturesPropertyValue != null || conn.eaglerPlayerProperty) {
			List<GameProfile.Property> props = gameProfile.getProperties();
			List<GameProfile.Property> fixedProps = new LinkedList<>(props);
			if(conn.texturesPropertyValue != null) {
				fixedProps.removeIf(texturesPredicate);
				fixedProps.add(new Property("textures", conn.texturesPropertyValue, conn.texturesPropertySignature));
			}
			if(conn.eaglerPlayerProperty) {
				fixedProps.removeIf(isEaglerPlayerPredicate);
				fixedProps.add(isEaglerPlayer);
			}
			gameProfile = gameProfile.withProperties(fixedProps);
			changed = true;
		}
		if(changed) {
			gameProfileEvent.setGameProfile(gameProfile);
		}
	}

	@Subscribe(priority = Short.MAX_VALUE, async = true)
	public void onLoginEvent(LoginEvent loginEvent, Continuation cont) {
		Player player = loginEvent.getPlayer();
		VelocityConnection conn = VelocityUnsafe.getInboundChannel(player)
				.attr(PipelineAttributes.<VelocityConnection>connectionData()).get();
		try {
			plugin.initializePlayer(player, conn, cont::resume);
		}catch(Exception ex) {
			cont.resumeWithException(ex);
		}
	}

	@Subscribe
	public void onPostLoginEvent(PostLoginEvent loginEvent) {
		plugin.confirmPlayer(loginEvent.getPlayer());
	}

	@Subscribe(priority = Short.MIN_VALUE)
	public void onPlayerDisconnected(DisconnectEvent disconnectEvent) {
		plugin.dropPlayer(disconnectEvent.getPlayer());
	}

	@Subscribe(priority = Short.MAX_VALUE)
	public void onServerConnected(ServerConnectedEvent connectEvent) {
		IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(connectEvent.getPlayer());
		if(platformPlayer != null) {
			RegisteredServer server = connectEvent.getServer();
			if(server != null) {
				IPlatformServer<Player> platformServer = plugin.getRegisteredServers().get(server.getServerInfo().getName());
				if(platformServer == null) {
					platformServer = new VelocityServer(plugin, server, false);
				}
				((VelocityPlayer)platformPlayer).server = platformServer;
			}else {
				((VelocityPlayer)platformPlayer).server = null;
			}
		}
	}

	@Subscribe
	public void onPluginMessageEvent(PluginMessageEvent evt) {
		PluginMessageHandler handler = plugin.registeredChannelsMap.get(evt.getIdentifier());
		if(handler != null) {
			evt.setResult(ForwardResult.handled());
			ChannelMessageSource src = evt.getSource();
			ChannelMessageSink dst = evt.getTarget();
			if(handler.backend) {
				IEaglerXServerMessageHandler<Player> ls = handler.handler;
				if(ls != null && (src instanceof ServerConnection) && (dst instanceof Player)) {
					IPlatformPlayer<Player> player = plugin.getPlayer((Player)dst);
					if(player != null) {
						ls.handle(handler.channel, player, evt.getData());
					}
				}
			}else {
				IEaglerXServerMessageHandler<Player> ls = handler.handler;
				if(ls != null && (src instanceof Player) && (dst instanceof ServerConnection)) {
					IPlatformPlayer<Player> player = plugin.getPlayer((Player)src);
					if(player != null) {
						ls.handle(handler.channel, player, evt.getData());
					}
				}
			}
		}
	}

}
