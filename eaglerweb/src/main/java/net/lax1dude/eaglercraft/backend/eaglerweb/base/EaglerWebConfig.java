/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class EaglerWebConfig {

	private static final Gson GSON;

	static {
		try {
			GSON = (Gson) Class.forName("net.lax1dude.eaglercraft.backend.server.base.EaglerXServer")
					.getField("GSON_PRETTY").get(null);
		} catch (ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EaglerWebConfig loadConfig(IEaglerWebLogger logger, File pluginDir)
			throws IOException, JsonParseException {
		if (!pluginDir.isDirectory() && !(new File(pluginDir, "web")).mkdirs()) {
			throw new IOException("Could not create config directory!");
		}

		File settings = new File(pluginDir, "settings.json");
		if (!settings.isFile()) {
			logger.info("Writing default config: " + settings.getAbsolutePath());
			try (InputStream is = EaglerWebConfig.class.getResourceAsStream("default_settings.json");
					OutputStream os = new FileOutputStream(settings)) {
				ByteStreams.copy(is, os);
			}
		}

		File mimetypes = new File(pluginDir, "mimetypes.json");
		if (!mimetypes.isFile()) {
			logger.info("Writing default config: " + mimetypes.getAbsolutePath());
			try (InputStream is = EaglerWebConfig.class.getResourceAsStream("default_mimetypes.json");
					OutputStream os = new FileOutputStream(mimetypes)) {
				ByteStreams.copy(is, os);
			}
		}

		JsonObject obj;
		try (Reader reader = new InputStreamReader(new FileInputStream(settings), StandardCharsets.UTF_8)) {
			obj = GSON.fromJson(reader, JsonObject.class);
		}

		ImmutableMap.Builder<String, ConfigDataSettings> settingsBuilder = ImmutableMap.builder();
		ConfigDataSettings defaultSettings = null;

		long memoryCacheExpiresAfter = obj.getAsJsonPrimitive("memory_cache_expires_after").getAsLong() * 1000l;
		int memoryCacheMaxFiles = obj.getAsJsonPrimitive("memory_cache_max_files").getAsInt();
		int fileIOThreadCount = obj.getAsJsonPrimitive("file_io_thread_count").getAsInt();
		boolean enableCORS = obj.getAsJsonPrimitive("enable_cors_support").getAsBoolean();

		for (Entry<String, JsonElement> etr : obj.getAsJsonObject("listeners").entrySet()) {
			ConfigDataSettings setting = parseSettings(pluginDir, etr.getValue().getAsJsonObject());
			if ("*".equals(etr.getKey())) {
				defaultSettings = setting;
			} else {
				settingsBuilder.put(etr.getKey(), setting);
			}
		}

		try (Reader reader = new InputStreamReader(new FileInputStream(mimetypes), StandardCharsets.UTF_8)) {
			obj = GSON.fromJson(reader, JsonObject.class);
		}

		ImmutableMap.Builder<String, ConfigDataMIMEType> mimeBuilder = ImmutableMap.builder();

		for (Entry<String, JsonElement> etr : obj.entrySet()) {
			parseMIMEType(etr.getKey(), etr.getValue().getAsJsonObject(), mimeBuilder);
		}

		return new EaglerWebConfig(memoryCacheExpiresAfter, memoryCacheMaxFiles, fileIOThreadCount, enableCORS,
				settingsBuilder.build(), defaultSettings, mimeBuilder.build());
	}

	private static ConfigDataSettings parseSettings(File pluginDir, JsonObject object) {
		File rootFolder = new File(pluginDir, object.getAsJsonPrimitive("document_root").getAsString())
				.getAbsoluteFile();
		List<String> pageIndexNames;
		if (object.has("page_index") && object.get("page_index").isJsonArray()) {
			JsonArray arr = object.getAsJsonArray("page_index");
			ImmutableList.Builder<String> builder = ImmutableList.builder();
			for (int i = 0, l = arr.size(); i < l; ++i) {
				builder.add(arr.get(i).getAsString());
			}
			pageIndexNames = builder.build();
		} else {
			pageIndexNames = Collections.emptyList();
		}
		File page404NotFound = object.has("page_404") && object.get("page_404").isJsonPrimitive()
				? new File(rootFolder, object.getAsJsonPrimitive("page_404").getAsString()).getAbsoluteFile()
				: null;
		File page429RateLimit = object.has("page_429") && object.get("page_429").isJsonPrimitive()
				? new File(rootFolder, object.getAsJsonPrimitive("page_429").getAsString()).getAbsoluteFile()
				: null;
		File page500InternalError = object.has("page_500") && object.get("page_500").isJsonPrimitive()
				? new File(rootFolder, object.getAsJsonPrimitive("page_500").getAsString()).getAbsoluteFile()
				: null;
		boolean enableAutoIndex;
		String dateFormat;
		if (object.has("autoindex")) {
			JsonObject autoindex = object.getAsJsonObject("autoindex");
			enableAutoIndex = autoindex.has("enable") && autoindex.get("enable").isJsonPrimitive()
					&& autoindex.get("enable").getAsBoolean();
			dateFormat = autoindex.has("date_format") && autoindex.get("date_format").isJsonPrimitive()
					? autoindex.getAsJsonPrimitive("date_format").getAsString()
					: null;
		} else {
			enableAutoIndex = false;
			dateFormat = null;
		}
		return new ConfigDataSettings(rootFolder, pageIndexNames, page404NotFound, page429RateLimit,
				page500InternalError, enableAutoIndex, dateFormat);
	}

	private static void parseMIMEType(String key, JsonObject object,
			ImmutableMap.Builder<String, ConfigDataMIMEType> mimeBuilder) {
		JsonArray jsonArray = object.getAsJsonArray("files");
		long expires = 0l;
		JsonElement el = object.get("expires");
		if (el != null) {
			expires = el.getAsInt() * 1000l;
		}
		el = object.get("charset");
		String charset = null;
		if (el != null) {
			charset = el.getAsString();
		}
		String header = key;
		if (charset != null) {
			header = header + "; charset=" + charset;
		}
		String cacheHeader = "no-cache";
		if (expires > 0l) {
			cacheHeader = "max-age=" + (expires / 1000l);
		}
		ConfigDataMIMEType mimeType = new ConfigDataMIMEType(key, charset, header, cacheHeader, expires);
		for (int i = 0, l = jsonArray.size(); i < l; ++i) {
			mimeBuilder.put(jsonArray.get(i).getAsString().toLowerCase(Locale.US), mimeType);
		}
	}

	static final ConfigDataMIMEType DEFAULT_MIME = new ConfigDataMIMEType("application/octet-stream", null,
			"application/octet-stream", "no-cache", 0l);

	private final long memoryCacheExpiresAfter;
	private final int memoryCacheMaxFiles;
	private final int fileIOThreadCount;
	private final boolean enableCORS;
	private final Map<String, ConfigDataSettings> settings;
	private final ConfigDataSettings defaultSettings;
	private final Map<String, ConfigDataMIMEType> mimetypes;

	private EaglerWebConfig(long memoryCacheExpiresAfter, int memoryCacheMaxFiles, int fileIOThreadCount,
			boolean enableCORS, Map<String, ConfigDataSettings> settings, ConfigDataSettings defaultSettings,
			Map<String, ConfigDataMIMEType> mimetypes) {
		this.memoryCacheExpiresAfter = memoryCacheExpiresAfter;
		this.memoryCacheMaxFiles = memoryCacheMaxFiles;
		this.fileIOThreadCount = fileIOThreadCount;
		this.enableCORS = enableCORS;
		this.settings = settings;
		this.defaultSettings = defaultSettings;
		this.mimetypes = mimetypes;
	}

	public long getMemoryCacheExpiresAfter() {
		return memoryCacheExpiresAfter;
	}

	public int getMemoryCacheMaxFiles() {
		return memoryCacheMaxFiles;
	}

	public int getFileIOThreadCount() {
		return fileIOThreadCount;
	}

	public boolean getEnableCORS() {
		return enableCORS;
	}

	public Map<String, ConfigDataSettings> getSettings() {
		return settings;
	}

	public ConfigDataSettings getDefaultSettings() {
		return defaultSettings;
	}

	public Map<String, ConfigDataMIMEType> getMimeTypes() {
		return mimetypes;
	}

	public static class ConfigDataSettings {

		private final File rootFolder;
		private final List<String> pageIndexNames;
		private final File page404NotFound;
		private final File page429RateLimit;
		private final File page500InternalError;
		private final boolean enableAutoIndex;
		private final String dateFormat;

		protected ConfigDataSettings(File rootFolder, List<String> pageIndexNames, File page404NotFound,
				File page429RateLimit, File page500InternalError, boolean enableAutoIndex, String dateFormat) {
			this.rootFolder = rootFolder;
			this.pageIndexNames = pageIndexNames;
			this.page404NotFound = page404NotFound;
			this.page429RateLimit = page429RateLimit;
			this.page500InternalError = page500InternalError;
			this.enableAutoIndex = enableAutoIndex;
			this.dateFormat = dateFormat;
		}

		public File getRootFolder() {
			return rootFolder;
		}

		public List<String> getPageIndexNames() {
			return pageIndexNames;
		}

		public File getPage404NotFound() {
			return page404NotFound;
		}

		public File getPage429RateLimit() {
			return page429RateLimit;
		}

		public File getPage500InternalError() {
			return page500InternalError;
		}

		public boolean isEnableAutoIndex() {
			return enableAutoIndex;
		}

		public String getDateFormat() {
			return dateFormat;
		}

	}

	public static class ConfigDataMIMEType {

		private final String mimeType;
		private final String charset;
		private final String contentTypeHeader;
		private final String cacheControlHeader;
		private final long expires;

		protected ConfigDataMIMEType(String mimeType, String charset, String contentTypeHeader,
				String cacheControlHeader, long expires) {
			this.mimeType = mimeType;
			this.charset = charset;
			this.contentTypeHeader = contentTypeHeader;
			this.cacheControlHeader = cacheControlHeader;
			this.expires = expires;
		}

		public String getMimeType() {
			return mimeType;
		}

		public String getCharset() {
			return charset;
		}

		public String getContentTypeHeader() {
			return contentTypeHeader;
		}

		public String getCacheControlHeader() {
			return cacheControlHeader;
		}

		public long getExpires() {
			return expires;
		}

	}

}
