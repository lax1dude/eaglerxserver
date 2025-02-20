package net.lax1dude.eaglercraft.backend.server.velocity;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import net.kyori.adventure.text.Component;
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
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServerVersion;
import net.lax1dude.eaglercraft.backend.server.base.ListenerInitList;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.velocity.chat.VelocityComponentHelper;
import net.lax1dude.eaglercraft.backend.server.velocity.event.VelocityEventDispatchAdapter;

public class PlatformPluginVelocity implements IPlatform<Player> {

	public static final String PLUGIN_ID = EaglerXServerVersion.VELOCITY_PLUGIN_ID;
	public static final String PLUGIN_NAME = EaglerXServerVersion.BRAND;
	public static final String PLUGIN_AUTHOR = EaglerXServerVersion.AUTHOR;
	public static final String PLUGIN_VERSION = EaglerXServerVersion.VERSION;

	private ProxyServer proxy;
	private Logger proxyLogger;
	private Path dataDir;
	private File dataDirFile;
	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player, Component> eventDispatcherImpl;

	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<Object> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<Object, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, Player> playerInitializer;
	protected Collection<IEaglerXServerCommandType> commandsList;
	protected Collection<IEaglerXServerListener> listenersList;
	protected Collection<IEaglerXServerMessageChannel> playerChannelsList;
	protected Collection<IEaglerXServerMessageChannel> backendChannelsList;
	protected IPlatformScheduler schedulerImpl;
	protected IPlatformComponentHelper componentHelperImpl;

	private final ConcurrentMap<Player, VelocityPlayer> playerInstanceMap = new ConcurrentHashMap<>(1024);

	protected ListenerInitList listenersToInit = null;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		proxyLogger = loggerIn;
		dataDir = dataDirIn;
		dataDirFile = dataDirIn.toFile();
		eventDispatcherImpl = new VelocityEventDispatchAdapter(proxy.getEventManager());
    	schedulerImpl = new VelocityScheduler(this, proxyIn.getScheduler());
    	componentHelperImpl = new VelocityComponentHelper(this);
		IEaglerXServerImpl<Player> serverImpl = new EaglerXServer<>();
		serverImpl.load(new InitProxying<Player>() {

			@Override
			public void setOnServerEnable(Runnable enable) {
				onServerEnable = enable;
			}

			@Override
			public void setOnServerDisable(Runnable disable) {
				onServerEnable = disable;
			}

			@Override
			public void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel> channels) {
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
			public void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, Player> initializer) {
				playerInitializer = (IEaglerXServerPlayerInitializer<Object, Object, Player>) initializer;
			}

			@Override
			public void setCommandRegistry(Collection<IEaglerXServerCommandType> commands) {
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
			public void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel> channels) {
				backendChannelsList = channels;
			}

		});
	}

	private static final ImmutableMap<String, EnumPipelineComponent> PIPELINE_COMPONENTS_MAP = 
			ImmutableMap.<String, EnumPipelineComponent>builder()
			.put("frame-decoder", EnumPipelineComponent.FRAME_DECODER)
			.put("frame-encoder", EnumPipelineComponent.FRAME_ENCODER)
			.put("minecraft-encoder", EnumPipelineComponent.MINECRAFT_ENCODER)
			.put("minecraft-decoder", EnumPipelineComponent.MINECRAFT_DECODER)
			.put("via-encoder", EnumPipelineComponent.VIA_ENCODER)
			.put("via-decoder", EnumPipelineComponent.VIA_DECODER)
			.put("protocolize2-decoder", EnumPipelineComponent.PROTOCOLIZE_DECODER)
			.put("protocolize2-encoder", EnumPipelineComponent.PROTOCOLIZE_ENCODER)
			.put("read-timeout", EnumPipelineComponent.READ_TIMEOUT_HANDLER)
			.put("legacy-ping-encoder", EnumPipelineComponent.VELOCITY_LEGACY_PING_ENCODER)
			.build();

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
    	proxy.getEventManager().register(this, new VelocityListener(this));
    	listenersToInit = new ListenerInitList(listenersList);
    	VelocityUnsafe.injectChannelInitializer(proxy, (listenerConf, channel) -> {
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

    	});
		if(onServerEnable != null) {
			onServerEnable.run();
		}
	}

	@Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
		proxy.getEventManager().unregisterListeners(this);
		if(onServerDisable != null) {
			onServerDisable.run();
		}
	}

	@Override
	public EnumAdapterPlatformType getType() {
		return EnumAdapterPlatformType.VELOCITY;
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

	public void initializeConnection(InboundConnection conn, String username, UUID uuid, Object pipelineData,
			Consumer<VelocityConnection> setAttr) {
		VelocityConnection c = new VelocityConnection(this, conn, username, uuid);
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

	public void initializePlayer(Player player, VelocityConnection connection) {
		VelocityPlayer p = new VelocityPlayer(player, connection);
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Object, Player>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}
			@Override
			public Object getConnectionAttachment() {
				return connection;
			}
			@Override
			public IPlatformPlayer<Player> getPlayer() {
				return p;
			}
		});
		playerInstanceMap.put(player, p);
		p.confirmTask = proxy.getScheduler().buildTask(this, () -> {
			p.confirmTask = null;
			proxyLogger.warn("Player {} was initialized, but never fired PostLoginEvent, dropping...", p.getUsername());
			dropPlayer(player);
		}).delay(5l, TimeUnit.SECONDS).schedule();
	}

	public void confirmPlayer(Player player) {
		VelocityPlayer p = playerInstanceMap.get(player);
		if(p != null) {
			ScheduledTask conf = p.confirmTask;
			if(conf != null) {
				p.confirmTask = null;
				conf.cancel();
			}
		}
	}

	public void dropPlayer(Player player) {
		VelocityPlayer p = playerInstanceMap.remove(player);
		if(p != null) {
			ScheduledTask conf = p.confirmTask;
			if(conf != null) {
				p.confirmTask = null;
				conf.cancel();
			}
			playerInitializer.destroyPlayer(p);
		}
	}

	public ProxyServer proxy() {
		return proxy;
	}

}
