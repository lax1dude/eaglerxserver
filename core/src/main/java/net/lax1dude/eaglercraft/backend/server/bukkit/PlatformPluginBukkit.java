package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerNettyPipelineInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformNettyPipelineInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent.EnumPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe.LoginConnectionHolder;
import net.lax1dude.eaglercraft.backend.server.bukkit.event.BukkitEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.bungee.chat.BungeeComponentHelper;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlatformPluginBukkit extends JavaPlugin implements IPlatform<Player> {

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player, BaseComponent> eventDispatcherImpl;

	protected Runnable cleanupListeners;
	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<Object> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<Object, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, Player> playerInitializer;
	protected Collection<IEaglerXServerCommandType<Player>> commandsList;
	protected IEaglerXServerListener listenerConf;
	protected Collection<IEaglerXServerMessageChannel> playerChannelsList;
	protected IPlatformScheduler schedulerImpl;
	protected IPlatformComponentHelper componentHelperImpl;
	protected CommandSender cacheConsoleCommandSenderInstance;
	protected IPlatformCommandSender<Player> cacheConsoleCommandSenderHandle;

	private final ConcurrentMap<Player, BukkitPlayer> playerInstanceMap = new ConcurrentHashMap<>(1024);

	public PlatformPluginBukkit() {
	}

	@Override
	public void onLoad() {
		eventDispatcherImpl = new BukkitEventDispatchAdapter(this, getServer().getPluginManager(),
				getServer().getScheduler());
		schedulerImpl = new BukkitScheduler(this, getServer().getScheduler());
		componentHelperImpl = new BungeeComponentHelper();
		cacheConsoleCommandSenderInstance = getServer().getConsoleSender();
		cacheConsoleCommandSenderHandle = new BukkitConsole(cacheConsoleCommandSenderInstance);
		IEaglerXServerImpl<Player> serverImpl = new EaglerXServer<>();
		serverImpl.load(new InitNonProxying<Player>() {

			@Override
			public void setOnServerEnable(Runnable enable) {
				onServerEnable = enable;
			}

			@Override
			public void setOnServerDisable(Runnable disable) {
				onServerEnable = disable;
			}

			@Override
			public void setPipelineInitializer(IEaglerXServerNettyPipelineInitializer<?> initializer) {
				pipelineInitializer = (IEaglerXServerNettyPipelineInitializer<Object>) initializer;
			}

			@Override
			public void setConnectionInitializer(IEaglerXServerConnectionInitializer<?, ?> initializer) {
				connectionInitializer = (IEaglerXServerConnectionInitializer<Object, Object>) initializer;
			}

			@Override
			public void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, Player> initializer) {
				playerInitializer = (IEaglerXServerPlayerInitializer<Object, Object, Player>) initializer;
			}

			@Override
			public void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel> channels) {
				playerChannelsList = channels;
			}

			@Override
			public IPlatform<Player> getPlatform() {
				return PlatformPluginBukkit.this;
			}

			@Override
			public void setCommandRegistry(Collection<IEaglerXServerCommandType<Player>> commands) {
				commandsList = commands;
			}

			@Override
			public void setEaglerListener(IEaglerXServerListener listener) {
				listenerConf = listener;
			}

		});
	}

	private static final ImmutableMap<String, EnumPipelineComponent> PIPELINE_COMPONENTS_MAP = 
			ImmutableMap.<String, EnumPipelineComponent>builder()
			.put("splitter", EnumPipelineComponent.FRAME_DECODER)
			.put("prepender", EnumPipelineComponent.FRAME_ENCODER)
			.put("encoder", EnumPipelineComponent.MINECRAFT_ENCODER)
			.put("decoder", EnumPipelineComponent.MINECRAFT_DECODER)
			.put("via-encoder", EnumPipelineComponent.VIA_ENCODER)
			.put("via-decoder", EnumPipelineComponent.VIA_DECODER)
			.put("protocol_lib_inbound_interceptor", EnumPipelineComponent.PROTOCOLLIB_INBOUND_INTERCEPTOR)
			.put("protocol_lib_inbound_protocol_getter", EnumPipelineComponent.PROTOCOLLIB_PROTOCOL_GETTER_NAME)
			.put("protocol_lib_wire_packet_encoder", EnumPipelineComponent.PROTOCOLLIB_WIRE_PACKET_ENCODER)
			.put("timeout", EnumPipelineComponent.READ_TIMEOUT_HANDLER)
			.put("legacy_query", EnumPipelineComponent.BUKKIT_LEGACY_HANDLER)
			.put("packet_handler", EnumPipelineComponent.INBOUND_PACKET_HANDLER)
			.build();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
		CommandMap cmdMap = BukkitUnsafe.getCommandMap(getServer());
		for(IEaglerXServerCommandType<Player> cmd : commandsList) {
			cmdMap.register("eagler", new BukkitCommand(this, cmd));
		}
		cleanupListeners = BukkitUnsafe.injectChannelInitializer(getServer(), (channel) -> {
			if (!channel.isActive()) {
				return;
			}
			
			List<IPipelineComponent> pipelineList = new ArrayList<>();
			
			eag: for(;;) {
				ChannelPipeline pipeline = channel.pipeline();
				for(String str : pipeline.names()) {
					ChannelHandler handler = pipeline.get(str);
					if(handler != null) {
						pipelineList.add(new IPipelineComponent() {

							private EnumPipelineComponent type = null;

							@Override
							public EnumPipelineComponent getIdentifiedType() {
								if(type == null) {
									type = PIPELINE_COMPONENTS_MAP.getOrDefault(str, EnumPipelineComponent.UNIDENTIFIED);
								}
								return type;
							}

							@Override
							public String getName() {
								return str;
							}

							@Override
							public ChannelHandler getHandle() {
								return handler;
							}

						});
					}else {
						// pipeline changed
						pipelineList.clear();
						continue eag;
					}
				}
				break eag;
			}
			
			pipelineInitializer.initialize(new IPlatformNettyPipelineInitializer<Object>() {
				@Override
				public void setAttachment(Object object) {
					channel.attr(PipelineAttributes.<Object>pipelineData()).set(object);
				}
				@Override
				public List<IPipelineComponent> getPipeline() {
					return pipelineList;
				}
				@Override
				public IEaglerXServerListener getListener() {
					return listenerConf;
				}
				@Override
				public Channel getChannel() {
					return channel;
				}
			});
			
		}, listenerConf);
		if(onServerEnable != null) {
			onServerEnable.run();
		}
	}

	@Override
	public void onDisable() {
		if(cleanupListeners != null) {
			cleanupListeners.run();
			cleanupListeners = null;
		}
		if(onServerDisable != null) {
			onServerDisable.run();
		}
	}

	@Override
	public EnumAdapterPlatformType getType() {
		return EnumAdapterPlatformType.BUKKIT;
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
	public IPlatformCommandSender<Player> getConsole() {
		return cacheConsoleCommandSenderHandle;
	}

	IPlatformCommandSender<Player> getCommandSender(CommandSender obj) {
		if(obj == null) {
			return null;
		}else if(obj instanceof Player) {
			return getPlayer((Player) obj);
		}else if(obj == cacheConsoleCommandSenderInstance) {
			return cacheConsoleCommandSenderHandle;
		}else {
			return new BukkitConsole(obj);
		}
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<Player>> playerCallback) {
		playerInstanceMap.values().forEach(playerCallback);
	}

	@Override
	public Collection<IPlatformPlayer<Player>> getAllPlayers() {
		return ImmutableList.copyOf(playerInstanceMap.values());
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
	public IEventDispatchAdapter<Player, ?> eventDispatcher() {
		return eventDispatcherImpl;
	}

	@Override
	public Class<Player> getPlayerClass() {
		return Player.class;
	}

	@Override
	public IPlatformScheduler getScheduler() {
		return schedulerImpl;
	}

	@Override
	public Set<EnumConfigFormat> getConfigFormats() {
		return EnumConfigFormat.getSupported();
	}

	@Override
	public IPlatformComponentHelper getComponentHelper() {
		return componentHelperImpl;
	}

	public void initializeConnection(LoginConnectionHolder loginConnection, Object pipelineData,
			Consumer<BukkitConnection> setAttr) {
		BukkitConnection c = new BukkitConnection(this, loginConnection);
		setAttr.accept(c);
		connectionInitializer.initializeConnection(new IPlatformConnectionInitializer<Object, Object>() {
			@Override
			public void setConnectionAttachment(Object attachment) {
				c.attachment = attachment;
			}
			@Override
			public Object getPipelineAttachment() {
				return pipelineData;
			}
			@Override
			public IPlatformConnection getConnection() {
				return c;
			}
		});
	}

	public void initializePlayer(Player player, BukkitConnection connection,
			Consumer<BukkitConnection> setAttr) {
		BukkitPlayer p;
		final BukkitConnection c;
		if(connection == null) {
			// vanilla players won't have an initialized connection
			c = new BukkitConnection(this, null);
			p = new BukkitPlayer(player, c);
			setAttr.accept(c);
			connectionInitializer.initializeConnection(new IPlatformConnectionInitializer<Object, Object>() {
				@Override
				public void setConnectionAttachment(Object attachment) {
					c.attachment = attachment;
				}
				@Override
				public Object getPipelineAttachment() {
					return null;
				}
				@Override
				public IPlatformConnection getConnection() {
					return c;
				}
			});
		}else {
			c = connection;
			p = new BukkitPlayer(player, c);
		}
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Object, Player>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}
			@Override
			public Object getConnectionAttachment() {
				return c;
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

	public void confirmPlayer(Player player) {
		BukkitPlayer p = playerInstanceMap.get(player);
		if(p != null) {
			BukkitTask conf = p.confirmTask;
			if(conf != null) {
				p.confirmTask = null;
				conf.cancel();
			}
		}
	}

	public void dropPlayer(Player player) {
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

}
