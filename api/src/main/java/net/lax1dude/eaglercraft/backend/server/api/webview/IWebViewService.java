package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;

public interface IWebViewService<PlayerObject> {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nullable
	default IWebViewManager<PlayerObject> getWebViewManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getWebViewManager() : null;
	}

	@Nonnull
	IPauseMenuService<PlayerObject> getPauseMenuService();

	@Nonnull
	IWebViewProvider<PlayerObject> getDefaultProvider();

	@Nonnull
	default IWebViewBlob createWebViewBlob(@Nonnull String markupIn) {
		return createWebViewBlob(markupIn.getBytes(StandardCharsets.UTF_8));
	}

	@Nonnull
	IWebViewBlob createWebViewBlob(@Nonnull byte[] bytesIn);

	@Nonnull
	IWebViewBlob createWebViewBlob(@Nonnull InputStream inputStream) throws IOException;

	@Nonnull
	default IWebViewBlob createWebViewBlob(@Nonnull File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return createWebViewBlob(is);
		}
	}

	@Nonnull
	SHA1Sum registerGlobalBlob(@Nonnull IWebViewBlob blob);

	default void unregisterGlobalBlob(@Nonnull IWebViewBlob blob) {
		unregisterGlobalBlob(blob.getHash());
	}

	void unregisterGlobalBlob(@Nonnull SHA1Sum sum);

	void registerBlobAlias(@Nonnull String name, @Nonnull SHA1Sum blob);

	void unregisterBlobAlias(@Nonnull String name);

	@Nullable
	SHA1Sum getBlobFromAlias(@Nonnull String name);

	@Nullable
	Map<String, String> getTemplateGlobals();

	void setTemplateGlobal(@Nonnull String key, @Nullable String value);

	void removeTemplateGlobal(@Nonnull String key);

	@Nonnull
	default ITemplateLoader createTemplateLoader() {
		return createTemplateLoader(null, null, null, false);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir) {
		return createTemplateLoader(baseDir, null, null, false);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir, boolean allowEvalMacro) {
		return createTemplateLoader(baseDir, null, null, allowEvalMacro);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir, @Nonnull Map<String, String> variables,
			boolean allowEvalMacro) {
		return createTemplateLoader(baseDir, variables, null, allowEvalMacro);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir, @Nonnull ITranslationProvider translations,
			boolean allowEvalMacro) {
		return createTemplateLoader(baseDir, null, translations, allowEvalMacro);
	}

	@Nonnull
	ITemplateLoader createTemplateLoader(@Nullable File baseDir, @Nullable Map<String, String> variables,
			@Nullable ITranslationProvider translations, boolean allowEvalMacro);

}
