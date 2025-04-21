package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
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
	IWebViewBlobBuilder<OutputStream> createWebViewBlobBuilderStream();

	@Nonnull
	IWebViewBlobBuilder<Writer> createWebViewBlobBuilderWriter();

	@Nonnull
	default IWebViewBlob createWebViewBlob(@Nonnull CharSequence markupIn) {
		if(markupIn == null) {
			throw new NullPointerException("markupIn");
		}
		IWebViewBlobBuilder<Writer> builder = createWebViewBlobBuilderWriter();
		try(Writer os = builder.stream()) {
			os.append(markupIn);
		}catch(IOException ex) {
			throw new RuntimeException("Unexpected IOException thrown", ex);
		}
		return builder.build();
	}

	@Nonnull
	default IWebViewBlob createWebViewBlob(@Nonnull byte[] bytesIn) {
		if(bytesIn == null) {
			throw new NullPointerException("bytesIn");
		}
		IWebViewBlobBuilder<OutputStream> builder = createWebViewBlobBuilderStream();
		try(OutputStream os = builder.stream()) {
			os.write(bytesIn);
		}catch(IOException ex) {
			throw new RuntimeException("Unexpected IOException thrown", ex);
		}
		return builder.build();
	}

	@Nonnull
	default IWebViewBlob createWebViewBlob(@Nonnull InputStream inputStream) throws IOException {
		if(inputStream == null) {
			throw new NullPointerException("inputStream");
		}
		IWebViewBlobBuilder<OutputStream> builder = createWebViewBlobBuilderStream();
		try(OutputStream os = builder.stream()) {
			inputStream.transferTo(os);
		}
		return builder.build();
	}

	@Nonnull
	default IWebViewBlob createWebViewBlob(@Nonnull File file) throws IOException {
		if(file == null) {
			throw new NullPointerException("file");
		}
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
		if(baseDir == null) throw new NullPointerException("baseDir");
		return createTemplateLoader(baseDir, null, null, false);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir, boolean allowEvalMacro) {
		if(baseDir == null) throw new NullPointerException("baseDir");
		return createTemplateLoader(baseDir, null, null, allowEvalMacro);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir, @Nonnull Map<String, String> variables,
			boolean allowEvalMacro) {
		if(baseDir == null) throw new NullPointerException("baseDir");
		if(variables == null) throw new NullPointerException("variables");
		return createTemplateLoader(baseDir, variables, null, allowEvalMacro);
	}

	@Nonnull
	default ITemplateLoader createTemplateLoader(@Nonnull File baseDir, @Nonnull ITranslationProvider translations,
			boolean allowEvalMacro) {
		if(baseDir == null) throw new NullPointerException("baseDir");
		if(translations == null) throw new NullPointerException("translations");
		return createTemplateLoader(baseDir, null, translations, allowEvalMacro);
	}

	@Nonnull
	ITemplateLoader createTemplateLoader(@Nullable File baseDir, @Nullable Map<String, String> variables,
			@Nullable ITranslationProvider translations, boolean allowEvalMacro);

}
