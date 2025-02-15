package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPipelineInitializer;
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

	private final ConcurrentMap<Player, IPlatformPlayer<Player>> playerInstanceMap = new ConcurrentHashMap<>();

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
			public void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel> channels) {
				
			}

			@Override
			public void setPipelineInitializer(IEaglerXServerPipelineInitializer<?> initializer) {
				
			}

			@Override
			public void setPlayerInitializer(IEaglerXServerPlayerInitializer<?> initializer) {
				
			}

			@Override
			public void setCommandRegistry(Collection<IEaglerXServerCommandType> commands) {
				
			}

			@Override
			public IPlatform<Player> getPlatform() {
				return PlatformPluginBukkit.this;
			}

			@Override
			public void setEaglerListener(IEaglerXServerListener listener) {
				
			}

		});
	}

	@Override
	public void onEnable() {
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
