package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;

class BukkitPlayer implements IPlatformPlayer<Player> {

	private static final VarHandle CONFIRM_TASK_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			CONFIRM_TASK_HANDLE = l.findVarHandle(BukkitPlayer.class, "confirmTask", BukkitTask.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final PlatformPluginBukkit plugin;
	private final Player player;
	Object attachment;
	volatile BukkitTask confirmTask;

	BukkitPlayer(PlatformPluginBukkit plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

	BukkitTask xchgConfirmTask() {
		return (BukkitTask)CONFIRM_TASK_HANDLE.getAndSetAcquire(this, null);
	}

	@Override
	public Player getPlayerObject() {
		return player;
	}

	@Override
	public <T> T getAttachment() {
		return (T) attachment;
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
		Channel ch = BukkitUnsafe.getPlayerChannel(player);
		return ch != null && ch.isActive();
	}

	@Override
	public void sendData(String channel, byte[] message) {
		player.sendPluginMessage(plugin, channel, message);
	}

}
