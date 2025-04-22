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
import java.net.SocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

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
	private Channel channel;
	private LoginConnectionHolder loginConnection;
	private Player playerInstance;
	String texturesPropertyValue;
	String texturesPropertySignature;
	Object attachment;
	boolean closePending;
	Consumer<Runnable> awaitPlayState;
	byte eaglerPlayerProperty;

	BukkitConnection(PlatformPluginBukkit plugin, LoginConnectionHolder loginConnection,
			Consumer<Runnable> awaitPlayState) {
		this.plugin = plugin;
		this.channel = loginConnection.getChannel();
		this.loginConnection = loginConnection;
		this.awaitPlayState = awaitPlayState;
	}

	void bindPlayer(Player player) {
		playerInstance = player;
		LOGIN_CONNECTION_HANDLE.setRelease(this, null);
	}

	@Override
	public Channel getChannel() {
		return channel;
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
				throw new IllegalStateException("Cannot access this on Bukkit before player initializes!");
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
				throw new IllegalStateException("Cannot access this on Bukkit before player initializes!");
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
				throw new IllegalStateException("Cannot access this on Bukkit before player initializes!");
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
		closePending = true;
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

	public void awaitPlayState(Runnable runnable) {
		if(awaitPlayState != null) {
			awaitPlayState.accept(runnable);
			awaitPlayState = null;
		}else {
			runnable.run();
		}
	}

}
