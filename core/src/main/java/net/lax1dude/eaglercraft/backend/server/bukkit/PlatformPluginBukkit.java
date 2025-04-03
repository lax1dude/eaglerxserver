package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.util.Attribute;
import net.lax1dude.eaglercraft.backend.server.adapter.AbortLoadException;
import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerJoinListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
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
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent.EnumPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.bukkit.BukkitUnsafe.LoginConnectionHolder;
import net.lax1dude.eaglercraft.backend.server.bukkit.event.BukkitEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.bungee.chat.BungeeComponentHelper;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.util.CompressionDisablerHack;
import net.lax1dude.eaglercraft.backend.server.util.DecompressionDisablerHack;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlatformPluginBukkit extends JavaPlugin implements IPlatform<Player>, IPlatformServer<Player> {

	protected final Map<String, IPlatformServer<Player>> registeredServers;

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player, BaseComponent> eventDispatcherImpl;

	protected boolean aborted = false;
	protected Runnable cleanupListeners;
	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<Object> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<Object, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, Player> playerInitializer;
	protected IEaglerXServerJoinListener<Player> serverJoinListener;
	protected Collection<IEaglerXServerCommandType<Player>> commandsList;
	protected IEaglerXServerListener listenerConf;
	protected Collection<IEaglerXServerMessageChannel<Player>> playerChannelsList;
	protected boolean post_v1_13;
	protected IPlatformScheduler schedulerImpl;
	protected IPlatformComponentHelper componentHelperImpl;
	protected CommandSender cacheConsoleCommandSenderInstance;
	protected IPlatformCommandSender<Player> cacheConsoleCommandSenderHandle;
	protected boolean enableNativeTransport;
	protected EventLoopGroup eventLoopGroup;

	private final ConcurrentMap<Player, BukkitPlayer> playerInstanceMap = (new MapMaker()).initialCapacity(512)
			.concurrencyLevel(16).makeMap();

	private final ChannelHandler compressionDisabler = new CompressionDisablerHack("compress", BukkitUnsafe::disposeCompressionHandler);
	private final ChannelHandler decompressionDisabler = new DecompressionDisablerHack("decompress", BukkitUnsafe::disposeDecompressionHandler);

	private final ChannelFactory<? extends Channel> channelFactoryNIO = NioSocketChannel::new;
	private final ChannelFactory<? extends Channel> channelFactoryEpoll = EpollSocketChannel::new;
	private final ChannelFactory<? extends ServerChannel> serverChannelFactoryNIO = NioServerSocketChannel::new;
	private final ChannelFactory<? extends ServerChannel> serverChannelFactoryEpoll = EpollServerSocketChannel::new;

	public PlatformPluginBukkit() {
		registeredServers = ImmutableMap.of("default", this);
	}

	@Override
	public void onLoad() {
		post_v1_13 = isPost_v1_13();
		Server server = getServer();
		loggerImpl = new JavaLogger(getLogger());
		eventDispatcherImpl = new BukkitEventDispatchAdapter(this, server.getPluginManager(), server.getScheduler());
		schedulerImpl = new BukkitScheduler(this, server.getScheduler());
		componentHelperImpl = new BungeeComponentHelper();
		cacheConsoleCommandSenderInstance = server.getConsoleSender();
		cacheConsoleCommandSenderHandle = new BukkitConsole(cacheConsoleCommandSenderInstance);
		enableNativeTransport = Epoll.isAvailable() && BukkitUnsafe.isEnableNativeTransport(server);
		eventLoopGroup = BukkitUnsafe.getEventLoopGroup(server, enableNativeTransport);
		if(enableNativeTransport && !(eventLoopGroup instanceof EpollEventLoopGroup)) {
			enableNativeTransport = false;
		}
		Init<Player> init = new InitNonProxying<Player>() {

			@Override
			public void setOnServerEnable(Runnable enable) {
				onServerEnable = enable;
			}

			@Override
			public void setOnServerDisable(Runnable disable) {
				onServerDisable = disable;
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
			public void setServerJoinListener(IEaglerXServerJoinListener<Player> listener) {
				serverJoinListener = listener;
			}

			@Override
			public void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel<Player>> channels) {
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

			@Override
			public SocketAddress getListenerAddress() {
				return new InetSocketAddress(server.getIp(), server.getPort());
			}

		};
		try {
			((IEaglerXServerImpl<Player>) new EaglerXServer<Player>()).load(init);
		}catch(AbortLoadException ex) {
			logger().error("Server startup aborted: " + ex.getMessage());
			Throwable t = ex.getCause();
			if(t != null) {
				logger().error("Caused by: ", t);
			}
			aborted = true;
			throw new IllegalStateException("Startup aborted");
		}
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
			.put("pe-decoder-packetevents", EnumPipelineComponent.PACKETEVENTS_DECODER)
			.put("pe-encoder-packetevents", EnumPipelineComponent.PACKETEVENTS_ENCODER)
			.put("pe-timeout-handler-packetevents", EnumPipelineComponent.PACKETEVENTS_TIMEOUT_HANDLER)
			.build();

	@Override
	public void onEnable() {
		if(aborted) {
			return;
		}
		Server server = getServer();
		server.getPluginManager().registerEvents(new BukkitListener(this), this);
		CommandMap cmdMap = BukkitUnsafe.getCommandMap(server);
		for(IEaglerXServerCommandType<Player> cmd : commandsList) {
			cmdMap.register("eagler", new BukkitCommand(this, cmd));
		}
		Messenger msgr = server.getMessenger();
		PluginMessageListener ls = (ch, player, data) -> {
			BukkitPlayer playerInstance = playerInstanceMap.get(player);
			if(playerInstance != null) {
				playerInstance.handleMCBrandMessage(data);
			}
		};
		if(!post_v1_13) {
			msgr.registerIncomingPluginChannel(this, "MC|Brand", ls);
		}
		msgr.registerIncomingPluginChannel(this, "minecraft:brand", ls);
		for(IEaglerXServerMessageChannel<Player> channel : playerChannelsList) {
			IEaglerXServerMessageHandler<Player> handler = channel.getHandler();
			msgr.registerOutgoingPluginChannel(this, channel.getModernName());
			if(!post_v1_13) {
				msgr.registerOutgoingPluginChannel(this, channel.getLegacyName());
			}
			if(handler != null) {
				ls = (ch, player, data) -> {
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
		cleanupListeners = BukkitUnsafe.injectChannelInitializer(getServer(), (channel) -> {
			if (!channel.isActive()) {
				return;
			}
			
			List<IPipelineComponent> pipelineList = new ArrayList<>();
			
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
				}
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
				public Consumer<SocketAddress> realAddressHandle() {
					return (addr) -> {
						Object o = channel.pipeline().get("packet_handler");
						if(o != null) {
							BukkitUnsafe.updateRealAddress(o, addr);
						}
					};
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
		if(aborted) {
			return;
		}
		if(cleanupListeners != null) {
			cleanupListeners.run();
			cleanupListeners = null;
		}
		if(onServerDisable != null) {
			onServerDisable.run();
		}
		Server server = getServer();
		Messenger msgr = server.getMessenger();
		if(!post_v1_13) {
			msgr.unregisterIncomingPluginChannel(this, "MC|Brand");
		}
		msgr.unregisterIncomingPluginChannel(this, "minecraft:brand");
		for(IEaglerXServerMessageChannel<Player> channel : playerChannelsList) {
			IEaglerXServerMessageHandler<Player> handler = channel.getHandler();
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

	public IPlatformServer<Player> getPlatformServerInstance() {
		return this;
	}

	@Override
	public Map<String, IPlatformServer<Player>> getRegisteredServers() {
		return registeredServers;
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

	@Override
	public boolean isOnlineMode() {
		return getServer().getOnlineMode();
	}

	@Override
	public boolean isModernPluginChannelNamesOnly() {
		return post_v1_13;
	}

	@Override
	public int getPlayerTotal() {
		return getServer().getOnlinePlayers().size();
	}

	@Override
	public int getPlayerMax() {
		return getServer().getMaxPlayers();
	}

	@Override
	public boolean isEaglerRegistered() {
		return true;
	}

	@Override
	public String getServerConfName() {
		return "default";
	}

	@Override
	public void handleConnectionInitFallback(Channel channel) {
		LoginConnectionHolder holder = BukkitUnsafe.getLoginConnection(channel.pipeline().get("packet_handler"));
		Object pipelineData = channel.attr(PipelineAttributes.<Object>pipelineData()).getAndSet(null);
		Attribute<BukkitConnection> attr = channel.attr(PipelineAttributes.<BukkitConnection>connectionData());
		initializeConnection(holder, pipelineData, attr::set);
	}

	@Override
	public void handleUndoCompression(ChannelHandlerContext ctx) {
		ctx.pipeline().addAfter("compress", "compress-disable-hack", compressionDisabler);
		ctx.pipeline().addBefore("decompress", "decompress-disable-hack", decompressionDisabler);
	}

	@Override
	public ChannelFactory<? extends Channel> getChannelFactory(SocketAddress address) {
		if(address instanceof DomainSocketAddress) {
			throw new UnsupportedOperationException("Unix sockets not supported by this platform!");
		}
		if(enableNativeTransport) {
			return channelFactoryEpoll;
		}else {
			return channelFactoryNIO;
		}
	}

	@Override
	public ChannelFactory<? extends ServerChannel> getServerChannelFactory(SocketAddress address) {
		if(address instanceof DomainSocketAddress) {
			throw new UnsupportedOperationException("Unix sockets not supported by this platform!");
		}
		if(enableNativeTransport) {
			return serverChannelFactoryEpoll;
		}else {
			return serverChannelFactoryNIO;
		}
	}

	@Override
	public EventLoopGroup getBossEventLoopGroup() {
		return null;
	}

	@Override
	public EventLoopGroup getWorkerEventLoopGroup() {
		return eventLoopGroup;
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
			@Override
			public void setUniqueId(UUID uuid) {
			}
			@Override
			public void setTexturesProperty(String propertyValue, String propertySignature) {
				c.texturesPropertyValue = propertyValue;
				c.texturesPropertySignature = propertySignature;
			}
			@Override
			public void setEaglerPlayerProperty(boolean enable) {
				c.eaglerPlayerProperty = enable;
			}
		});
	}

	public void initializePlayer(Player player, BukkitConnection connection,
			Consumer<BukkitConnection> setAttr, Runnable onComplete) {
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
				@Override
				public void setUniqueId(UUID uuid) {
				}
				@Override
				public void setTexturesProperty(String propertyValue, String propertySignature) {
					c.texturesPropertyValue = propertyValue;
					c.texturesPropertySignature = propertySignature;
				}
				@Override
				public void setEaglerPlayerProperty(boolean enable) {
					c.eaglerPlayerProperty = enable;
				}
			});
		}else {
			c = connection;
			p = new BukkitPlayer(player, c);
		}
		if(c.eaglerPlayerProperty || c.texturesPropertyValue != null) {
			BukkitUnsafe.PropertyInjector injector = BukkitUnsafe.propertyInjector(player);
			if(c.texturesPropertyValue != null) {
				injector.injectTexturesProperty(c.texturesPropertyValue, c.texturesPropertySignature);
				c.texturesPropertyValue = null;
				c.texturesPropertySignature = null;
			}
			if(c.eaglerPlayerProperty) {
				injector.injectIsEaglerPlayerProperty();
			}
			injector.complete();
		}
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Object, Player>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}
			@Override
			public Object getConnectionAttachment() {
				return c.attachment;
			}
			@Override
			public IPlatformPlayer<Player> getPlayer() {
				return p;
			}
			@Override
			public void complete() {
				playerInstanceMap.put(player, p);
				p.confirmTask = getServer().getScheduler().runTaskLaterAsynchronously(PlatformPluginBukkit.this, () -> {
					p.confirmTask = null;
					getLogger().warning("Player " + p.getUsername() + " was initialized, but never fired PlayerJoinEvent, dropping...");
					dropPlayer(player);
				}, 5000l);
				onComplete.run();
			}
			@Override
			public void cancel() {
				onComplete.run();
			}
		});
	}

	public void confirmPlayer(Player player) {
		BukkitPlayer p = playerInstanceMap.get(player);
		if(p != null) {
			BukkitTask conf = p.confirmTask;
			if(conf != null) {
				p.confirmTask = null;
				conf.cancel();
			}
			IEaglerXServerJoinListener<Player> listener = serverJoinListener;
			if(listener != null) {
				listener.handle(p, this);
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
