package net.lax1dude.eaglercraft.backend.server.velocity;

import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.unix.DomainSocketAddress;
import net.kyori.adventure.text.Component;
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
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
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
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.SLF4JLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent.EnumPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServerVersion;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.velocity.chat.VelocityComponentHelper;
import net.lax1dude.eaglercraft.backend.server.velocity.event.VelocityEventDispatchAdapter;

@Plugin(
	id = PlatformPluginVelocity.PLUGIN_ID,
	name = PlatformPluginVelocity.PLUGIN_NAME,
	version = PlatformPluginVelocity.PLUGIN_VERSION,
	description = "Official EaglercraftX plugin for Velocity servers",
	authors = {
		PlatformPluginVelocity.PLUGIN_AUTHOR
	},
	dependencies = {
		@Dependency(id = "skinsrestorer", optional = true)
	}
)
public class PlatformPluginVelocity implements IPlatform<Player> {

	public static final String PLUGIN_ID = EaglerXServerAPI.PLUGIN_ID;
	public static final String PLUGIN_NAME = EaglerXServerVersion.BRAND;
	public static final String PLUGIN_AUTHOR = EaglerXServerVersion.AUTHOR;
	public static final String PLUGIN_VERSION = EaglerXServerVersion.VERSION;

	private ProxyServer proxy;
	private Logger proxyLogger;
	private Path dataDir;
	private File dataDirFile;
	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player, Component> eventDispatcherImpl;

	protected boolean aborted = false;
	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<IPipelineData> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<IPipelineData, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, Player> playerInitializer;
	protected IEaglerXServerJoinListener<Player> serverJoinListener;
	protected Collection<IEaglerXServerCommandType<Player>> commandsList;
	protected Collection<CommandMeta> registeredCommandsList;
	protected Collection<IEaglerXServerListener> listenersList;
	protected Collection<IEaglerXServerMessageChannel<Player>> playerChannelsList;
	protected Collection<IEaglerXServerMessageChannel<Player>> backendChannelsList;
	protected IPlatformScheduler schedulerImpl;
	protected IPlatformComponentHelper componentHelperImpl;
	protected CommandSource cacheConsoleCommandSenderInstance;
	protected IPlatformCommandSender<Player> cacheConsoleCommandSenderHandle;
	protected Map<String, IPlatformServer<Player>> registeredServers;
	protected ChannelIdentifier[] registeredChannels;
	protected Map<ChannelIdentifier, PluginMessageHandler> registeredChannelsMap;
	protected ChannelFactory<? extends Channel> channelFactory;
	protected ChannelFactory<? extends Channel> unixChannelFactory;
	protected ChannelFactory<? extends ServerChannel> serverChannelFactory;
	protected ChannelFactory<? extends ServerChannel> serverUnixChannelFactory;
	protected EventLoopGroup bossEventLoopGroup;
	protected EventLoopGroup workerEventLoopGroup;

	public class PluginMessageHandler {

		public final boolean backend;
		public final IEaglerXServerMessageChannel<Player> channel;
		public final IEaglerXServerMessageHandler<Player> handler;

		protected PluginMessageHandler(boolean backend, IEaglerXServerMessageChannel<Player> channel,
				IEaglerXServerMessageHandler<Player> handler) {
			this.backend = backend;
			this.channel = channel;
			this.handler = handler;
		}

	}

	private final ConcurrentMap<Player, VelocityPlayer> playerInstanceMap = (new MapMaker()).initialCapacity(1024)
			.concurrencyLevel(16).makeMap();

	protected ListenerInitList listenersToInit = null;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		proxyLogger = loggerIn;
		dataDir = dataDirIn;
		dataDirFile = dataDirIn.toFile();
		loggerImpl = new SLF4JLogger(loggerIn);
		eventDispatcherImpl = new VelocityEventDispatchAdapter(proxy.getEventManager());
    	schedulerImpl = new VelocityScheduler(this, proxyIn.getScheduler());
    	componentHelperImpl = new VelocityComponentHelper();
    	cacheConsoleCommandSenderInstance = proxyIn.getConsoleCommandSource();
    	cacheConsoleCommandSenderHandle = new VelocityConsole(cacheConsoleCommandSenderInstance);
    	registeredCommandsList = new ArrayList<>();
    	ImmutableMap.Builder<String, IPlatformServer<Player>> builder = ImmutableMap.builder();
    	for(RegisteredServer server : proxyIn.getAllServers()) {
    		builder.put(server.getServerInfo().getName(), new VelocityServer(this, server, true));
    	}
    	registeredServers = builder.build();
    	channelFactory = VelocityUnsafe.getChannelFactory(proxyIn);
    	unixChannelFactory = VelocityUnsafe.getUnixChannelFactory(proxyIn);
    	serverChannelFactory = VelocityUnsafe.getServerChannelFactory(proxyIn);
    	serverUnixChannelFactory = VelocityUnsafe.getServerUnixChannelFactory(proxyIn);
    	bossEventLoopGroup = VelocityUnsafe.getBossEventLoopGroup(proxyIn);
    	workerEventLoopGroup = VelocityUnsafe.getWorkerEventLoopGroup(proxyIn);
		Init<Player> init = new InitProxying<Player>() {

			@Override
			public void setOnServerEnable(Runnable enable) {
				onServerEnable = enable;
			}

			@Override
			public void setOnServerDisable(Runnable disable) {
				onServerDisable = disable;
			}

			@Override
			public void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel<Player>> channels) {
				playerChannelsList = channels;
			}

			@Override
			public void setPipelineInitializer(IEaglerXServerNettyPipelineInitializer<? extends IPipelineData> initializer) {
				pipelineInitializer = (IEaglerXServerNettyPipelineInitializer<IPipelineData>) initializer;
			}

			@Override
			public void setConnectionInitializer(IEaglerXServerConnectionInitializer<? extends IPipelineData, ?> initializer) {
				connectionInitializer = (IEaglerXServerConnectionInitializer<IPipelineData, Object>) initializer;
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
			public void setCommandRegistry(Collection<IEaglerXServerCommandType<Player>> commands) {
				commandsList = commands;
			}

			@Override
			public IPlatform<Player> getPlatform() {
				return PlatformPluginVelocity.this;
			}

			@Override
			public void setEaglerListeners(Collection<IEaglerXServerListener> listeners) {
				listenersList = listeners;
			}

			@Override
			public void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel<Player>> channels) {
				backendChannelsList = channels;
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
			.put("frame-decoder", EnumPipelineComponent.FRAME_DECODER)
			.put("frame-encoder", EnumPipelineComponent.FRAME_ENCODER)
			.put("minecraft-encoder", EnumPipelineComponent.MINECRAFT_ENCODER)
			.put("minecraft-decoder", EnumPipelineComponent.MINECRAFT_DECODER)
			.put("via-encoder", EnumPipelineComponent.VIA_ENCODER)
			.put("via-decoder", EnumPipelineComponent.VIA_DECODER)
			.put("handler", EnumPipelineComponent.INBOUND_PACKET_HANDLER)
			.put("protocolize2-decoder", EnumPipelineComponent.PROTOCOLIZE_DECODER)
			.put("protocolize2-encoder", EnumPipelineComponent.PROTOCOLIZE_ENCODER)
			.put("read-timeout", EnumPipelineComponent.READ_TIMEOUT_HANDLER)
			.put("legacy-ping-encoder", EnumPipelineComponent.VELOCITY_LEGACY_PING_ENCODER)
			.put("pe-decoder-packetevents", EnumPipelineComponent.PACKETEVENTS_DECODER)
			.put("pe-encoder-packetevents", EnumPipelineComponent.PACKETEVENTS_ENCODER)
			.put("pe-timeout-handler-packetevents", EnumPipelineComponent.PACKETEVENTS_TIMEOUT_HANDLER)
			.build();

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
		if(aborted) {
			return;
		}
		aborted = true; // Will set to false if onProxyInit completes normally
    	proxy.getEventManager().register(this, new VelocityListener(this));
    	listenersToInit = new ListenerInitList(listenersList);
    	registeredCommandsList.clear();
    	for(IEaglerXServerCommandType<Player> cmd : commandsList) {
    		registeredCommandsList.add((new VelocityCommand(this, cmd)).register());
    	}
    	ImmutableMap.Builder<ChannelIdentifier, PluginMessageHandler> channelMapBuilder = ImmutableMap.builder();
    	for(IEaglerXServerMessageChannel<Player> ch : playerChannelsList) {
    		PluginMessageHandler handler = new PluginMessageHandler(false, ch, ch.getHandler());
    		channelMapBuilder.put(new LegacyChannelIdentifier(ch.getLegacyName()), handler);
    		channelMapBuilder.put(MinecraftChannelIdentifier.from(ch.getModernName()), handler);
    	}
    	for(IEaglerXServerMessageChannel<Player> ch : backendChannelsList) {
    		PluginMessageHandler handler = new PluginMessageHandler(true, ch, ch.getHandler());
    		channelMapBuilder.put(new LegacyChannelIdentifier(ch.getLegacyName()), handler);
    		channelMapBuilder.put(MinecraftChannelIdentifier.from(ch.getModernName()), handler);
    	}
    	registeredChannelsMap = channelMapBuilder.build();
    	registeredChannels = registeredChannelsMap.keySet().toArray(new ChannelIdentifier[registeredChannelsMap.size()]);
    	proxy.getChannelRegistrar().register(registeredChannels);
    	VelocityUnsafe.injectChannelInitializer(proxy, (listenerConf, channel) -> {
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
								if (type == EnumPipelineComponent.UNIDENTIFIED
										&& "io.netty.handler.codec.haproxy.HAProxyMessageDecoder"
												.equals(handler.getClass().getName())) {
									type = EnumPipelineComponent.HAPROXY_HANDLER;
								}
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
			pipelineInitializer.initialize(new IPlatformNettyPipelineInitializer<IPipelineData>() {
				@Override
				public void setAttachment(IPipelineData object) {
					channel.attr(PipelineAttributes.<IPipelineData>pipelineData()).set(object);
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
						Object o = channel.pipeline().get("handler");
						if(o != null) {
							VelocityUnsafe.updateRealAddress(o, addr);
						}
					};
				}
				@Override
				public Channel getChannel() {
					return channel;
				}
			});

    	});
		if(onServerEnable != null) {
			try {
				onServerEnable.run();
			}catch(AbortLoadException ex) {
				logger().error("Server startup aborted: " + ex.getMessage());
				Throwable t = ex.getCause();
				if(t != null) {
					logger().error("Caused by: ", t);
				}
				onProxyShutdown(null);
				throw new IllegalStateException("Startup aborted");
			}
		}
		aborted = false;
	}

	@Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
		if(aborted) {
			return;
		}
		if(onServerDisable != null) {
			onServerDisable.run();
		}
		proxy.getEventManager().unregisterListeners(this);
		for(CommandMeta cmd : registeredCommandsList) {
			proxy.getCommandManager().unregister(cmd);
		}
		registeredCommandsList.clear();
		if(registeredChannels != null) {
			proxy.getChannelRegistrar().unregister(registeredChannels);
			registeredChannels = null;
		}
	}

	@Override
	public EnumAdapterPlatformType getType() {
		return EnumAdapterPlatformType.VELOCITY;
	}

	@Override
	public String getVersion() {
		return proxy.getVersion().getVersion();
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	@Override
	public File getDataFolder() {
		return dataDirFile;
	}

	@Override
	public IPlatformLogger logger() {
		return loggerImpl;
	}

	@Override
	public IPlatformCommandSender<Player> getConsole() {
		return cacheConsoleCommandSenderHandle;
	}

	IPlatformCommandSender<Player> getCommandSender(CommandSource obj) {
		if(obj == null) {
			return null;
		}else if(obj instanceof Player player) {
			return getPlayer(player);
		}else if(obj == cacheConsoleCommandSenderInstance) {
			return cacheConsoleCommandSenderHandle;
		}else {
			return new VelocityConsole(obj);
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
		Optional<Player> player = proxy.getPlayer(username);
		if(player.isPresent()) {
			return playerInstanceMap.get(player.get());
		}else {
			return null;
		}
	}

	@Override
	public IPlatformPlayer<Player> getPlayer(UUID uuid) {
		Optional<Player> player = proxy.getPlayer(uuid);
		if(player.isPresent()) {
			return playerInstanceMap.get(player.get());
		}else {
			return null;
		}
	}

	@Override
	public Map<String, IPlatformServer<Player>> getRegisteredServers() {
		return registeredServers;
	}

	@Override
	public IPlatformServer<Player> getServer(String serverName) {
		IPlatformServer<Player> ret = registeredServers.get(serverName);
		if(ret != null) {
			return ret;
		}else {
			Optional<RegisteredServer> svr = proxy.getServer(serverName);
			if(svr.isPresent()) {
				return new VelocityServer(this, svr.get(), false);
			}
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

	@Override
	public boolean isOnlineMode() {
		return proxy.getConfiguration().isOnlineMode();
	}

	@Override
	public boolean isModernPluginChannelNamesOnly() {
		return false;
	}

	@Override
	public int getPlayerTotal() {
		return proxy.getPlayerCount();
	}

	@Override
	public int getPlayerMax() {
		return proxy.getConfiguration().getShowMaxPlayers();
	}

	@Override
	public void handleConnectionInitFallback(Channel channel) {
	}

	@Override
	public void handleUndoCompression(ChannelHandlerContext ctx) {
	}

	@Override
	public ChannelFactory<? extends Channel> getChannelFactory(SocketAddress address) {
		if(address instanceof DomainSocketAddress) {
			if(unixChannelFactory != null) {
				return unixChannelFactory;
			}else {
				throw new UnsupportedOperationException("Unix sockets unsupported on this platform");
			}
		}else {
			return channelFactory;
		}
	}

	@Override
	public ChannelFactory<? extends ServerChannel> getServerChannelFactory(SocketAddress address) {
		if(address instanceof DomainSocketAddress) {
			if(serverUnixChannelFactory != null) {
				return serverUnixChannelFactory;
			}else {
				throw new UnsupportedOperationException("Unix sockets unsupported on this platform");
			}
		}else {
			return serverChannelFactory;
		}
	}

	@Override
	public EventLoopGroup getBossEventLoopGroup() {
		return bossEventLoopGroup;
	}

	@Override
	public EventLoopGroup getWorkerEventLoopGroup() {
		return workerEventLoopGroup;
	}

	public void initializeConnection(InboundConnection conn, String username, UUID uuid, IPipelineData pipelineData,
			Consumer<VelocityConnection> setAttr) {
		boolean eag = pipelineData != null && pipelineData.isEaglerPlayer();
		VelocityConnection c = new VelocityConnection(this, conn, username, uuid,
				eag ? pipelineData::awaitPlayState : null);
		if(eag) {
			c.compressionDisable = true;
		}
		setAttr.accept(c);
		connectionInitializer.initializeConnection(new IPlatformConnectionInitializer<IPipelineData, Object>() {
			@Override
			public void setConnectionAttachment(Object attachment) {
				c.attachment = attachment;
			}
			@Override
			public IPipelineData getPipelineAttachment() {
				return pipelineData;
			}
			@Override
			public IPlatformConnection getConnection() {
				return c;
			}
			@Override
			public void setUniqueId(UUID uuid) {
				c.uuid = uuid;
			}
			@Override
			public void setTexturesProperty(String propertyValue, String propertySignature) {
				c.texturesPropertyValue = propertyValue;
				c.texturesPropertySignature = propertySignature;
			}
			@Override
			public void setEaglerPlayerProperty(boolean enable) {
				c.eaglerPlayerProperty = enable ? (byte) 2 : (byte) 1;
			}
		});
	}

	public void initializePlayer(Player player, VelocityConnection connection, Consumer<Boolean> onComplete) {
		VelocityPlayer p = new VelocityPlayer(player, connection);
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Object, Player>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}
			@Override
			public Object getConnectionAttachment() {
				return connection.attachment;
			}
			@Override
			public IPlatformPlayer<Player> getPlayer() {
				return p;
			}
			@Override
			public void complete() {
				playerInstanceMap.put(player, p);
				onComplete.accept(true);
			}
			@Override
			public void cancel() {
				onComplete.accept(false);
			}
		});
	}

	public void handleServerPreConnect(IPlatformPlayer<Player> player) {
		IEaglerXServerJoinListener<Player> listener = serverJoinListener;
		if(listener != null) {
			listener.handlePreConnect(player);
		}
	}

	public void handleServerPostConnect(IPlatformPlayer<Player> player, IPlatformServer<Player> server) {
		IEaglerXServerJoinListener<Player> listener = serverJoinListener;
		if(listener != null) {
			listener.handlePostConnect(player, server);
		}
	}

	public void dropPlayer(Player player) {
		VelocityPlayer p = playerInstanceMap.remove(player);
		if(p != null) {
			playerInitializer.destroyPlayer(p);
		}
	}

	public ProxyServer proxy() {
		return proxy;
	}

}
