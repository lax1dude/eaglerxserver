package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.bukkit.event.BukkitEventDispatchAdapter;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlatformPluginBukkit extends JavaPlugin implements IPlatform<Player> {

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<Player, BaseComponent> eventDispatcherImpl;

	protected Runnable onServerEnable;
	protected Runnable onServerDisable;
	protected IEaglerXServerNettyPipelineInitializer<Object> pipelineInitializer;
	protected IEaglerXServerConnectionInitializer<Object, Object> connectionInitializer;
	protected IEaglerXServerPlayerInitializer<Object, Object, Player> playerInitializer;
	protected Collection<IEaglerXServerCommandType> commandsList;
	protected IEaglerXServerListener listenerConf;
	protected Collection<IEaglerXServerMessageChannel> playerChannelsList;

	private final ConcurrentMap<Player, IPlatformPlayer<Player>> playerInstanceMap = new ConcurrentHashMap<>(1024);

	public PlatformPluginBukkit() {
	}

	@Override
	public void onLoad() {
		eventDispatcherImpl = new BukkitEventDispatchAdapter(this, getServer().getPluginManager(),
				getServer().getScheduler());
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
			public void setCommandRegistry(Collection<IEaglerXServerCommandType> commands) {
				commandsList = commands;
			}

			@Override
			public void setEaglerListener(IEaglerXServerListener listener) {
				listenerConf = listener;
			}

		});
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
		if(onServerEnable != null) {
			onServerEnable.run();
		}
	}

	@Override
	public void onDisable() {
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

}
