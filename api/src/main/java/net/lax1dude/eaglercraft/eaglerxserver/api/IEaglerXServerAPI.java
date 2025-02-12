package net.lax1dude.eaglercraft.eaglerxserver.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.eaglerxserver.api.internal.factory.EaglerXServerAPIFactory;
import net.lax1dude.eaglercraft.eaglerxserver.api.misc.IPacketImageLoader;
import net.lax1dude.eaglercraft.eaglerxserver.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.eaglerxserver.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.eaglerxserver.api.players.IBasePlayer;
import net.lax1dude.eaglercraft.eaglerxserver.api.players.IEaglerPlayer;
import net.lax1dude.eaglercraft.eaglerxserver.api.query.IWebSocketQueryService;
import net.lax1dude.eaglercraft.eaglerxserver.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.eaglerxserver.api.skins.ISkinService;
import net.lax1dude.eaglercraft.eaglerxserver.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.eaglerxserver.api.webview.IWebViewService;

public interface IEaglerXServerAPI<PlayerObject> extends IPacketImageLoader, ISkinImageLoader {

	@SuppressWarnings("unchecked")
	public static <PlayerObject> IEaglerXServerAPI<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return (IEaglerXServerAPI<PlayerObject>) EaglerXServerAPIFactory.INSTANCE.createAPI(playerObj);
	}

	public static IEaglerXServerAPI<?> instance() {
		EaglerXServerAPIFactory factory = EaglerXServerAPIFactory.INSTANCE;
		return factory.createAPI(factory.getPlayerClass());
	}

	public static Class<?> getPlayerClass() {
		return EaglerXServerAPIFactory.INSTANCE.getPlayerClass();
	}

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

	IWebSocketQueryService<PlayerObject> getWebSocketQueryService();

}
