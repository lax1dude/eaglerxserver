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

package net.lax1dude.eaglercraft.backend.supervisor.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.supervisor.netty.PipelineFactory;
import net.lax1dude.eaglercraft.backend.supervisor.status.AuthUtil;

public class EaglerXSupervisorConfig {

	private SocketAddress listenAddress = null;
	private String secretKey = null;
	private int readTimeout = 30000;
	private boolean enableStatus = true;
	private SocketAddress listenStatusAddress = null;
	private String statusUsername = null;
	private String statusPassword = null;
	private String authString = null;
	private boolean downloadVanillaSkins = true;
	private Set<String> allowedSkinDownloadOrigins = null;
	private String skinCacheDBURI = null;
	private boolean skinCacheDBSQLiteCompatible = false;
	private int skinCacheThreadPoolSize = -1;
	private int databaseKeepObjectsDays = 45;
	private int databaseMaxObjects = 32768;
	private int databaseCompressionLevel = 6;
	private int memoryCacheKeepObjectsSeconds = 900;
	private int memoryCacheMaxObjects = 32768;
	private String sqlDriverClass = "internal";
	private String sqlDriverPath = "internal";

	public void load(File conf) throws IOException {
		if (!conf.isFile()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					EaglerXSupervisorConfig.class.getResourceAsStream("default_supervisor_config.properties"),
					StandardCharsets.UTF_8))) {
				try (PrintWriter w = new PrintWriter(
						new OutputStreamWriter(new FileOutputStream(conf), StandardCharsets.UTF_8))) {
					String str;
					while ((str = br.readLine()) != null) {
						w.println(str);
					}
				}
			}
		}

		Properties props = new Properties();
		try (Reader is = new InputStreamReader(new FileInputStream(conf), StandardCharsets.UTF_8)) {
			props.load(is);
		}

		listenAddress = PipelineFactory.getAddr(getRequiredString(props, "listen-addr"));
		secretKey = getStringOrNull(props, "secret-key");
		readTimeout = getRequiredInt(props, "read-timeout");
		enableStatus = "true".equalsIgnoreCase(getStringOrNull(props, "status-http-enable"));
		if (enableStatus) {
			listenStatusAddress = PipelineFactory.getAddr(getRequiredString(props, "status-http-listen-addr"));
			statusUsername = getStringOrNull(props, "status-http-username");
			statusPassword = getStringOrNull(props, "status-http-password");
			if (statusUsername != null && statusPassword != null) {
				authString = AuthUtil.createBasic(statusUsername, statusPassword);
			} else {
				authString = null;
			}
		} else {
			listenStatusAddress = null;
			statusUsername = null;
			statusPassword = null;
			authString = null;
		}
		downloadVanillaSkins = "true".equalsIgnoreCase(getStringOrNull(props, "download-vanilla-skins"));
		if (downloadVanillaSkins) {
			allowedSkinDownloadOrigins = new HashSet<>(
					Arrays.asList(getRequiredString(props, "valid-skin-download-origins").split("\\s*[;,]\\s*")));
			skinCacheDBURI = getRequiredString(props, "skin-cache-db-uri");
			skinCacheDBSQLiteCompatible = Boolean
					.parseBoolean(getRequiredString(props, "skin-cache-db-sqlite-compatible"));
			skinCacheThreadPoolSize = getRequiredInt(props, "skin-cache-thread-pool-size");
			databaseKeepObjectsDays = getRequiredInt(props, "database-keep-objects-days");
			databaseMaxObjects = getRequiredInt(props, "database-max-objects");
			databaseCompressionLevel = getRequiredInt(props, "database-compression-level");
			memoryCacheKeepObjectsSeconds = getRequiredInt(props, "memory-cache-keep-objects-seconds");
			memoryCacheMaxObjects = getRequiredInt(props, "memory-cache-max-objects");
			sqlDriverClass = getRequiredString(props, "sql-driver-class");
			sqlDriverPath = getRequiredString(props, "sql-driver-path");
		} else {
			allowedSkinDownloadOrigins = null;
			skinCacheDBURI = null;
			skinCacheDBSQLiteCompatible = false;
			skinCacheThreadPoolSize = -1;
			databaseKeepObjectsDays = 45;
			databaseMaxObjects = 32768;
			databaseCompressionLevel = 6;
			memoryCacheKeepObjectsSeconds = 900;
			memoryCacheMaxObjects = 32768;
			sqlDriverClass = "internal";
			sqlDriverPath = "internal";
		}
	}

	private static int getRequiredInt(Properties props, String name) throws IOException {
		String ret = getRequiredString(props, name);
		try {
			return Integer.parseInt(ret);
		} catch (NumberFormatException ex) {
			throw new IOException("Config variable " + name + " is not an integer: \"" + ret + "\"");
		}
	}

	private static String getRequiredString(Properties props, String name) throws IOException {
		String ret = getStringOrNull(props, name);
		if (ret != null) {
			return ret;
		} else {
			throw new IOException("Required config variable is missing: " + name);
		}
	}

	private static String getStringOrNull(Properties props, String name) {
		Object ret = props.get(name);
		if (ret != null) {
			String s = ret.toString();
			if (s.length() > 0) {
				return s;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public SocketAddress getListenAddress() {
		return listenAddress;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public boolean isEnableStatus() {
		return enableStatus;
	}

	public SocketAddress getListenStatusAddress() {
		return listenStatusAddress;
	}

	public String getStatusUsername() {
		return statusUsername;
	}

	public String getStatusPassword() {
		return statusPassword;
	}

	public String getAuthString() {
		return authString;
	}

	public boolean isDownloadVanillaSkins() {
		return downloadVanillaSkins;
	}

	public Set<String> getAllowedSkinDownloadOrigins() {
		return allowedSkinDownloadOrigins;
	}

	public String getSkinCacheDBURI() {
		return skinCacheDBURI;
	}

	public boolean getSkinCacheDBSQLiteCompatible() {
		return skinCacheDBSQLiteCompatible;
	}

	public int getSkinCacheThreadPoolSize() {
		return skinCacheThreadPoolSize;
	}

	public int getDatabaseKeepObjectsDays() {
		return databaseKeepObjectsDays;
	}

	public int getDatabaseMaxObjects() {
		return databaseMaxObjects;
	}

	public int getDatabaseCompressionLevel() {
		return databaseCompressionLevel;
	}

	public int getMemoryCacheKeepObjectsSeconds() {
		return memoryCacheKeepObjectsSeconds;
	}

	public int getMemoryCacheMaxObjects() {
		return memoryCacheMaxObjects;
	}

	public String getSQLDriverClass() {
		return sqlDriverClass;
	}

	public String getSQLDriverPath() {
		return sqlDriverPath;
	}

}