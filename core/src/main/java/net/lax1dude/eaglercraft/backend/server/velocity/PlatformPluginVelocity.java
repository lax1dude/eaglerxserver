package net.lax1dude.eaglercraft.backend.server.velocity;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerNettyPipelineInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayerInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServerVersion;
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

	private final ConcurrentMap<Player, VelocityPlayer> playerInstanceMap = new ConcurrentHashMap<>(1024);

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		proxyLogger = loggerIn;
		dataDir = dataDirIn;
		dataDirFile = dataDirIn.toFile();
		eventDispatcherImpl = new VelocityEventDispatchAdapter(proxy.getEventManager());
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

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
    	proxy.getEventManager().register(this, new VelocityListener(this));
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
