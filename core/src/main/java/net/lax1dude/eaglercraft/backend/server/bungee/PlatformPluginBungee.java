package net.lax1dude.eaglercraft.backend.server.bungee;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.AbortLoadException;
import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerJoinListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerCountHandler;
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
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.bungee.chat.BungeeComponentHelper;
import net.lax1dude.eaglercraft.backend.server.bungee.event.BungeeEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class PlatformPluginBungee extends Plugin implements IPlatform<ProxiedPlayer> {

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<ProxiedPlayer, BaseComponent> eventDispatcherImpl;

	protected boolean aborted = false;
	protected Runnable cleanupListeners;
	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<IPipelineData> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<IPipelineData, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, ProxiedPlayer> playerInitializer;
	protected IEaglerXServerJoinListener<ProxiedPlayer> serverJoinListener;
	protected Collection<IEaglerXServerCommandType<ProxiedPlayer>> commandsList;
	protected Collection<IEaglerXServerListener> listenersList;
	protected Collection<IEaglerXServerMessageChannel<ProxiedPlayer>> playerChannelsList;
	protected Collection<IEaglerXServerMessageChannel<ProxiedPlayer>> backendChannelsList;
	protected IPlatformScheduler schedulerImpl;
	protected IPlatformComponentHelper componentHelperImpl;
	protected CommandSender cacheConsoleCommandSenderInstance;
	protected IPlatformCommandSender<ProxiedPlayer> cacheConsoleCommandSenderHandle;
	protected Map<String, IPlatformServer<ProxiedPlayer>> registeredServers;
	protected Map<String, PluginMessageHandler> registeredChannelsMap;
	protected IEaglerXServerPlayerCountHandler playerCountHandler;
	protected Function<SocketAddress, Class<? extends Channel>> channelFactory;
	protected Function<SocketAddress, Class<? extends ServerChannel>> serverChannelFactory;
	protected EventLoopGroup bossEventLoopGroup;
	protected EventLoopGroup workerEventLoopGroup;

	public class PluginMessageHandler {

		public final boolean backend;
		public final IEaglerXServerMessageChannel<ProxiedPlayer> channel;
		public final IEaglerXServerMessageHandler<ProxiedPlayer> handler;

		protected PluginMessageHandler(boolean backend, IEaglerXServerMessageChannel<ProxiedPlayer> channel,
				IEaglerXServerMessageHandler<ProxiedPlayer> handler) {
			this.backend = backend;
			this.channel = channel;
			this.handler = handler;
		}

	}

	private final ConcurrentMap<ProxiedPlayer, BungeePlayer> playerInstanceMap = (new MapMaker()).initialCapacity(1024)
			.concurrencyLevel(16).makeMap();

	public PlatformPluginBungee() {
	}

	@Override
	public void onLoad() {
		aborted = true; // Will set to false if onLoad completes normally
		ProxyServer proxy = getProxy();
		loggerImpl = new JavaLogger(getLogger());
		eventDispatcherImpl = new BungeeEventDispatchAdapter(proxy.getPluginManager());
		schedulerImpl = new BungeeScheduler(this, proxy.getScheduler());
		TextComponent alreadyConnected = new TextComponent(proxy.getTranslation("already_connected_proxy"));
		alreadyConnected.setColor(ChatColor.RED);
		componentHelperImpl = new BungeeComponentHelper(alreadyConnected);
		cacheConsoleCommandSenderInstance = proxy.getConsole();
		cacheConsoleCommandSenderHandle = new BungeeConsole(cacheConsoleCommandSenderInstance);
		registeredServers = Collections.emptyMap();
    	channelFactory = BungeeUnsafe.getChannelFactory();
    	serverChannelFactory = BungeeUnsafe.getServerChannelFactory();
    	bossEventLoopGroup = BungeeUnsafe.getBossEventLoopGroup(proxy);
    	workerEventLoopGroup = BungeeUnsafe.getWorkerEventLoopGroup(proxy);
		Init<ProxiedPlayer> init = new InitProxying<ProxiedPlayer>() {

			@Override
			public void setOnServerEnable(Runnable enable) {
				onServerEnable = enable;
			}

			@Override
			public void setOnServerDisable(Runnable disable) {
				onServerDisable = disable;
			}

			@Override
			public void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel<ProxiedPlayer>> channels) {
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
			public void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, ProxiedPlayer> initializer) {
				playerInitializer = (IEaglerXServerPlayerInitializer<Object, Object, ProxiedPlayer>) initializer;
			}

			@Override
			public void setServerJoinListener(IEaglerXServerJoinListener<ProxiedPlayer> listener) {
				serverJoinListener = listener;
			}

			@Override
			public void setCommandRegistry(Collection<IEaglerXServerCommandType<ProxiedPlayer>> commands) {
				commandsList = commands;
			}

			@Override
			public IPlatform<ProxiedPlayer> getPlatform() {
				return PlatformPluginBungee.this;
			}

			@Override
			public void setEaglerListeners(Collection<IEaglerXServerListener> listeners) {
				listenersList = listeners;
			}

			@Override
			public void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel<ProxiedPlayer>> channels) {
				backendChannelsList = channels;
			}

		};
		try {
			((IEaglerXServerImpl<ProxiedPlayer>) new EaglerXServer<ProxiedPlayer>()).load(init);
		}catch(AbortLoadException ex) {
			logger().error("Server startup aborted: " + ex.getMessage());
			Throwable t = ex.getCause();
			if(t != null) {
				logger().error("Caused by: ", t);
			}
			throw new IllegalStateException("Startup aborted");
		}

		aborted = false;
	}

	private static final ImmutableMap<String, EnumPipelineComponent> PIPELINE_COMPONENTS_MAP = 
			ImmutableMap.<String, EnumPipelineComponent>builder()
			.put("frame-decoder", EnumPipelineComponent.FRAME_DECODER)
			.put("frame-prepender", EnumPipelineComponent.FRAME_ENCODER)
			.put("frame-prepender-compress", EnumPipelineComponent.FRAME_ENCODER)
			.put("packet-encoder", EnumPipelineComponent.MINECRAFT_ENCODER)
			.put("packet-decoder", EnumPipelineComponent.MINECRAFT_DECODER)
			.put("via-encoder", EnumPipelineComponent.VIA_ENCODER)
			.put("via-decoder", EnumPipelineComponent.VIA_DECODER)
			.put("protocolize2-decoder", EnumPipelineComponent.PROTOCOLIZE_DECODER)
			.put("protocolize2-encoder", EnumPipelineComponent.PROTOCOLIZE_ENCODER)
			.put("timeout", EnumPipelineComponent.READ_TIMEOUT_HANDLER)
			.put("legacy-decoder", EnumPipelineComponent.BUNGEE_LEGACY_HANDLER)
			.put("legacy-kick", EnumPipelineComponent.BUNGEE_LEGACY_KICK_ENCODER)
			.put("inbound-boss", EnumPipelineComponent.INBOUND_PACKET_HANDLER)
			.put("pe-decoder-packetevents", EnumPipelineComponent.PACKETEVENTS_DECODER)
			.put("pe-encoder-packetevents", EnumPipelineComponent.PACKETEVENTS_ENCODER)
			.put("pe-timeout-handler-packetevents", EnumPipelineComponent.PACKETEVENTS_TIMEOUT_HANDLER)
			.build();

	@Override
	public void onEnable() {
		if(aborted) {
			return;
		}
		aborted = true; // Will set to false if onEnable returns normally
		ImmutableMap.Builder<String, IPlatformServer<ProxiedPlayer>> serverMapBuilder = ImmutableMap.builder();
		for(Entry<String, ServerInfo> etr : getProxy().getServers().entrySet()) {
			serverMapBuilder.put(etr.getKey(), new BungeeServer(this, etr.getValue(), true));
		}
		registeredServers = serverMapBuilder.build();
		ProxyServer bungee = getProxy();
		PluginManager mgr = bungee.getPluginManager();
		mgr.registerListener(this, new BungeeListener(this));
		for(IEaglerXServerCommandType<ProxiedPlayer> cmd : commandsList) {
			mgr.registerCommand(this, new BungeeCommand(this, cmd));
		}
		ImmutableMap.Builder<String, PluginMessageHandler> builder = ImmutableMap.builder();
		for(IEaglerXServerMessageChannel<ProxiedPlayer> channel : playerChannelsList) {
			PluginMessageHandler handler = new PluginMessageHandler(false, channel, channel.getHandler());
			builder.put(channel.getLegacyName(), handler);
			builder.put(channel.getModernName(), handler);
		}
		for(IEaglerXServerMessageChannel<ProxiedPlayer> channel : backendChannelsList) {
			PluginMessageHandler handler = new PluginMessageHandler(true, channel, channel.getHandler());
			builder.put(channel.getLegacyName(), handler);
			builder.put(channel.getModernName(), handler);
		}
		registeredChannelsMap = builder.build();
		for(String channel : registeredChannelsMap.keySet()) {
			bungee.registerChannel(channel);
		}
		cleanupListeners = BungeeUnsafe.injectChannelInitializer(getProxy(), (listener, channel) -> {
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
					return listener;
				}
				@Override
				public Consumer<SocketAddress> realAddressHandle() {
					return (addr) -> {
						Object o = channel.pipeline().get("handler-boss");
						if(o != null) {
							BungeeUnsafe.updateRealAddress(o, addr);
						}
					};
				}
				@Override
				public Channel getChannel() {
					return channel;
				}
			});
			
		}, listenersList);

		if(onServerEnable != null) {
			try {
				onServerEnable.run();
			}catch(AbortLoadException ex) {
				logger().error("Server startup aborted: " + ex.getMessage());
				Throwable t = ex.getCause();
				if(t != null) {
					logger().error("Caused by: ", t);
				}
				onDisable();
				throw new IllegalStateException("Startup aborted");
			}
		}

		aborted = false;
	}

	@Override
	public void onDisable() {
		if(aborted) {
			return;
		}
		if(onServerDisable != null) {
			onServerDisable.run();
		}
		if(cleanupListeners != null) {
			cleanupListeners.run();
			cleanupListeners = null;
		}
		ProxyServer bungee = getProxy();
		PluginManager mgr = bungee.getPluginManager();
		mgr.unregisterListeners(this);
		mgr.unregisterCommands(this);
		for(String channel : registeredChannelsMap.keySet()) {
			bungee.unregisterChannel(channel);
		}
	}

	@Override
	public EnumAdapterPlatformType getType() {
		return EnumAdapterPlatformType.BUNGEE;
	}

	@Override
	public String getVersion() {
		return getProxy().getVersion();
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
	public IPlatformCommandSender<ProxiedPlayer> getConsole() {
		return cacheConsoleCommandSenderHandle;
	}

	IPlatformCommandSender<ProxiedPlayer> getCommandSender(CommandSender obj) {
		if(obj == null) {
			return null;
		}else if(obj instanceof ProxiedPlayer player) {
			return getPlayer(player);
		}else if(obj == cacheConsoleCommandSenderInstance) {
			return cacheConsoleCommandSenderHandle;
		}else {
			return new BungeeConsole(obj);
		}
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<ProxiedPlayer>> playerCallback) {
		playerInstanceMap.values().forEach(playerCallback);
	}

	@Override
	public Collection<IPlatformPlayer<ProxiedPlayer>> getAllPlayers() {
		return ImmutableList.copyOf(playerInstanceMap.values());
	}

	@Override
	public IPlatformPlayer<ProxiedPlayer> getPlayer(ProxiedPlayer playerObj) {
		return playerInstanceMap.get(playerObj);
	}

	@Override
	public IPlatformPlayer<ProxiedPlayer> getPlayer(String username) {
		ProxiedPlayer player = getProxy().getPlayer(username);
		if(player != null) {
			return playerInstanceMap.get(player);
		}else {
			return null;
		}
	}

	@Override
	public IPlatformPlayer<ProxiedPlayer> getPlayer(UUID uuid) {
		ProxiedPlayer player = getProxy().getPlayer(uuid);
		if(player != null) {
			return playerInstanceMap.get(player);
		}else {
			return null;
		}
	}

	@Override
	public Map<String, IPlatformServer<ProxiedPlayer>> getRegisteredServers() {
		return registeredServers;
	}

	@Override
	public IPlatformServer<ProxiedPlayer> getServer(String serverName) {
		IPlatformServer<ProxiedPlayer> ret = registeredServers.get(serverName);
		if(ret != null) {
			return ret;
		}else {
			Map<String, ServerInfo> servers = getProxy().getServers();
			if(servers != null) {
				ServerInfo info = servers.get(serverName);
				if(info != null) {
					return new BungeeServer(this, info, false);
				}
			}
			return null;
		}
	}

	@Override
	public IEventDispatchAdapter<ProxiedPlayer, ?> eventDispatcher() {
		return eventDispatcherImpl;
	}

	@Override
	public Class<ProxiedPlayer> getPlayerClass() {
		return ProxiedPlayer.class;
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
		return BungeeUnsafe.isOnlineMode(getProxy());
	}

	@Override
	public boolean isModernPluginChannelNamesOnly() {
		return false;
	}

	@Override
	public int getPlayerTotal() {
		return getProxy().getOnlineCount();
	}

	@Override
	public int getPlayerMax() {
		return BungeeUnsafe.getPlayerMax(getProxy());
	}

	@Override
	public void setPlayerCountHandler(IEaglerXServerPlayerCountHandler playerCountHandler) {
		this.playerCountHandler = playerCountHandler;
	}

	@Override
	public Bootstrap setChannelFactory(Bootstrap bootstrap, SocketAddress address) {
		return bootstrap.channel(channelFactory.apply(address));
	}

	@Override
	public ServerBootstrap setServerChannelFactory(ServerBootstrap bootstrap, SocketAddress address) {
		return bootstrap.channel(serverChannelFactory.apply(address));
	}

	@Override
	public EventLoopGroup getBossEventLoopGroup() {
		return bossEventLoopGroup;
	}

	@Override
	public EventLoopGroup getWorkerEventLoopGroup() {
		return workerEventLoopGroup;
	}

	public void initializeConnection(PendingConnection conn, IPipelineData pipelineData,
			Consumer<BungeeConnection> setAttr) {
		boolean eag = pipelineData != null && pipelineData.isEaglerPlayer();
		if(eag) {
			BungeeUnsafe.injectCompressionDisable(conn);
		}
		BungeeConnection c = new BungeeConnection(this, conn,
				eag ? pipelineData::awaitPlayState : null);
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
				conn.setUniqueId(uuid);
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

	public void initializePlayer(ProxiedPlayer player, BungeeConnection connection, Consumer<Boolean> onComplete) {
		BungeePlayer p = new BungeePlayer(player, connection);
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Object, ProxiedPlayer>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}
			@Override
			public Object getConnectionAttachment() {
				return connection.attachment;
			}
			@Override
			public IPlatformPlayer<ProxiedPlayer> getPlayer() {
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

	public void handleServerPreConnect(IPlatformPlayer<ProxiedPlayer> player) {
		IEaglerXServerJoinListener<ProxiedPlayer> listener = serverJoinListener;
		if(listener != null) {
			listener.handlePreConnect(player);
		}
	}

	public void handleServerPostConnect(IPlatformPlayer<ProxiedPlayer> player, IPlatformServer<ProxiedPlayer> server) {
		IEaglerXServerJoinListener<ProxiedPlayer> listener = serverJoinListener;
		if(listener != null) {
			listener.handlePostConnect(player, server);
		}
	}

	public void dropPlayer(ProxiedPlayer player) {
		BungeePlayer p = playerInstanceMap.remove(player);
		if(p != null) {
			playerInitializer.destroyPlayer(p);
		}
	}

}
