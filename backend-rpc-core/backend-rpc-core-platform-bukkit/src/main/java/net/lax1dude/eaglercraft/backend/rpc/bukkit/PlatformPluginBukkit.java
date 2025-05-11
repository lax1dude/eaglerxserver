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

package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

import eu.hexagonmc.spigot.annotation.meta.DependencyType;
import eu.hexagonmc.spigot.annotation.plugin.Dependency;
import eu.hexagonmc.spigot.annotation.plugin.Plugin;
import eu.hexagonmc.spigot.annotation.plugin.Plugin.Spigot;
import net.lax1dude.eaglercraft.backend.rpc.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageChannel;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageHandler;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCWorldChangeHandler;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.rpc.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.rpc.api.bukkit.EaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCVersion;
import net.lax1dude.eaglercraft.backend.rpc.bukkit.event.BukkitEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

@Plugin(
	name = PlatformPluginBukkit.PLUGIN_NAME,
	version = PlatformPluginBukkit.PLUGIN_VERSION,
	description = "Official backend RPC plugin for EaglercraftXServer",
	spigot = @Spigot(
		authors = {
			PlatformPluginBukkit.PLUGIN_AUTHOR
		},
		website = "https://lax1dude.net/eaglerxserver",
		prefix = "EaglerXBackendRPC"
	),
	dependencies = {
		@Dependency(name = EaglerXServerAPI.PLUGIN_NAME, type = DependencyType.SOFTDEPEND)
	}
)
public class PlatformPluginBukkit extends JavaPlugin implements IPlatform<Player> {

	public static final String PLUGIN_NAME = EaglerXBackendRPC.PLUGIN_NAME;
	public static final String PLUGIN_AUTHOR = EaglerXBackendRPCVersion.AUTHOR;
	public static final String PLUGIN_VERSION = EaglerXBackendRPCVersion.VERSION;

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player> eventDispatcher;
	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IBackendRPCPlayerInitializer<Object, Player> playerInitializer;
	protected boolean post_v1_13;
	protected IPlatformScheduler schedulerImpl;
	protected IPlatformComponentHelper componentHelper;

	protected boolean localMode;
	protected Collection<IBackendRPCMessageChannel<Player>> channelsList;
	protected IBackendRPCWorldChangeHandler<Player> worldChangeHandler;
	protected Consumer<IEaglercraftInitializePlayerEvent<Player>> initializePlayerHandler;
	protected Consumer<IEaglercraftWebViewChannelEvent<Player>> localWebViewChannelHandler;
	protected Consumer<IEaglercraftWebViewMessageEvent<Player>> localWebViewMessageHandler;
	protected Consumer<IEaglercraftVoiceChangeEvent<Player>> localVoiceChangeHandler;

	private final ConcurrentMap<Player, BukkitPlayer> playerInstanceMap = (new MapMaker()).initialCapacity(256)
			.concurrencyLevel(16).makeMap();

	@Override
	public void onLoad() {
		post_v1_13 = checkPost_v1_13();
		Server server = getServer();
		loggerImpl = new JavaLogger(getLogger());
		eventDispatcher = new BukkitEventDispatchAdapter(this, server, server.getPluginManager());
		schedulerImpl = new BukkitScheduler(this, server.getScheduler());
		componentHelper = new BukkitComponentHelper();
		Init<Player> init = new Init<Player>() {
			@Override
			public void setOnServerEnable(Runnable enable) {
				onServerEnable = enable;
			}

			@Override
			public void setOnServerDisable(Runnable disable) {
				onServerDisable = disable;
			}

			@Override
			public void setPlayerInitializer(IBackendRPCPlayerInitializer<?, Player> initializer) {
				playerInitializer = (IBackendRPCPlayerInitializer<Object, Player>) initializer;
			}

			@Override
			public void setWorldChangeHandler(IBackendRPCWorldChangeHandler<Player> handler) {
				worldChangeHandler = handler;
			}

			@Override
			public IPlatform<Player> getPlatform() {
				return PlatformPluginBukkit.this;
			}

			@Override
			public InitLocalMode<Player> localMode() {
				localMode = true;
				return new InitLocalMode<Player>() {
					@Override
					public void setOnInitializePlayer(Consumer<IEaglercraftInitializePlayerEvent<Player>> handler) {
						initializePlayerHandler = handler;
					}

					@Override
					public void setOnWebViewChannel(Consumer<IEaglercraftWebViewChannelEvent<Player>> handler) {
						localWebViewChannelHandler = handler;
					}

					@Override
					public void setOnWebViewMessage(Consumer<IEaglercraftWebViewMessageEvent<Player>> handler) {
						localWebViewMessageHandler = handler;
					}

					@Override
					public void setOnVoiceChange(Consumer<IEaglercraftVoiceChangeEvent<Player>> handler) {
						localVoiceChangeHandler = handler;
					}
				};
			}

			@Override
			public InitRemoteMode<Player> remoteMode() {
				localMode = false;
				return new InitRemoteMode<Player>() {
					@Override
					public void setEaglerPlayerChannels(Collection<IBackendRPCMessageChannel<Player>> channels) {
						channelsList = channels;
					}
				};
			}
		};
		EaglerXBackendRPCFactory.<Player>create().load(init);
	}

	@Override
	public void onEnable() {
		Server server = getServer();
		PluginManager pluginManager = server.getPluginManager();
		pluginManager.registerEvents(new BukkitListener(this), this);
		if (localMode) {
			pluginManager.registerEvents(new BukkitListenerLocal(this), this);
		} else {
			Messenger msgr = server.getMessenger();
			for (IBackendRPCMessageChannel<Player> channel : channelsList) {
				IBackendRPCMessageHandler<Player> handler = channel.getHandler();
				msgr.registerOutgoingPluginChannel(this, channel.getModernName());
				if (!post_v1_13) {
					msgr.registerOutgoingPluginChannel(this, channel.getLegacyName());
				}
				if (handler != null) {
					PluginMessageListener ls = (ch, player, data) -> {
						IPlatformPlayer<Player> platformPlayer = getPlayer(player);
						if (platformPlayer != null) {
							handler.handle(channel, platformPlayer, data);
						}
					};
					msgr.registerIncomingPluginChannel(this, channel.getModernName(), ls);
					if (!post_v1_13) {
						msgr.registerIncomingPluginChannel(this, channel.getLegacyName(), ls);
					}
				}
			}
		}
		if (onServerEnable != null) {
			onServerEnable.run();
		}
	}

	@Override
	public void onDisable() {
		if (onServerDisable != null) {
			onServerDisable.run();
		}
		if (!localMode) {
			Messenger msgr = getServer().getMessenger();
			for (IBackendRPCMessageChannel<Player> channel : channelsList) {
				IBackendRPCMessageHandler<Player> handler = channel.getHandler();
				msgr.unregisterOutgoingPluginChannel(this, channel.getModernName());
				if (!post_v1_13) {
					msgr.unregisterOutgoingPluginChannel(this, channel.getLegacyName());
				}
				if (handler != null) {
					msgr.unregisterIncomingPluginChannel(this, channel.getModernName());
					if (!post_v1_13) {
						msgr.unregisterIncomingPluginChannel(this, channel.getLegacyName());
					}
				}
			}
		}
	}

	@Override
	public EnumAdapterPlatformType getType() {
		return EnumAdapterPlatformType.BUKKIT;
	}

	@Override
	public Class<Player> getPlayerClass() {
		return Player.class;
	}

	@Override
	public String getPluginId() {
		return getDescription().getName();
	}

	@Override
	public IPlatformLogger logger() {
		return loggerImpl;
	}

	@Override
	public IEventDispatchAdapter<Player> eventDispatcher() {
		return eventDispatcher;
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<Player>> playerCallback) {
		playerInstanceMap.values().forEach(playerCallback);
	}

	@Override
	public IPlatformPlayer<Player> getPlayer(Player playerObj) {
		return playerInstanceMap.get(playerObj);
	}

	@Override
	public IPlatformPlayer<Player> getPlayer(String username) {
		Player player = getServer().getPlayer(username);
		if (player != null) {
			return playerInstanceMap.get(player);
		} else {
			return null;
		}
	}

	@Override
	public IPlatformPlayer<Player> getPlayer(UUID uuid) {
		Player player = getServer().getPlayer(uuid);
		if (player != null) {
			return playerInstanceMap.get(player);
		} else {
			return null;
		}
	}

	@Override
	public Collection<IPlatformPlayer<Player>> getAllPlayers() {
		return ImmutableList.copyOf(playerInstanceMap.values());
	}

	@Override
	public IPlatformScheduler getScheduler() {
		return schedulerImpl;
	}

	@Override
	public IPlatformComponentHelper getComponentHelper() {
		return componentHelper;
	}

	void initializePlayer(Player player) {
		BukkitPlayer p = new BukkitPlayer(this, player);
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Player>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}

			@Override
			public IPlatformPlayer<Player> getPlayer() {
				return p;
			}

			@Override
			public boolean isEaglerPlayerProperty() {
				return BukkitUnsafe.isEaglerPlayerProperty(player);
			}
		});
		playerInstanceMap.put(player, p);
		p.confirmTask = getServer().getScheduler().runTaskLater(this, () -> {
			p.confirmTask = null;
			getLogger().warning(
					"Player " + p.getUsername() + " was initialized, but never fired PlayerJoinEvent, dropping...");
			dropPlayer(player);
		}, 5000l);
	}

	void confirmPlayer(Player player) {
		BukkitPlayer p = playerInstanceMap.get(player);
		if (p != null) {
			BukkitTask conf = p.xchgConfirmTask();
			if (conf != null) {
				conf.cancel();
			}
			playerInitializer.confirmPlayer(p);
		}
	}

	void dropPlayer(Player player) {
		BukkitPlayer p = playerInstanceMap.remove(player);
		if (p != null) {
			BukkitTask conf = p.xchgConfirmTask();
			if (conf != null) {
				conf.cancel();
			}
			playerInitializer.destroyPlayer(p);
		}
	}

	@Override
	public boolean isPost_v1_13() {
		return post_v1_13;
	}

	private boolean checkPost_v1_13() {
		String[] ver = getServer().getBukkitVersion().split("[\\.\\-]");
		if (ver.length >= 2) {
			try {
				int i = Integer.parseInt(ver[0]);
				int j = Integer.parseInt(ver[1]);
				return i > 1 || (i == 1 && j >= 13);
			} catch (NumberFormatException ex) {
			}
		}
		return false;
	}

}
