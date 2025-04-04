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

import net.lax1dude.eaglercraft.backend.rpc.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCImpl;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageChannel;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageHandler;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPreInitializer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.rpc.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCBase;
import net.lax1dude.eaglercraft.backend.rpc.bukkit.event.BukkitEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

public class PlatformPluginBukkit extends JavaPlugin implements IPlatform<Player> {

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player> eventDispatcher;
	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IBackendRPCPlayerInitializer<Object, Object, Player> playerInitializer;
	protected boolean post_v1_13;
	protected IPlatformScheduler schedulerImpl;

	protected boolean localMode;
	protected Collection<IBackendRPCMessageChannel<Player>> channelsList;
	protected Consumer<IEaglercraftInitializePlayerEvent<Player>> localInitHandler;
	protected Consumer<IEaglercraftDestroyPlayerEvent<Player>> localDestroyHandler;
	protected Consumer<IEaglercraftWebViewChannelEvent<Player>> localWebViewChannelHandler;
	protected Consumer<IEaglercraftWebViewMessageEvent<Player>> localWebViewMessageHandler;
	protected Consumer<IEaglercraftVoiceChangeEvent<Player>> localToggleVoiceHandler;

	private final ConcurrentMap<Player, BukkitPlayer> playerInstanceMap = (new MapMaker()).initialCapacity(256)
			.concurrencyLevel(16).makeMap();

	@Override
	public void onLoad() {
		post_v1_13 = isPost_v1_13();
		Server server = getServer();
		loggerImpl = new JavaLogger(getLogger());
		eventDispatcher = new BukkitEventDispatchAdapter(this, server.getPluginManager());
		schedulerImpl = new BukkitScheduler(this, server.getScheduler());
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
			public void setPlayerInitializer(IBackendRPCPlayerInitializer<?, ?, Player> initializer) {
				playerInitializer = (IBackendRPCPlayerInitializer<Object, Object, Player>) initializer;
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
					public void setOnEaglerPlayerInitialized(Consumer<IEaglercraftInitializePlayerEvent<Player>> handler) {
						localInitHandler = handler;
					}
					@Override
					public void setOnEaglerPlayerDestroyed(Consumer<IEaglercraftDestroyPlayerEvent<Player>> handler) {
						localDestroyHandler = handler;
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
					public void setOnToggleVoice(Consumer<IEaglercraftVoiceChangeEvent<Player>> handler) {
						localToggleVoiceHandler = handler;
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
		((IBackendRPCImpl<Player>) EaglerXBackendRPCBase.<Player>init()).load(init);
	}

	@Override
	public void onEnable() {
		Server server = getServer();
		PluginManager pluginManager = server.getPluginManager();
		pluginManager.registerEvents(new BukkitListener(this), this);
		if(localMode) {
			pluginManager.registerEvents(new BukkitListenerLocal(this), this);
		}else {
			Messenger msgr = server.getMessenger();
			for(IBackendRPCMessageChannel<Player> channel : channelsList) {
				IBackendRPCMessageHandler<Player> handler = channel.getHandler();
				msgr.registerOutgoingPluginChannel(this, channel.getModernName());
				if(!post_v1_13) {
					msgr.registerOutgoingPluginChannel(this, channel.getLegacyName());
				}
				if(handler != null) {
					PluginMessageListener ls = (ch, player, data) -> {
						IPlatformPlayer<Player> platformPlayer = getPlayer(player);
						if(platformPlayer != null) {
							handler.handle(channel, platformPlayer, data);
						}
					};
					msgr.registerIncomingPluginChannel(this, channel.getModernName(), ls);
					if(!post_v1_13) {
						msgr.registerIncomingPluginChannel(this, channel.getLegacyName(), ls);
					}
				}
			}
		}
		if(onServerEnable != null) {
			onServerEnable.run();
		}
	}

	@Override
	public void onDisable() {
		if(onServerDisable != null) {
			onServerDisable.run();
		}
		if(!localMode) {
			Messenger msgr = getServer().getMessenger();
			for(IBackendRPCMessageChannel<Player> channel : channelsList) {
				IBackendRPCMessageHandler<Player> handler = channel.getHandler();
				msgr.unregisterOutgoingPluginChannel(this, channel.getModernName());
				if(!post_v1_13) {
					msgr.unregisterOutgoingPluginChannel(this, channel.getLegacyName());
				}
				if(handler != null) {
					msgr.unregisterIncomingPluginChannel(this, channel.getModernName());
					if(!post_v1_13) {
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
		if(player != null) {
			return playerInstanceMap.get(player);
		}else {
			return null;
		}
	}

	@Override
	public IPlatformPlayer<Player> getPlayer(UUID uuid) {
		Player player = getServer().getPlayer(uuid);
		if(player != null) {
			return playerInstanceMap.get(player);
		}else {
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

	void initializePlayer(Player player) {
		BukkitPlayer p = new BukkitPlayer(this, player);
		playerInitializer.initializePlayer(new IPlatformPreInitializer<Object, Player>() {
			@Override
			public void setPreAttachment(Object attachment) {
				p.preAttachment = attachment;
			}
			@Override
			public IPlatformPlayer<Player> getPlayer() {
				return p;
			}
		});
		playerInstanceMap.put(player, p);
		p.confirmTask = getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
			p.confirmTask = null;
			getLogger().warning("Player " + p.getUsername() + " was initialized, but never fired PlayerJoinEvent, dropping...");
			dropPlayer(player);
		}, 5000l);
	}

	void confirmPlayer(Player player) {
		BukkitPlayer p = playerInstanceMap.get(player);
		if(p != null) {
			BukkitTask conf = p.confirmTask;
			if(conf != null) {
				p.confirmTask = null;
				conf.cancel();
			}
			playerInitializer.confirmPlayer(new IPlatformPlayerInitializer<Object, Object, Player>() {
				@Override
				public void setPlayerAttachment(Object attachment) {
					p.attachment = attachment;
				}
				@Override
				public Object getPreAttachment() {
					return p.preAttachment;
				}
				@Override
				public IPlatformPlayer<Player> getPlayer() {
					return p;
				}
			});
			p.preAttachment = null;
		}
	}

	void dropPlayer(Player player) {
		BukkitPlayer p = playerInstanceMap.remove(player);
		if(p != null) {
			BukkitTask conf = p.confirmTask;
			if(conf != null) {
				p.confirmTask = null;
				conf.cancel();
			}
			playerInitializer.destroyPlayer(p);
		}
	}

	private boolean isPost_v1_13() {
		String[] ver = getServer().getVersion().split("[\\.\\-]");
		if(ver.length >= 2) {
			try {
				return Integer.parseInt(ver[0]) >= 1 || Integer.parseInt(ver[1]) >= 13;
			}catch(NumberFormatException ex) {
			}
		}
		return false;
	}

}
