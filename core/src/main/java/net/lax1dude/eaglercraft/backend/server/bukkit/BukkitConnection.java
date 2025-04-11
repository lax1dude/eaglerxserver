package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.SocketAddress;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe.LoginConnectionHolder;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitConnection implements IPlatformConnection {

	private static final VarHandle LOGIN_CONNECTION_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			LOGIN_CONNECTION_HANDLE = l.findVarHandle(BukkitConnection.class, "loginConnection", LoginConnectionHolder.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final PlatformPluginBukkit plugin;
	private LoginConnectionHolder loginConnection;
	private Player playerInstance;
	String texturesPropertyValue;
	String texturesPropertySignature;
	boolean eaglerPlayerProperty;
	boolean closePending;
	PlatformPluginBukkit.CloseRedirector closeRedirector;
	Object attachment;

	BukkitConnection(PlatformPluginBukkit plugin, LoginConnectionHolder loginConnection) {
		this.plugin = plugin;
		this.loginConnection = loginConnection;
	}

	void bindPlayer(Player player) {
		playerInstance = player;
		LOGIN_CONNECTION_HANDLE.setRelease(this, null);
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
			LoginConnectionHolder loginConn = (LoginConnectionHolder)LOGIN_CONNECTION_HANDLE.getAcquire(this);
			if(loginConn == null) {
				return playerInstance.getName();
			}else {
				return loginConn.getUsername();
			}
		}
	}

	@Override
	public UUID getUniqueId() {
		Player player = playerInstance;
		if(player != null) {
			return player.getUniqueId();
		}else {
			LoginConnectionHolder loginConn = (LoginConnectionHolder)LOGIN_CONNECTION_HANDLE.getAcquire(this);
			if(loginConn == null) {
				return playerInstance.getUniqueId();
			}else {
				return loginConn.getUniqueId();
			}
		}
	}

	@Override
	public SocketAddress getSocketAddress() {
		Player player = playerInstance;
		if(player != null) {
			return player.getAddress();
		}else {
			LoginConnectionHolder loginConn = (LoginConnectionHolder)LOGIN_CONNECTION_HANDLE.getAcquire(this);
			if(loginConn == null) {
				return playerInstance.getAddress();
			}else {
				return loginConn.getRemoteAddress();
			}
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
		if(closePending) {
			return false;
		}
		Player player = playerInstance;
		if(player != null) {
			Channel c = BukkitUnsafe.getPlayerChannel(player);
			return c != null && c.isActive();
		}else {
			LoginConnectionHolder loginConn = (LoginConnectionHolder)LOGIN_CONNECTION_HANDLE.getAcquire(this);
			if(loginConn == null) {
				player = playerInstance;
				Channel c = BukkitUnsafe.getPlayerChannel(player);
				return c != null && c.isActive();
			}else {
				return loginConn.isConnected();
			}
		}
	}

	@Override
	public void disconnect() {
		if(closePending) {
			return;
		}
		closePending = true;
		PlatformPluginBukkit.CloseRedirector closer = this.closeRedirector;
		if(closer != null) {
			closer.accept(null);
			return;
		}
		Player player = playerInstance;
		if(player != null) {
			plugin.getScheduler().execute(() -> {
				player.kickPlayer("Connection Closed");
			});
		}else {
			LoginConnectionHolder loginConn = (LoginConnectionHolder)LOGIN_CONNECTION_HANDLE.getAcquire(this);
			if(loginConn == null) {
				plugin.getScheduler().execute(() -> {
					playerInstance.kickPlayer("Connection Closed");
				});
			}else {
				loginConn.disconnect();
			}
		}
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		if(closePending) {
			return;
		}
		closePending = true;
		PlatformPluginBukkit.CloseRedirector closer = this.closeRedirector;
		if(closer != null) {
			closer.accept(kickMessage);
			return;
		}
		String msg = ((BaseComponent)kickMessage).toLegacyText();
		Player player = playerInstance;
		if(player != null) {
			plugin.getScheduler().execute(() -> {
				player.kickPlayer(msg);
			});
		}else {
			LoginConnectionHolder loginConn = (LoginConnectionHolder)LOGIN_CONNECTION_HANDLE.getAcquire(this);
			if(loginConn == null) {
				plugin.getScheduler().execute(() -> {
					playerInstance.kickPlayer(msg);
				});
			}else {
				loginConn.disconnect(msg);
			}
		}
	}

}
