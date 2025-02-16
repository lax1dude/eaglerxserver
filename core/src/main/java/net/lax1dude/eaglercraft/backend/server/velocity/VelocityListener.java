package net.lax1dude.eaglercraft.backend.server.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;

class VelocityListener {

	private final PlatformPluginVelocity plugin;

	VelocityListener(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
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
	public void onLoginEvent(LoginEvent loginEvent) {
		Player player = loginEvent.getPlayer();
		VelocityConnection conn = VelocityUnsafe.getPlayerChannel(player)
				.attr(PipelineAttributes.<VelocityConnection>connectionData()).get();
		plugin.initializePlayer(player, conn);
	}

	@Subscribe
	public void onPostLoginEvent(PostLoginEvent loginEvent) {
		plugin.confirmPlayer(loginEvent.getPlayer());
	}

	@Subscribe(priority = Short.MIN_VALUE)
	public void onPlayerDisconnected(DisconnectEvent disconnectEvent) {
		plugin.dropPlayer(disconnectEvent.getPlayer());
	}

}
