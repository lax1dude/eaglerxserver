package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXBackendRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.IEaglerRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public interface IEaglerXBackendRPC<PlayerObject> extends IRPCAttributeHolder {

	@Nonnull
	public static <PlayerObject> IEaglerXBackendRPC<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return EaglerXBackendRPCFactory.INSTANCE.getAPI(playerObj);
	}

	@Nonnull
	public static IEaglerXBackendRPC<?> instance() {
		return EaglerXBackendRPCFactory.INSTANCE.getDefaultAPI();
	}

	@Nonnull
	public static Set<Class<?>> getPlayerTypes() {
		return EaglerXBackendRPCFactory.INSTANCE.getPlayerTypes();
	}

	@Nonnull
	public static IEaglerRPCFactory getFactoryInstance() {
		return EaglerXBackendRPCFactory.INSTANCE;
	}

	@Nonnull
	IEaglerRPCFactory getFactory();

	@Nonnull
	EnumPlatformType getPlatformType();

	@Nonnull
	Class<PlayerObject> getPlayerClass();

	@Nonnull
	IVoiceService<PlayerObject> getVoiceService();

	@Nonnull
	ISkinImageLoader getSkinImageLoader(boolean cacheEnabled);

	@Nonnull
	IPacketImageLoader getPacketImageLoader();

	@Nonnull
	Set<Class<?>> getComponentTypes();

	@Nonnull
	IPauseMenuBuilder createPauseMenuBuilder();

	@Nonnull
	<ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(
			@Nonnull Class<ComponentType> componentType);

	@Nullable
	IBasePlayer<PlayerObject> getBasePlayer(@Nonnull PlayerObject player);

	@Nullable
	IBasePlayer<PlayerObject> getBasePlayerByName(@Nonnull String playerName);

	@Nullable
	IBasePlayer<PlayerObject> getBasePlayerByUUID(@Nonnull UUID playerUUID);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayer(@Nonnull PlayerObject player);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayerByName(@Nonnull String playerName);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(@Nonnull UUID playerUUID);

	boolean isEaglerPlayer(@Nonnull PlayerObject player);

	boolean isEaglerPlayerByName(@Nonnull String playerName);

	boolean isEaglerPlayerByUUID(@Nonnull UUID playerUUID);

	@Nonnull
	Collection<IBasePlayer<PlayerObject>> getAllPlayers();

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers();

	@Nonnull
	IScheduler getScheduler();

	void setBaseRequestTimeout(int seconds);

	int getBaseRequestTimeout();

	@Nonnull
	UUID intern(@Nonnull UUID uuid);

	boolean isLocal();

}
