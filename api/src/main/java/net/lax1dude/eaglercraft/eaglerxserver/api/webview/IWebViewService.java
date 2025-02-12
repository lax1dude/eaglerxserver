package net.lax1dude.eaglercraft.eaglerxserver.api.webview;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.eaglerxserver.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.eaglerxserver.api.players.IEaglerPlayer;

public interface IWebViewService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default IWebViewManager<PlayerObject> getWebViewManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getWebViewManager() : null;
	}

	default IWebViewManager<PlayerObject> getWebViewManager(IEaglerPlayer<PlayerObject> player) {
		return player.getWebViewManager();
	}

	IPauseMenuService<PlayerObject> getPauseMenuService();

	IWebViewBlob createWebViewBlob(String markupIn);

	IWebViewBlob createWebViewBlob(byte[] bytesIn);

	IWebViewBlob createWebViewBlob(InputStream inputStream);

	IWebViewBlob createWebViewBlob(File file);

	Map<String, String> getTemplateGlobals();

	void addTemplateGlobal(String key, String value);

	String loadWebViewTemplate(String markupIn, Map<String, String> variables, boolean allowEvalMacro);

	default String loadWebViewTemplate(String markupIn, boolean allowEvalMacro) {
		return loadWebViewTemplate(markupIn, null, allowEvalMacro);
	}

	String loadWebViewTemplate(byte[] bytesIn, Map<String, String> variables, boolean allowEvalMacro);

	default String loadWebViewTemplate(byte[] bytesIn, boolean allowEvalMacro) {
		return loadWebViewTemplate(bytesIn, null, allowEvalMacro);
	}

	String loadWebViewTemplate(InputStream inputStream, Map<String, String> variables, boolean allowEvalMacro);

	default String loadWebViewTemplate(InputStream inputStream, boolean allowEvalMacro) {
		return loadWebViewTemplate(inputStream, null, allowEvalMacro);
	}

	String loadWebViewTemplate(File file, Map<String, String> variables, boolean allowEvalMacro);

	default String loadWebViewTemplate(File file, boolean allowEvalMacro) {
		return loadWebViewTemplate(file, null, allowEvalMacro);
	}

}
