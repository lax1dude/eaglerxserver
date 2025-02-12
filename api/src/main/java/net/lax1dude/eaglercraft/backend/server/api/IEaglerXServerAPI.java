package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.IEaglerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryService;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IEaglerXServerAPI<PlayerObject> extends IAttributeHolder, IBrandResolver {

	public static <PlayerObject> IEaglerXServerAPI<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return EaglerXServerAPIFactory.INSTANCE.createAPI(playerObj);
	}

	public static IEaglerXServerAPI<?> instance() {
		EaglerXServerAPIFactory factory = EaglerXServerAPIFactory.INSTANCE;
		return factory.createAPI(factory.getPlayerClass());
	}

	public static Class<?> getPlayerClass() {
		return EaglerXServerAPIFactory.INSTANCE.getPlayerClass();
	}

	public static IEaglerAPIFactory getFactoryInstance() {
		return EaglerXServerAPIFactory.INSTANCE;
	}

	IEaglerAPIFactory getFactory();

	EnumPlatformType getPlatform();

	Class<?> getEaglerXServerClass();

	<ServerImpl> ServerImpl getEaglerXServerInstance(Class<ServerImpl> clazz);

	Class<?> getPlatformPluginClass();

	<PluginImpl> PluginImpl getPlatformPluginInstance(Class<PluginImpl> clazz);

	IBasePlayer<PlayerObject> getPlayer(PlayerObject player);

	IBasePlayer<PlayerObject> getPlayerByName(String playerName);

	IBasePlayer<PlayerObject> getPlayerByUUID(UUID playerUUID);

	IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player);

	IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName);

	IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID);

	boolean isEaglerPlayer(PlayerObject player);

	boolean isEaglerPlayerByName(String playerName);

	boolean isEaglerPlayerByUUID(UUID playerUUID);

	ISkinService<PlayerObject> getSkinService();

	IVoiceService<PlayerObject> getVoiceService();

	INotificationService<PlayerObject> getNotificationService();

	IPauseMenuService<PlayerObject> getPauseMenuService();

	IWebViewService<PlayerObject> getWebViewService();

	IQueryService<PlayerObject> getQueryService();

	ISupervisorService<PlayerObject> getSupervisorService();

	IAttributeManager<PlayerObject> getAttributeManager();

}
