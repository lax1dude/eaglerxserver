package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Set;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXServerRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.IEaglerRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.voice.api.IEaglerVoiceAPI;

public interface IEaglerXServerRPC<PlayerObject> extends IEaglerVoiceAPI<PlayerObject>, IRPCAttributeHolder {

	public static <PlayerObject> IEaglerXServerRPC<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return EaglerXServerRPCFactory.INSTANCE.getAPI(playerObj);
	}

	public static IEaglerXServerRPC<?> instance() {
		return EaglerXServerRPCFactory.INSTANCE.getDefaultAPI();
	}

	public static Set<Class<?>> getPlayerTypes() {
		return EaglerXServerRPCFactory.INSTANCE.getPlayerTypes();
	}

	public static IEaglerRPCFactory getFactoryInstance() {
		return EaglerXServerRPCFactory.INSTANCE;
	}

	IEaglerRPCFactory getFactory();

	EnumPlatformType getPlatformType();

	IVoiceServiceX<PlayerObject> getVoiceService();

	ISkinImageLoader getSkinImageLoader();

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

}
