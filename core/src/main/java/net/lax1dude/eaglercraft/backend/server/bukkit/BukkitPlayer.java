package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitPlayer implements IPlatformPlayer<Player> {

	private static final VarHandle CONFIRM_TASK_HANDLE;

	private static final boolean PAPER_VIEW_DISTANCE_SUPPORT;
	private static final Method PAPER_SET_VIEW_DISTANCE_SEND;
	private static final Method PAPER_SET_VIEW_DISTANCE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			CONFIRM_TASK_HANDLE = l.findVarHandle(BukkitPlayer.class, "confirmTask", BukkitTask.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
		boolean support;
		Method viewDistance;
		Method viewDistanceSend;
		try {
			viewDistanceSend = Player.class.getMethod("setSendViewDistance", int.class);
			viewDistance = null;
			support = true;
		}catch(NoSuchMethodException ex) {
			viewDistanceSend = null;
			try {
				viewDistance = Player.class.getMethod("setViewDistance", int.class);
				support = true;
			}catch(NoSuchMethodException exx) {
				viewDistance = null;
				support = false;
			}
		}
		PAPER_VIEW_DISTANCE_SUPPORT = support;
		PAPER_SET_VIEW_DISTANCE_SEND = viewDistanceSend;
		PAPER_SET_VIEW_DISTANCE = viewDistance;
	}

	private final Player player;
	private final BukkitConnection connection;
	volatile BukkitTask confirmTask;
	Object attachment;
	private String brandString;
	Consumer<Object> closeRedirector;

	BukkitPlayer(Player player, BukkitConnection connection) {
		this.player = player;
		this.connection = connection;
		this.connection.bindPlayer(player);
		this.brandString = null;
	}

	BukkitTask xchgConfirmTask() {
		return (BukkitTask)CONFIRM_TASK_HANDLE.getAndSetAcquire(this, null);
	}

	@Override
	public IPlatformConnection getConnection() {
		return connection;
	}

	@Override
	public Player getPlayerObject() {
		return player;
	}

	@Override
	public IPlatformServer<Player> getServer() {
		return connection.getPlugin();
	}

	@Override
	public String getUsername() {
		return player.getName();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	@Override
	public boolean isOnlineMode() {
		return connection.isOnlineMode();
	}

	@Override
	public String getMinecraftBrand() {
		return brandString;
	}

	@Override
	public void sendDataClient(String channel, byte[] message) {
		player.sendPluginMessage(connection.getPlugin(), channel, message);
	}

	@Override
	public void sendDataBackend(String channel, byte[] message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSetViewDistanceSupportedPaper() {
		return PAPER_VIEW_DISTANCE_SUPPORT;
	}

	@Override
	public void setViewDistancePaper(int distance) {
		if(PAPER_SET_VIEW_DISTANCE_SEND != null) {
			try {
				PAPER_SET_VIEW_DISTANCE_SEND.invoke(player, distance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Reflection failed!");
			}
		}else if(PAPER_SET_VIEW_DISTANCE != null) {
			try {
				PAPER_SET_VIEW_DISTANCE.invoke(player, distance);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Reflection failed!");
			}
		}
	}

	@Override
	public String getTexturesProperty() {
		return BukkitUnsafe.getTexturesProperty(player);
	}

	@Override
	public void disconnect() {
		connection.disconnect();
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		connection.disconnect(kickMessage);
	}

	@Override
	public <T> T getPlayerAttachment() {
		return (T) attachment;
	}

	@Override
	public boolean checkPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		player.sendMessage((BaseComponent) component);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public IPlatformPlayer<Player> asPlayer() {
		return this;
	}

	void handleMCBrandMessage(byte[] data) {
		if(data.length > 0) {
			int len = (int)data[0] & 0xFF;
			// Brand over 127 chars is probably garbage anyway...
			if(len < 128 && len == data.length - 1) {
				brandString = new String(data, 1, len, StandardCharsets.UTF_8);
			}
		}
	}

}
