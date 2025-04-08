package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXBackendRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.IEaglerRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public interface IEaglerXBackendRPC<PlayerObject> extends IRPCAttributeHolder {

	public static <PlayerObject> IEaglerXBackendRPC<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return EaglerXBackendRPCFactory.INSTANCE.getAPI(playerObj);
	}

	public static IEaglerXBackendRPC<?> instance() {
		return EaglerXBackendRPCFactory.INSTANCE.getDefaultAPI();
	}

	public static Set<Class<?>> getPlayerTypes() {
		return EaglerXBackendRPCFactory.INSTANCE.getPlayerTypes();
	}

	public static IEaglerRPCFactory getFactoryInstance() {
		return EaglerXBackendRPCFactory.INSTANCE;
	}

	IEaglerRPCFactory getFactory();

	EnumPlatformType getPlatformType();

	Class<PlayerObject> getPlayerClass();

	IVoiceService<PlayerObject> getVoiceService();

	ISkinImageLoader getSkinImageLoader(boolean cacheEnabled);

	IPacketImageLoader getPacketImageLoader();

	Set<Class<?>> getComponentTypes();

	IPauseMenuBuilder createPauseMenuBuilder();

	<ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(Class<ComponentType> componentType);

	IBasePlayer<PlayerObject> getBasePlayer(PlayerObject player);

	IBasePlayer<PlayerObject> getBasePlayerByName(String playerName);

	IBasePlayer<PlayerObject> getBasePlayerByUUID(UUID playerUUID);

	IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player);

	IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName);

	IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID);

	boolean isEaglerPlayer(PlayerObject player);

	boolean isEaglerPlayerByName(String playerName);

	boolean isEaglerPlayerByUUID(UUID playerUUID);

	Collection<IBasePlayer<PlayerObject>> getAllPlayers();

	Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers();

	IScheduler getScheduler();

	void setBaseRequestTimeout(int seconds);

	int getBaseRequestTimeout();

	UUID intern(UUID uuid);

	boolean isLocal();

}
