package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;

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

	IWebViewProvider<PlayerObject> getDefaultProvider();

	default IWebViewBlob createWebViewBlob(String markupIn) {
		return createWebViewBlob(markupIn.getBytes(StandardCharsets.UTF_8));
	}

	IWebViewBlob createWebViewBlob(byte[] bytesIn);

	IWebViewBlob createWebViewBlob(InputStream inputStream) throws IOException;

	default IWebViewBlob createWebViewBlob(File file) throws IOException {
		try(InputStream is = new FileInputStream(file)) {
			return createWebViewBlob(is);
		}
	}

	SHA1Sum registerGlobalBlob(IWebViewBlob blob);

	default void unregisterGlobalBlob(IWebViewBlob blob) {
		unregisterGlobalBlob(blob.getHash());
	}

	void unregisterGlobalBlob(SHA1Sum sum);

	Map<String, String> getTemplateGlobals();

	void setTemplateGlobal(String key, String value);

	void removeTemplateGlobal(String key);

	default ITemplateLoader createTemplateLoader() {
		return createTemplateLoader(null, null, null, false);
	}

	default ITemplateLoader createTemplateLoader(File baseDir) {
		return createTemplateLoader(baseDir, null, null, false);
	}

	default ITemplateLoader createTemplateLoader(File baseDir, boolean allowEvalMacro) {
		return createTemplateLoader(baseDir, null, null, allowEvalMacro);
	}

	default ITemplateLoader createTemplateLoader(File baseDir, Map<String, String> variables, boolean allowEvalMacro) {
		return createTemplateLoader(baseDir, variables, null, allowEvalMacro);
	}

	default ITemplateLoader createTemplateLoader(File baseDir, ITranslationProvider translations, boolean allowEvalMacro) {
		return createTemplateLoader(baseDir, null, translations, allowEvalMacro);
	}

	ITemplateLoader createTemplateLoader(File baseDir, Map<String, String> variables, ITranslationProvider translations,
			boolean allowEvalMacro);

}
