package net.lax1dude.eaglercraft.backend.server.bungee;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.AbortLoadException;
import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
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
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformZlib;
import net.lax1dude.eaglercraft.backend.server.adapter.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineComponent.EnumPipelineComponent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.bungee.chat.BungeeComponentHelper;
import net.lax1dude.eaglercraft.backend.server.bungee.event.BungeeEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.util.FallbackJava11Zlib;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
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
	protected IEaglerXServerNettyPipelineInitializer<Object> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<Object, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, ProxiedPlayer> playerInitializer;
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
	protected Function<SocketAddress, ChannelFactory<? extends Channel>> channelFactory;
	protected Function<SocketAddress, ChannelFactory<? extends ServerChannel>> serverChannelFactory;
	protected EventLoopGroup bossEventLoopGroup;
	protected EventLoopGroup workerEventLoopGroup;
	protected BungeeNative.IBungeeNativeZlibFactory zlibFactory;

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

	private final ConcurrentMap<ProxiedPlayer, BungeePlayer> playerInstanceMap = new ConcurrentHashMap<>(1024);

	public PlatformPluginBungee() {
	}

	@Override
	public void onLoad() {
		ProxyServer proxy = getProxy();
		loggerImpl = new JavaLogger(getLogger());
		eventDispatcherImpl = new BungeeEventDispatchAdapter(proxy.getPluginManager());
		schedulerImpl = new BungeeScheduler(this, proxy.getScheduler());
		componentHelperImpl = new BungeeComponentHelper();
		cacheConsoleCommandSenderInstance = proxy.getConsole();
		cacheConsoleCommandSenderHandle = new BungeeConsole(cacheConsoleCommandSenderInstance);
		ImmutableMap.Builder<String, IPlatformServer<ProxiedPlayer>> serverMapBuilder = ImmutableMap.builder();
		for(Entry<String, ServerInfo> etr : proxy.getServers().entrySet()) {
			serverMapBuilder.put(etr.getKey(), new BungeeServer(this, etr.getValue(), true));
		}
		registeredServers = serverMapBuilder.build();
    	channelFactory = BungeeUnsafe.getChannelFactory();
    	serverChannelFactory = BungeeUnsafe.getServerChannelFactory();
    	bossEventLoopGroup = BungeeUnsafe.getBossEventLoopGroup(proxy);
    	workerEventLoopGroup = BungeeUnsafe.getWorkerEventLoopGroup(proxy);
    	zlibFactory = BungeeNative.bindFactory();
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
			public void setPipelineInitializer(IEaglerXServerNettyPipelineInitializer<?> initializer) {
				pipelineInitializer = (IEaglerXServerNettyPipelineInitializer<Object>) initializer;
			}

			@Override
			public void setConnectionInitializer(IEaglerXServerConnectionInitializer<?, ?> initializer) {
				connectionInitializer = (IEaglerXServerConnectionInitializer<Object, Object>) initializer;
			}

			@Override
			public void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, ProxiedPlayer> initializer) {
				playerInitializer = (IEaglerXServerPlayerInitializer<Object, Object, ProxiedPlayer>) initializer;
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
			aborted = true;
			throw new IllegalStateException("Startup aborted");
		}
	}

	private static final ImmutableMap<String, EnumPipelineComponent> PIPELINE_COMPONENTS_MAP = 
			ImmutableMap.<String, EnumPipelineComponent>builder()
			.put("frame-decoder", EnumPipelineComponent.FRAME_DECODER)
			.put("frame-prepender", EnumPipelineComponent.FRAME_ENCODER)
			.put("packet-encoder", EnumPipelineComponent.MINECRAFT_ENCODER)
			.put("packet-decoder", EnumPipelineComponent.MINECRAFT_DECODER)
			.put("via-encoder", EnumPipelineComponent.VIA_ENCODER)
			.put("via-decoder", EnumPipelineComponent.VIA_DECODER)
			.put("protocolize2-decoder", EnumPipelineComponent.PROTOCOLIZE_DECODER)
			.put("protocolize2-encoder", EnumPipelineComponent.PROTOCOLIZE_ENCODER)
			.put("timeout", EnumPipelineComponent.READ_TIMEOUT_HANDLER)
			.put("legacy-decoder", EnumPipelineComponent.BUNGEE_LEGACY_HANDLER)
			.put("legacy-kick", EnumPipelineComponent.BUNGEE_LEGACY_KICK_ENCODER)
			.put("handler-boss", EnumPipelineComponent.INBOUND_PACKET_HANDLER)
			.put("pe-decoder-packetevents", EnumPipelineComponent.PACKETEVENTS_DECODER)
			.put("pe-encoder-packetevents", EnumPipelineComponent.PACKETEVENTS_ENCODER)
			.put("pe-timeout-handler-packetevents", EnumPipelineComponent.PACKETEVENTS_TIMEOUT_HANDLER)
			.build();

	@Override
	public void onEnable() {
		if(aborted) {
			return;
		}
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
			onServerEnable.run();
		}
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
		}else if(obj instanceof ProxiedPlayer) {
			return getPlayer((ProxiedPlayer) obj);
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
	public void handleConnectionInitFallback(Channel channel) {
	}

	@Override
	public void handleUndoCompression(ChannelHandlerContext ctx) {
	}

	@Override
	public ChannelFactory<? extends Channel> getChannelFactory(SocketAddress address) {
		return channelFactory.apply(address);
	}

	@Override
	public ChannelFactory<? extends ServerChannel> getServerChannelFactory(SocketAddress address) {
		return serverChannelFactory.apply(address);
	}

	@Override
	public EventLoopGroup getBossEventLoopGroup() {
		return bossEventLoopGroup;
	}

	@Override
	public EventLoopGroup getWorkerEventLoopGroup() {
		return workerEventLoopGroup;
	}

	@Override
	public IPlatformZlib createNativeZlib(boolean compression, boolean decompression, int compressionLevel) {
		IPlatformZlib ret;
		if(zlibFactory != null && (ret = zlibFactory.create(compression, decompression, compressionLevel)) != null) {
			return ret;
		}
		return FallbackJava11Zlib.create(compression, decompression, compressionLevel);
	}

	public void initializeConnection(PendingConnection conn, Object pipelineData, Consumer<BungeeConnection> setAttr) {
		if((pipelineData instanceof IPipelineData) && ((IPipelineData)pipelineData).isEaglerPlayer()) {
			BungeeUnsafe.injectCompressionDisable(conn);
		}
		BungeeConnection c = new BungeeConnection(this, conn);
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
				conn.setUniqueId(uuid);
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

	public void initializePlayer(ProxiedPlayer player, BungeeConnection connection, Runnable onComplete) {
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
				onComplete.run();
			}
			@Override
			public void cancel() {
				onComplete.run();
			}
		});
	}

	public void dropPlayer(ProxiedPlayer player) {
		BungeePlayer p = playerInstanceMap.remove(player);
		if(p != null) {
			playerInitializer.destroyPlayer(p);
		}
	}

}
