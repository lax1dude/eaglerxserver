package net.lax1dude.eaglercraft.backend.server.bungee;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

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
import net.lax1dude.eaglercraft.backend.server.bungee.event.BungeeEventDispatchAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IPlatform<ProxiedPlayer> {

	private IPlatformLogger loggerImpl;
	private IEventDispatchAdapter<ProxiedPlayer, BaseComponent> eventDispatcherImpl;

	protected Runnable onServerEnable;
	protected Runnable onServerDisable;

	private final ConcurrentMap<ProxiedPlayer, IPlatformPlayer<ProxiedPlayer>> playerInstanceMap = new ConcurrentHashMap<>();

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
			public IPlatform<ProxiedPlayer> getPlatform() {
				return PlatformPluginBungee.this;
			}

			@Override
			public void setEaglerListeners(Collection<IEaglerXServerListener> listeners) {
				
			}

			@Override
			public void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel> channels) {
				
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

}
