/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

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
		World world = player.getWorld();
		return world != null ? new BukkitWorld(connection.getPlugin(), world) : null;
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
	public void sendMessage(String message) {
		player.sendMessage(new TextComponent(message));
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		player.sendMessage((BaseComponent) component);
	}

	@Override
	public void disconnect() {
		connection.disconnect();
	}

	@Override
	public void disconnect(String kickMessage) {
		connection.disconnect(new TextComponent(kickMessage));
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
