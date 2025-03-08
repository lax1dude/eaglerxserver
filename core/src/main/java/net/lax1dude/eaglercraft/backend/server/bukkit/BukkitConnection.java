package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.net.SocketAddress;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe.LoginConnectionHolder;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitConnection implements IPlatformConnection {

	private final PlatformPluginBukkit plugin;
	private LoginConnectionHolder loginConnection;
	String texturesPropertyValue;
	String texturesPropertySignature;
	boolean eaglerPlayerProperty;
	private volatile Player playerInstance;
	Object attachment;

	BukkitConnection(PlatformPluginBukkit plugin, LoginConnectionHolder loginConnection) {
		this.plugin = plugin;
		this.loginConnection = loginConnection;
	}

	void bindPlayer(Player player) {
		playerInstance = player;
		loginConnection = null;
	}

	@Override
	public Channel getChannel() {
		return loginConnection.getChannel();
	}

	@Override
	public <T> T getAttachment() {
		return (T) attachment;
	}

	public PlatformPluginBukkit getPlugin() {
		return plugin;
	}

	@Override
	public String getUsername() {
		Player player = playerInstance;
		if(player != null) {
			return player.getName();
		}else {
			return loginConnection.getUsername();
		}
	}

	@Override
	public UUID getUniqueId() {
		Player player = playerInstance;
		if(player != null) {
			return player.getUniqueId();
		}else {
			return loginConnection.getUniqueId();
		}
	}

	@Override
	public SocketAddress getSocketAddress() {
		Player player = playerInstance;
		if(player != null) {
			return player.getAddress();
		}else {
			return loginConnection.getRemoteAddress();
		}
	}

	@Override
	public int getMinecraftProtocol() {
		return 47; // TODO: how to get protocol?
	}

	@Override
	public boolean isOnlineMode() {
		return plugin.getServer().getOnlineMode();
	}

	@Override
	public boolean isConnected() {
		Player player = playerInstance;
		if(player != null) {
			Channel c = BukkitUnsafe.getPlayerChannel(player);
			return c != null && c.isActive();
		}else {
			return loginConnection.isConnected();
		}
	}

	@Override
	public void disconnect() {
		Player player = playerInstance;
		if(player != null) {
			player.kickPlayer("Connection Closed");
		}else {
			loginConnection.disconnect();
		}
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		String msg = ((BaseComponent)kickMessage).toLegacyText();
		Player player = playerInstance;
		if(player != null) {
			player.kickPlayer(msg);
		}else {
			loginConnection.disconnect(msg);
		}
	}

}
