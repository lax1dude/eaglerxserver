package net.lax1dude.eaglercraft.backend.server.bungee;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

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
import net.lax1dude.eaglercraft.backend.server.bungee.event.BungeeEventDispatchAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IPlatform<ProxiedPlayer> {

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<ProxiedPlayer, BaseComponent> eventDispatcherImpl;

	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<Object> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<Object, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, ProxiedPlayer> playerInitializer;
	protected Collection<IEaglerXServerCommandType> commandsList;
	protected Collection<IEaglerXServerListener> listenersList;
	protected Collection<IEaglerXServerMessageChannel> playerChannelsList;
	protected Collection<IEaglerXServerMessageChannel> backendChannelsList;

	private final ConcurrentMap<ProxiedPlayer, BungeePlayer> playerInstanceMap = new ConcurrentHashMap<>(1024);

	public PlatformPluginBungee() {
	}

	@Override
	public void onLoad() {
		eventDispatcherImpl = new BungeeEventDispatchAdapter(getProxy().getPluginManager());
		IEaglerXServerImpl<ProxiedPlayer> serverImpl = new EaglerXServer<>();
		serverImpl.load(new InitProxying<ProxiedPlayer>() {

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
			public void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, ProxiedPlayer> initializer) {
				playerInitializer = (IEaglerXServerPlayerInitializer<Object, Object, ProxiedPlayer>) initializer;
			}

			@Override
			public void setCommandRegistry(Collection<IEaglerXServerCommandType> commands) {
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
			public void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel> channels) {
				backendChannelsList = channels;
			}

		});
	}

	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
		if(onServerEnable != null) {
			onServerEnable.run();
		}
	}

	@Override
	public void onDisable() {
		getProxy().getPluginManager().unregisterListeners(this);
		if(onServerDisable != null) {
			onServerDisable.run();
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
	public IEventDispatchAdapter<ProxiedPlayer, ?> eventDispatcher() {
		return eventDispatcherImpl;
	}

	@Override
	public Class<ProxiedPlayer> getPlayerClass() {
		return ProxiedPlayer.class;
	}

	public void initializeConnection(PendingConnection conn, Object pipelineData, Consumer<BungeeConnection> setAttr) {
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
		});
	}

	public void initializePlayer(ProxiedPlayer player, BungeeConnection connection) {
		BungeePlayer p = new BungeePlayer(player, connection);
		playerInitializer.initializePlayer(new IPlatformPlayerInitializer<Object, Object, ProxiedPlayer>() {
			@Override
			public void setPlayerAttachment(Object attachment) {
				p.attachment = attachment;
			}
			@Override
			public Object getConnectionAttachment() {
				return connection;
			}
			@Override
			public IPlatformPlayer<ProxiedPlayer> getPlayer() {
				return p;
			}
		});
		playerInstanceMap.put(player, p);
	}

	public void dropPlayer(ProxiedPlayer player) {
		BungeePlayer p = playerInstanceMap.remove(player);
		if(p != null) {
			playerInitializer.destroyPlayer(p);
		}
	}

}
