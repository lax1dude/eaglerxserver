package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.netty.channel.unix.DomainSocketAddress;
import net.lax1dude.eaglercraft.backend.server.adapter.EnumAdapterPlatformType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class EaglerConfigLoader {

	public static ConfigDataRoot loadConfig(IPlatform<?> platform) throws IOException {
		ConfigHelper helper = new ConfigHelper(platform);
		return helper.getConfigDirectory(platform, (val) -> {
			return loadConfig(val, platform.getType());
		});
	}

	public static ConfigDataRoot loadConfig(IConfigDirectory root, EnumAdapterPlatformType platform)
			throws IOException {
		ConfigDataSettings settings = root.loadConfig("settings", (config) -> {
			String serverName = config.getString(
				"server_name", "EaglercraftXServer",
				"Sets the name of this EaglercraftX server that is sent with query responses "
				+ "and used for the default \"404 websocket upgrade failure\" page"
			);
			UUID serverUUID = UUID.fromString(config.getString(
				"server_uuid", () -> UUID.randomUUID().toString(),
				"Sets the UUID of this EaglercraftX server to send with query responses, has "
				+ "no official uses outside of server lists"
			));
			int eaglerLoginTimeout = config.getInteger(
				"eagler_login_timeout", 10000,
				"Default value is 10000, sets the maximum age in milliseconds that a connection "
				+ "can stay in the login phase before being disconnected, this is necessary "
				+ "because WebSocket ping frames could be used to keep a connection from timing "
				+ "out forever without ever having to advance it to the next state"
			);
			int httpMaxInitialLineLength = config.getInteger(
				"http_max_initial_line_length", 4096,
				"Default value is 4096, sets the maximum length for the initial request line"
			);
			int httpMaxHeaderSize = config.getInteger(
				"http_max_header_size", 16384,
				"Default value is 16384, sets the maximum combined length of all initial headers"
			);
			int httpMaxChunkSize = config.getInteger(
				"http_max_chunk_size", 16384,
				"Default value is 16384, sets the maximum length of every request body chunk"
			);
			int httpMaxContentLength = config.getInteger(
				"http_max_content_length", 65536,
				"Default value is 65536, sets the maximum total length of an incoming request body"
			);
			boolean httpAllowKeepAlive = config.getBoolean(
				"http_allow_keep_alive", false,
				"Default value is false, set to true to allow the server to keep non-WebSocket "
				+ "HTTP connections alive for multiple requests, so that the browser can reuse "
				+ "the same channel for efficiency. Ignore if you are using EaglerXServer for "
				+ "WebSockets only."
			);
			int httpWebSocketCompressionLevel = config.getInteger(
				"http_websocket_compression_level", 6,
				"Default value is 6, sets the ZLIB compression level (0-9) to use for "
				+ "compressing websocket frames, set to 0 to disable if HTTP compression is "
				+ "already handled through a reverse proxy. You almost definitely need some "
				+ "level of compression for the game to be playable on WiFi networks."
			);
			int httpWebSocketFragmentSize = config.getInteger(
				"http_websocket_fragment_size", 65536,
				"Default value is 65536, sets the size limit for websocket frames before a "
				+ "frame is split into multiple fragments"
			);
			int httpWebSocketMaxFrameLength = config.getInteger(
				"http_websocket_max_frame_length", 2097151,
				"Default value is 2097151, sets the max size for websocket frames"
			);
			int tlsCertRefreshRate = config.getInteger(
				"tls_certificate_refresh_rate", 60,
				"Default value is 60, how often in seconds to check if any listener TLS "
				+ "certificates have been changed and need to be reloaded."
			);
			boolean enableIsEaglerPlayerProperty = config.getBoolean(
				"enable_is_eagler_player_property", true,
				"Default value is true, can be used to control if the isEaglerPlayer GameProfile "
				+ "property should be added to EaglercraftX players, this property is primarily used "
				+ "to ensure that EaglercraftX players always only display their custom skins when "
				+ "viewed by another EaglercraftX players on the server instead of showing the "
				+ "skin attached to their Java Edition username, but this property has also caused "
				+ "plugins like ViaVersion to crash."
			);
			String eaglerPlayersVanillaSkin = config.getString(
				"eagler_players_vanilla_skin", "",
				"Default value is '' but was originally 'lax1dude', can be used to set "
				+ "the skin to apply to EaglercraftX players when a player on Minecraft Java "
				+ "Edition sees them in game. The value is the username or (dashed) UUID of "
				+ "a premium Minecraft account to use the skin from. You cannot use a local "
				+ "PNG file due to the profile signature requirements in vanilla Minecraft "
				+ "clients."
			);
			if(eaglerPlayersVanillaSkin.length() == 0) {
				eaglerPlayersVanillaSkin = null;
			}
			boolean enableAuthenticationEvents = config.getBoolean(
				"enable_authentication_events", false,
				"Default value is false, if the events for hooking into the EaglercraftX client's "
				+ "authentication system and cookie system should be enabled"
			);
			boolean enableBackendRPCAPI = config.getBoolean(
				"enable_backend_rpc_api", false,
				"Default value is false, if support for servers running the "
				+ "EaglerXBukkitAPI plugin should be enabled or not."
			);
			boolean useModernizedChannelNames = config.getBoolean(
				"use_modernized_channel_names", false,
				"Default value is false, if \"modernized\" plugin channel names "
				+ "compatible with Minecraft 1.13+ should be used for EaglerXBukkitAPI "
				+ "plugin message packets"
			);
			int protocolV4DefragSendDelay = config.getInteger(
				"protocol_v4_defrag_send_delay", 10,
				"Default value is 10, the number of milliseconds to wait before flushing all "
				+ "pending EaglercraftX plugin message packets, saves bandwidth by combining "
				+ "multiple messages into a single plugin message packet. Setting this to 0 has "
				+ "the same effect on clientbound packets as setting eaglerNoDelay to true does "
				+ "on a post-u37 client for all serverbound packets."
			);
			IEaglerConfSection protocols = config.getSection("protocols");
			int minMinecraftProtocol = protocols.getInteger(
				"min_minecraft_protocol", 47,
				"Default value is 47, sets the minimum Minecraft protocol version that "
				+ "EaglercraftX-based clients are allowed to connect with (47 = 1.8)"
			);
			int maxMinecraftProtocol = protocols.getInteger(
				"max_minecraft_protocol", 340,
				"Default value is 340, sets the maximum Minecraft protocol version that "
				+ "EaglercraftX-based clients are allowed to connect with (340 = 1.12.2)"
			);
			boolean eaglerXRewindAllowed = protocols.getBoolean(
				"eaglerxrewind_allowed", true,
				"If legacy clients (like eagler 1.5.2) should be allowed to join (emulates "
				+ "an EaglercraftX 1.8 connection), has no effect unless the EaglerXRewind "
				+ "plugin is installed."
			);
			boolean protocolLegacyAllowed = protocols.getBoolean(
				"protocol_legacy_allowed", true,
				"If v1 and v2 clients should be allowed to join."
			);
			boolean protocolV3Allowed = protocols.getBoolean(
				"protocol_v3_allowed", true,
				"If v3 clients should be allowed to join."
			);
			boolean protocolV4Allowed = protocols.getBoolean(
				"protocol_v4_allowed", true,
				"If v4 clients should be allowed to join."
			);
			boolean protocolV5Allowed = protocols.getBoolean(
				"protocol_v5_allowed", true,
				"If v5 clients should be allowed to join."
			);
			IEaglerConfSection skinService = config.getSection("skin_service");
			int skinLookupRatelimitPlayer = skinService.getInteger(
				"skin_lookup_ratelimit_player", 1000,
				"Default value is 1000, limit of how many skin lookups an eaglercraft player is "
				+ "allowed to attempt per minute."
			);
			boolean downloadVanillaSkinsToClients = skinService.getBoolean(
				"download_vanilla_skins_to_clients", true,
				"Default value is true, sets if the server should download the textures of "
				+ "custom skulls and skins of vanilla online-mode players from Mojang's "
				+ "servers to cache locally in an SQLite, MySQL, or MariaDB database, and "
				+ "send to all EaglercraftX clients on the server that attempt to render them."
			);
			IEaglerConfList validSkinDownloadURLsConf = skinService.getList("valid_skin_download_urls");
			if(!validSkinDownloadURLsConf.exists()) {
				validSkinDownloadURLsConf.setComment("List of strings, default includes only "
						+ "'textures.minecraft.net', sets the allowed domains to download "
						+ "custom skulls and skins from that are requested by EaglercraftX "
						+ "clients, only relevant if download_vanilla_skins_to_clients is enabled.");
			}
			Set<String> validSkinDownloadURLs = ImmutableSet
					.copyOf(validSkinDownloadURLsConf.getAsStringList(() -> Arrays.asList("textures.minecraft.net")));
			int skinDownloadRatelimit = skinService.getInteger(
				"skin_download_ratelimit_player", 250,
				"Default value is 250, limit of how many texture downloads a single player "
				+ "is allowed to trigger per minute, only relevant if download_vanilla_skins_to_clients "
				+ "is enabled."
			);
			int skinDownloadRatelimitGlobal = skinService.getInteger(
				"skin_download_ratelimit_global", 30000,
				"Default value is 30000, limit of how many texture downloads the entire server "
				+ "is allowed to perform per minute, only relevant if download_vanilla_skins_to_clients "
				+ "is enabled."
			);
			String skinCacheDBURI = skinService.getString(
				"skin_cache_db_uri", "jdbc:sqlite:eagler_skins_cache.db",
				"Default value is 'jdbc:sqlite:eaglercraft_skins_cache.db', the URI of JDBC "
				+ "database the cache to use for skins downloaded from Mojang, for MySQL "
				+ "databases this should include the username and password"
			);
			String skinCacheDriverClass = skinService.getString(
				"skin_cache_db_driver_class", "internal",
				"Default value is 'internal', the full name of the JDBC driver class to use "
				+ "for the database"
			);
			String skinCacheDriverPath = skinService.getString(
				"skin_cache_db_driver_path", "internal",
				"Default value is 'internal', the path to the JAR containing the JDBC driver "
				+ "to use for the database. If the driver is already on the classpath, set it "
				+ "to 'classpath'."
			);
			boolean skinCacheSQLiteCompatible = skinService.getBoolean(
				"skin_cache_db_sqlite_compatible", true,
				"Default value is true, if the skin cache should use SQL syntax compatible with "
				+ "SQLite, if false it is assumed you are using a MySQL or MariaDB database "
				+ "instead of the default setup."
			);
			int skinCacheThreadCount = skinService.getInteger(
				"skin_cache_thread_count", -1,
				"Default value is -1, sets the number of threads to use for database queries "
				+ "and compression. Set to -1 to use all available processors."
			);
			int skinCacheCompressionLevel = skinService.getInteger(
				"skin_cache_compression_level", 6,
				"Default value is 6, sets the compression level to use for the skin cache "
				+ "database, value can be between 0-9."
			);
			int skinCacheMemoryKeepSeconds = skinService.getInteger(
				"skin_cache_memory_keep_objects_seconds", 900,
				"Default value is 900, sets how many seconds skins and capes should be cached "
				+ "in memory after being downloaded/loaded from the database."
			);
			int skinCacheMemoryMaxObjects = skinService.getInteger(
				"skin_cache_memory_max_objects", 4096,
				"Default value is 4096, sets the maximum number of skins that should be "
				+ "cached in memory."
			);
			int skinCacheDiskKeepObjectsDays = skinService.getInteger(
				"skin_cache_disk_keep_objects_days", 45,
				"Default value is 45, sets the max age for textures (skin/cape files) stored "
				+ "in the skin cache database, only relevant if download_vanilla_skins_to_clients "
				+ "is enabled."
			);
			int skinCacheDiskMaxObjects = skinService.getInteger(
				"skin_cache_disk_max_objects", 32768,
				"Default value is 32768, sets the max number of textures (skin files) stored "
				+ "in the skin cache database before the oldest textures begin to be deleted, "
				+ "only relevant if download_vanilla_skins_to_clients is enabled."
			);
			int skinCacheAntagonistsRatelimit = skinService.getInteger(
				"skin_cache_antagonists_ratelimit", 15,
				"Default value is 15, sets the lockout limit for failing skin lookup requests, "
				+ "intended to reduce the effectiveness of some of the more simplistic types "
				+ "denial of service attacks that skids may attempt to perform on the skin "
				+ "download system, only relevant if download_vanilla_skins_to_clients is enabled."
			);
			boolean enableFNAWSkinModelsGlobal = skinService.getBoolean(
				"enable_fnaw_skin_models_global", true,
				"Default value is true, set to false to make the Five Nights At Winston's skins "
				+ "render with regular player models, can be used to avoid confused people "
				+ "complaining about hitboxes"
			);
			IEaglerConfList enableFNAWSkinsOnServersConf = skinService.getList("enable_fnaw_skin_models_servers");
			if(!enableFNAWSkinsOnServersConf.exists()) {
				enableFNAWSkinsOnServersConf.setComment("If enable_fnaw_skin_models_global is false, "
						+ "sets the list of servers (by name) where the FNAW should be enabled");
			}
			Set<String> enableFNAWSkinModelsOnServers = ImmutableSet
					.copyOf(enableFNAWSkinsOnServersConf.getAsStringList(() -> Collections.emptyList()));
			IEaglerConfSection voiceService = config.getSection("voice_service");
			boolean enableVoiceChatGlobal = voiceService.getBoolean(
				"enable_voice_service_global", false,
				"Default value is false, if voice chat should be enabled on all servers"
			);
			IEaglerConfList enableVoiceChatOnServersConf = skinService.getList("enable_voice_service_servers");
			if(!enableVoiceChatOnServersConf.exists()) {
				enableVoiceChatOnServersConf.setComment("If enable_voice_service_global is false, "
						+ "sets the list of servers (by name) where voice chat should be enabled");
			}
			Set<String> enableVoiceChatOnServers = ImmutableSet
					.copyOf(enableVoiceChatOnServersConf.getAsStringList(() -> Collections.emptyList()));
			boolean separateVoiceChannelsPerServer = voiceService.getBoolean(
				"separate_voice_channels_per_server", true,
				"Default value is true, if each server should get its own global voice channel, or "
				+ "if players on all servers should share the same global voice channel"
			);
			IEaglerConfSection updateService = config.getSection("update_service");
			boolean enableUpdateSystem = updateService.getBoolean(
				"enable_update_system", true,
				"Default value is true, if relaying certificates for the client update system "
				+ "should be enabled"
			);
			boolean discardLoginPacketCerts = updateService.getBoolean(
				"discard_login_packet_certs", false,
				"Default value is false, can be used to prevent the server from relaying random "
				+ "crowdsourced update certificates that were recieved from players who joined "
				+ "the server using signed clients."
			);
			int certPacketDataRateLimit = updateService.getInteger(
				"cert_packet_data_rate_limit", 524288,
				"Default value is 524288, can be used to set the global rate limit for how many "
				+ "bytes per second of certificates the server should send to all players."
			);
			boolean enableEagcertFolder = updateService.getBoolean(
				"enable_eagcert_folder", true,
				"Default value is true, can be used to enable or disable the \"eagcert\" folder "
				+ "used for distributing specific certificates as locally provided .cert files"
			);
			boolean downloadLatestCerts = updateService.getBoolean(
				"download_latest_certs", true,
				"Default value is true, can be used to automaticlly download the latest certificates "
				+ "to the \"eagcert\" folder"
			);
			IEaglerConfList downloadCertsFromConf = updateService.getList("download_certs_from");
			if(!downloadCertsFromConf.exists()) {
				downloadCertsFromConf.setComment("List of strings, defines the URLs to download "
						+ "the certificates from if download_latest_certs is enabled");
			}
			List<String> downloadCertsFrom = ImmutableList
					.copyOf(downloadCertsFromConf.getAsStringList(() -> Arrays.asList(
							"https://eaglercraft.com/backup.cert",
							"https://deev.is/eagler/backup.cert"
						)));
			int checkForUpdateEvery = updateService.getInteger(
				"check_for_update_every", 28800,
				"Default value is 28800 seconds, defines how often to check the URL list for "
				+ "updated certificates"
			);
			return new ConfigDataSettings(serverName, serverUUID, eaglerLoginTimeout, httpMaxInitialLineLength,
					httpMaxHeaderSize, httpMaxChunkSize, httpMaxContentLength, httpWebSocketCompressionLevel,
					httpWebSocketFragmentSize, httpWebSocketMaxFrameLength, httpAllowKeepAlive, tlsCertRefreshRate,
					enableAuthenticationEvents, enableBackendRPCAPI, useModernizedChannelNames,
					eaglerPlayersVanillaSkin, enableIsEaglerPlayerProperty, protocolV4DefragSendDelay,
					new ConfigDataSettings.ConfigDataProtocols(minMinecraftProtocol, maxMinecraftProtocol,
							eaglerXRewindAllowed, protocolLegacyAllowed, protocolV3Allowed, protocolV4Allowed,
							protocolV5Allowed),
					new ConfigDataSettings.ConfigDataSkinService(skinLookupRatelimitPlayer,
							downloadVanillaSkinsToClients, validSkinDownloadURLs, skinDownloadRatelimit,
							skinDownloadRatelimitGlobal, skinCacheDBURI, skinCacheDriverClass, skinCacheDriverPath,
							skinCacheSQLiteCompatible, skinCacheThreadCount, skinCacheCompressionLevel,
							skinCacheMemoryKeepSeconds, skinCacheMemoryMaxObjects, skinCacheDiskKeepObjectsDays,
							skinCacheDiskMaxObjects, skinCacheAntagonistsRatelimit, enableFNAWSkinModelsGlobal,
							enableFNAWSkinModelsOnServers),
					new ConfigDataSettings.ConfigDataVoiceService(enableVoiceChatGlobal, enableVoiceChatOnServers,
							separateVoiceChannelsPerServer),
					new ConfigDataSettings.ConfigDataUpdateService(enableUpdateSystem, discardLoginPacketCerts,
							certPacketDataRateLimit, enableEagcertFolder, downloadLatestCerts, downloadCertsFrom,
							checkForUpdateEvery));
		});
		Map<String, ConfigDataListener> listeners;
		if(platform == EnumAdapterPlatformType.BUKKIT) {
			listeners = root.loadConfig("listener", (config) -> {
				return ImmutableMap.of("default", loadListener(config, "default", platform));
			});
		}else {
			listeners = root.loadConfig("listeners", (config) -> {
				if(!config.exists()) {
					config.getSection("listener0");
				}
				ImmutableMap.Builder<String, ConfigDataListener> builder = ImmutableMap.builder();
				for(String key : config.getKeys()) {
					builder.put(key, loadListener(config.getSection(key), key, platform));
				}
				return builder.build();
			});
		}
		ConfigDataSupervisor supervisor = root.loadConfig("supervisor", (config) -> {
			boolean enableSupervisor = config.getBoolean(
				"enable_supervisor", false,
				"Set to true to run the plugin in multi-proxy mode with a supervisor server (EaglerXSupervisor)"
			);
			SocketAddress supervisorAddress = getAddr(config.getString(
				"supervisor_address", "0.0.0.0:36900",
				"The ip:port combo of the supervisor server, unix sockets are also supported via unix://"
			));
			String supervisorSecret = config.getString(
				"supervisor_secret", "",
				"Login secret, can be left blank, used as a last resort to protect the supervisor "
				+ "without a proper firewall if you're a dumbass"
			);
			int supervisorConnectTimeout = config.getInteger(
				"supervisor_connect_timeout", 30000,
				"Connection timeout in milliseconds of the supervisor server connection (default: 30000)"
			);
			int supervisorReadTimeout = config.getInteger(
				"supervisor_read_timeout", 30000,
				"Read timeout in milliseconds of the supervisor server connection (default: 30000)"
			);
			String supervisorUnavailableMessage = config.getString(
				"supervisor_unavailable_message", "Supervisor server is down",
				"Kick message displayed when a player attempts to login while the supervisor is down"
			);
			int supervisorSkinAntagonistsRatelimit = config.getInteger(
				"supervisor_skin_antagonists_ratelimit", 20,
				"How many fake skin/cape lookup requests to nonexistant players or URLs the proxy "
				+ "should tolerate in a minute before rate limiting a malicious player for attempting "
				+ "a denial-of-service (default: 20)"
			);
			int supervisorBrandAntagonistsRatelimit = config.getInteger(
				"supervisor_brand_antagonists_ratelimit", 40,
				"Same as skin antagonist ratelimit, except for client brand lookup requests (default: 40)"
			);
			boolean supervisorLookupIgnoreV2UUID = config.getBoolean(
				"supervisor_lookup_ignore_v2_uuid", true,
				"Workaround for NPCs, ignores v2 UUIDs in eagler skin, cape, and brand uuid lookups to "
				+ "avoid antagonist ratelimits (default: true)"
			);
			return new ConfigDataSupervisor(enableSupervisor, supervisorAddress, supervisorSecret,
					supervisorConnectTimeout, supervisorReadTimeout, supervisorUnavailableMessage,
					supervisorSkinAntagonistsRatelimit, supervisorBrandAntagonistsRatelimit,
					supervisorLookupIgnoreV2UUID);
		});
		List<ConfigDataICEServer> iceServers = root.loadConfig("ice_servers", (config) -> {
			ImmutableList.Builder<ConfigDataICEServer> builder = ImmutableList.builder();
			IEaglerConfList noPasswdList = config.getList("ice_servers_no_passwd");
			if(!noPasswdList.exists()) {
				noPasswdList.setComment("Defines a set of STUN/TURN server URIs to use that don't require a username and password.");
				noPasswdList.appendString("stun:stun.l.google.com:19302");
				noPasswdList.appendString("stun:stun1.l.google.com:19302");
				noPasswdList.appendString("stun:stun2.l.google.com:19302");
				noPasswdList.appendString("stun:stun3.l.google.com:19302");
				noPasswdList.appendString("stun:stun4.l.google.com:19302");
			}
			for(String str : noPasswdList.getAsStringList()) {
				builder.add(new ConfigDataICEServer(str));
			}
			IEaglerConfList passwdList = config.getList("ice_servers_passwd");
			if(!noPasswdList.exists()) {
				noPasswdList.setComment("Defines a set of STUN/TURN server URIs to use that do require a "
						+ "username and password, along with the username and password to use with each one. "
						+ "Note that these 'openrelay' TURN servers are no longer working as of 2024, and are "
						+ "only provided as an example");
				IEaglerConfSection section = noPasswdList.appendSection();
				section.getString("url", "turn:openrelay.metered.ca:80");
				section.getString("username", "openrelayproject");
				section.getString("password", "openrelayproject");
				section = noPasswdList.appendSection();
				section.getString("url", "turn:openrelay.metered.ca:443");
				section.getString("username", "openrelayproject");
				section.getString("password", "openrelayproject");
				section = noPasswdList.appendSection();
				section.getString("url", "turn:openrelay.metered.ca:443?transport=tcp");
				section.getString("username", "openrelayproject");
				section.getString("password", "openrelayproject");
			}
			for(int i = 0; i < passwdList.getLength(); ++i) {
				IEaglerConfSection section = passwdList.getIfSection(i);
				if(section != null) {
					String url = section.getIfString("url");
					String username = section.getIfString("username");
					String password = section.getIfString("password");
					if (url != null && username != null && password != null
							&& !url.startsWith("turn:openrelay.metered.ca")) {
						builder.add(new ConfigDataICEServer(url, username, password));
					}
				}
			}
			return builder.build();
		});
		ConfigDataPauseMenu pauseMenu = root.loadConfig("pause_menu", (config) -> {
			boolean enableCustomPauseMenu = config.getBoolean(
				"enable_custom_pause_menu", false,
				"Default value is false, if pause menu customization should be enabled on supported clients or not"
			);
			IEaglerConfSection serverInfoButtonConf = config.getSection("server_info_button");
			if(!serverInfoButtonConf.exists()) {
				serverInfoButtonConf.setComment("Defines properties of the \"Server Info\" button, which is always "
						+ "hidden unless pause menu customization is enabled");
			}
			boolean enableServerInfoButton = serverInfoButtonConf.getBoolean(
				"enable_button", true,
				"If the button should be shown or not"
			);
			String serverInfoButtonText = serverInfoButtonConf.getString(
				"button_text", "Server Info",
				"The text to display on the button, useful if you want to use this feature for something "
				+ "other than a \"Server Info\" button");
			boolean serverInfoButtonOpenNewTab = serverInfoButtonConf.getBoolean(
				"button_mode_open_new_tab", false,
				"Can be used to make the \"Server Info\" button act as a hyperlink that opens a URL in a "
				+ "new tab instead of displaying content in an embedded webview iframe in the client."
			);
			String serverInfoButtonEmbedURL = serverInfoButtonConf.getString(
				"server_info_embed_url", "",
				"Sets the URL for the \"Server Info\" button to use if it should open a URL in a new tab "
				+ "or the webview instead of directly downloading the markup to display from the "
				+ "EaglerXBungee server itself over the WebSocket."
			);
			boolean serverInfoButtonModeEmbedFile = serverInfoButtonConf.getBoolean(
				"button_mode_embed_file", true,
				"Determines if the \"Server Info\" button should download the webview markup directly "
				+ "from the EaglerXBungee server over WebSocket instead of loading an external URL. Cannot "
				+ "be used with button_mode_open_new_tab!"
			);
			String serverInfoButtonEmbedFile = serverInfoButtonConf.getString(
				"server_info_embed_file", "server_info.html",
				"Sets the name of the local file/template containing the markup to display in the "
				+ "\"Server Info\" webview if it is not in URL mode."
			);
			String serverInfoButtonEmbedScreenTitle = serverInfoButtonConf.getString(
				"server_info_embed_screen_title", "Server Info",
				"Sets the title string of the screen that displays the webview."
			);
			int serverInfoButtonEmbedSendChunkRate = serverInfoButtonConf.getInteger(
				"server_info_embed_send_chunk_rate", 1,
				"Defines how many chunks of server info data to send per 250ms when downloading the server "
				+ "info markup to a client."
			);
			int serverInfoButtonEmbedSendChunkSize = serverInfoButtonConf.getInteger(
				"server_info_embed_send_chunk_size", 24576,
				"Defines the size of each chunk of server info data when it is being downloaded to a client."
			);
			boolean serverInfoButtonEmbedEnableTemplateMacros = serverInfoButtonConf.getBoolean(
				"enable_template_macros", true,
				"if the server info markup should be processed for any eagler template macros (defined like "
				+ "{% arg1 `arg2` ... %})"
			);
			IEaglerConfSection serverInfoButtonEmbedTemplateGlobalsConf = serverInfoButtonConf.getSection("server_info_embed_template_globals");
			if(!serverInfoButtonEmbedTemplateGlobalsConf.exists()) {
				serverInfoButtonEmbedTemplateGlobalsConf.getString("example_global", "eagler");
			}
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
			for(String str : serverInfoButtonEmbedTemplateGlobalsConf.getKeys()) {
				String s = serverInfoButtonEmbedTemplateGlobalsConf.getIfString(str);
				if(s != null) {
					builder.put(str, s);
				}
			}
			Map<String, String> serverInfoButtonEmbedTemplateGlobals = builder.build();
			boolean serverInfoButtonEmbedAllowTemplateEvalMacro = serverInfoButtonConf.getBoolean(
				"allow_embed_template_eval_macro", false,
				"If the template processor should allow the \"eval\" macro to be used in the server info "
				+ "markup file (not to be confused with the JavaScript function, although there is never "
				+ "a good reason to use JavaScript's eval function in your code either)"
			);
			boolean serverInfoButtonEmbedEnableJavascript = serverInfoButtonConf.getBoolean(
				"enable_webview_javascript", false,
				"If the server info webview should allow JavaScript to be executed or not. This will display "
				+ "an \"allow JavaScript\" screen to your players the first time they attempt to view it."
			);
			boolean serverInfoButtonEmbedEnableMessageAPI = serverInfoButtonConf.getBoolean(
				"enable_webview_message_api", false,
				"If the server info webview has JavaScript enabled and should be permitted to open a message "
				+ "channel back to your EaglerXBungee server to exchange arbitrary message packets. This "
				+ "can be used, for example, to implement a dynamic menu on your server using JavaScript "
				+ "and HTML that players can access through the server info webview that integrates directly "
				+ "with your gamemodes."
			);
			boolean serverInfoButtonEmbedEnableStrictCSP = serverInfoButtonConf.getBoolean(
				"enable_webview_strict_csp", true,
				"If the csp attribute on the webview iframe should be set or not for added security, beware "
				+ "this is not supported on all browsers and will be silently disabled when the client "
				+ "detects it as unsupported."
			);
			IEaglerConfSection discordButtonConf = config.getSection("discord_button");
			if(!discordButtonConf.exists()) {
				discordButtonConf.setComment("Section, can be used to turn the \"Invite\" (formerly \"Open "
						+ "to LAN\") button on the pause menu into a \"Discord\" button that players can "
						+ "click to join your discord server");
			}
			boolean discordButtonEnable = discordButtonConf.getBoolean(
				"enable_button", true,
				"Sets if the discord button should be enabled or not."
			);
			String discordButtonText = discordButtonConf.getString(
				"button_text", "Discord",
				"Sets the text that should be displayed on the button"
			);
			String discordButtonURL =  discordButtonConf.getString(
				"button_url", "https://invite url here",
				"Defines the URL to open when the button is pressed"
			);
			IEaglerConfSection customImagesConf = config.getSection("custom_images");
			if(!customImagesConf.exists()) {
				customImagesConf.getString("icon_title_L", "");
				customImagesConf.getString("icon_title_R", "");
				customImagesConf.getString("icon_backToGame_L", "");
				customImagesConf.getString("icon_backToGame_R", "");
				customImagesConf.getString("icon_achievements_L", "");
				customImagesConf.getString("icon_achievements_R", "");
				customImagesConf.getString("icon_statistics_L", "");
				customImagesConf.getString("icon_statistics_R", "");
				customImagesConf.getString("icon_serverInfo_L", "");
				customImagesConf.getString("icon_serverInfo_R", "");
				customImagesConf.getString("icon_options_L", "");
				customImagesConf.getString("icon_options_R", "");
				customImagesConf.getString("icon_discord_L", "");
				customImagesConf.getString("icon_discord_R", "");
				customImagesConf.getString("icon_disconnect_L", "");
				customImagesConf.getString("icon_disconnect_R", "");
				customImagesConf.getString("icon_background_pause", "test_image.png");
				customImagesConf.getString("icon_background_all", "test_image.png");
				customImagesConf.getString("icon_watermark_pause", "");
				customImagesConf.getString("icon_watermark_all", "");
			}
			builder = ImmutableMap.builder();
			for(String str : customImagesConf.getKeys()) {
				String s = customImagesConf.getIfString(str);
				if(s != null) {
					builder.put(str, s);
				}
			}
			Map<String, String> customImages = builder.build();
			return new ConfigDataPauseMenu(enableCustomPauseMenu, enableServerInfoButton, serverInfoButtonText,
					serverInfoButtonOpenNewTab, serverInfoButtonEmbedURL, serverInfoButtonModeEmbedFile,
					serverInfoButtonEmbedFile, serverInfoButtonEmbedScreenTitle, serverInfoButtonEmbedSendChunkRate,
					serverInfoButtonEmbedSendChunkSize, serverInfoButtonEmbedEnableTemplateMacros,
					serverInfoButtonEmbedTemplateGlobals, serverInfoButtonEmbedAllowTemplateEvalMacro,
					serverInfoButtonEmbedEnableJavascript, serverInfoButtonEmbedEnableMessageAPI,
					serverInfoButtonEmbedEnableStrictCSP, discordButtonEnable, discordButtonText, discordButtonURL,
					customImages);
		});
		return new ConfigDataRoot(settings, listeners, supervisor, iceServers, pauseMenu);
	}

	private static ConfigDataListener loadListener(IEaglerConfSection listener, String name, EnumAdapterPlatformType platform) {
		SocketAddress injectAddress = null;
		if(platform != EnumAdapterPlatformType.BUKKIT) {
			injectAddress = getAddr(listener.getString(
				"inject_address", mapDefaultListener(platform),
				"The address of the listener to inject into, note that if no listeners with "
				+ "this address are configured on the underlying proxy, then this entry will "
				+ "not do anything"));
		}
		boolean dualStack = listener.getBoolean(
			"dual_stack", true,
			"Default value is true, sets if this listener should accept both Eaglercraft "
			+ "WebSockets and regular Minecraft Java Edition TCP connections. The connection "
			+ "type is determined from the first packet, where an HTTP/1.1 request will be "
			+ "treated as an Eaglercraft connection, and anything else will be assumed to be "
			+ "an ordinary vanilla TCP connection."
		);
		boolean forwardIp = listener.getBoolean(
			"forward_ip", false,
			"Default value is false, sets if connections to this listener will use an HTTP "
			+ "header to forward the player's real IP address from a reverse proxy (or "
			+ "CloudFlare) to the BungeeCord server. This is required for EaglerXBungee's "
			+ "rate limiting and a lot of plugins to work correctly if they are used behind "
			+ "a reverse HTTP proxy or CloudFlare."
		);
		String forwardIPHeader = listener.getString(
			"forward_ip_header", "X-Real-IP",
			"Default value is 'X-Real-IP', sets the name of the request header that contains "
			+ "the player's real IP address if the forward_ip option is enabled. This option "
			+ "is commonly set to X-Forwarded-For or CF-Connecting-IP for a lot of server setups."
		);
		IEaglerConfSection tlsConfigSection = listener.getSection("tls_config");
		if(!tlsConfigSection.exists()) {
			tlsConfigSection.setComment("Settings for HTTPS (WSS) connections, HTTPS is normally "
					+ "handled by nginx or caddy, but if you are trying to run EaglerXServer "
					+ "without any reverse HTTP proxies then this can be useful.");
		}
		boolean enableTLS = tlsConfigSection.getBoolean(
			"enable_tls", false,
			"Default value is false, sets if this listener should accept HTTPS (WSS) connections."
		);
		boolean requireTLS = tlsConfigSection.getBoolean(
			"require_tls", true,
			"Default value is true, sets if this listener should always assume connections to be "
			+ "HTTPS (WSS), requires enable_tls to be true."
		);
		boolean tlsManagedByExternalPlugin = tlsConfigSection.getBoolean(
			"tls_managed_by_external_plugin", false,
			"Default value is false, if the TLS certificates for this listener are managed by another plugin."
		);
		String tlsPublicChainFile = tlsConfigSection.getString(
			"tls_public_chain_file", "fullchain.pem",
			"The X.509 certificate chain file in PEM format, relative to the working directory."
		);
		String tlsPrivateKeyFile = tlsConfigSection.getString(
			"tls_private_key_file", "privatekey.pem",
			"The PKCS#8 private key file in PEM format, relative to the working directory."
		);
		String tlsPrivateKeyPassword = tlsConfigSection.getString(
			"tls_private_key_password", "",
			"The password to the private key (if applicable), leave blank for none"
		);
		if(tlsPrivateKeyPassword.trim().length() == 0|| "null".equals(tlsPrivateKeyPassword)) {
			tlsPrivateKeyPassword = null;
		}
		boolean tlsAutoRefreshCert = tlsConfigSection.getBoolean(
			"tls_auto_refresh_cert", true,
			"Default value is true, if the certificate and private key should be reloaded when changes are detected."
		);
		String redirectLegacyClientsTo = listener.getString(
			"redirect_legacy_clients_to", "",
			"Default value is '', sets the WebSocket address to redirect legacy Eaglercraft "
			+ "1.5 clients to if they mistakenly try to join the server through this listener."
		);
		if(redirectLegacyClientsTo.trim().length() == 0 || "null".equals(redirectLegacyClientsTo)) {
			redirectLegacyClientsTo = null;
		}
		String serverIcon = listener.getString(
			"server_icon", "server-icon.png",
			"Default value is 'server-icon.png', sets the name of the 64x64 PNG file to display "
			+ "as this listener's server icon, relative to the working directory."
		);
		IEaglerConfList serverMOTDConf = listener.getList("server_motd");
		if(!serverMOTDConf.exists()) {
			serverMOTDConf.setComment("List of up to 2 strings, default value is '&6An "
					+ "EaglercraftX server', sets the contents of the listener's MOTD, which is "
					+ "the text displayed along with the server_icon when players add this "
					+ "server's listener address to their client's Multiplayer menu server list.");
		}
		List<String> serverMOTD = ImmutableList
				.copyOf(serverMOTDConf.getAsStringList(() -> Arrays.asList(
						"&6An EaglercraftX server"
					)));
		boolean allowMOTD = listener.getBoolean(
			"allow_motd", true,
			"Default value is true, is this listener should respond to MOTD queries or not"
		);
		boolean allowQuery = listener.getBoolean(
			"allow_query", true,
			"Default value is true, is this listener should respond to other query types or not"
		);
		boolean showMOTDPlayerList = listener.getBoolean(
			"show_motd_player_list", true,
			"Default value is true, if this listener's MOTD should list the names of online "
			+ "players or not"
		);
		boolean allowCookieRevokeQuery = listener.getBoolean(
			"allow_cookie_revoke_query", true,
			"Default value is true, If this listener should accept queries from post-u37 "
			+ "clients to revoke session tokens, you need to create your own BungeeCord "
			+ "plugin to go with EaglerXBungee that handles the EaglercraftRevokeSessionQueryEvent "
			+ "event it fires in order for this feature to work correctly."
		);
		IEaglerConfSection requestMOTDCache = listener.getSection("request_motd_cache");
		if(!requestMOTDCache.exists()) {
			requestMOTDCache.setComment("Section that defines caching hints for server lists "
					+ "that cache the MOTD via the 'MOTD.cache' query. As far as we know, not "
					+ "even the official Eaglercraft Server List on eaglercraft.com currently "
					+ "pays attention to these hints or attempts to cache MOTDs, so they can "
					+ "be ignored for now.");
		}
		int motdCacheTTL = requestMOTDCache.getInteger(
			"cache_ttl", 7200,
			"Default value is 7200, sets how many seconds for the server list to store the "
			+ "MOTD in cache."
		);
		boolean motdCacheAnimation = requestMOTDCache.getBoolean(
			"online_server_list_animation", false,
			"Default is false, if the MOTD should be cached in an \"animated format\" that is "
			+ "yet to be standardized."
		);
		boolean motdCacheResults = requestMOTDCache.getBoolean(
			"online_server_list_results", true,
			"Default is true, if the MOTD should be cached when shown in search results."
		);
		boolean motdCacheTrending = requestMOTDCache.getBoolean(
			"online_server_list_trending", true,
			"Default is true, if the MOTD should be cached if the server makes it to the top "
			+ "of the homepage."
		);
		boolean motdCachePortfolios = requestMOTDCache.getBoolean(
			"online_server_list_portfolios", true,
			"Default is true, if the MOTD should be cached when viewing more details about "
			+ "the specific server."
		);
		IEaglerConfSection ratelimitConf = listener.getSection("ratelimit");
		ConfigDataListener.ConfigRateLimit limitIP = loadRatelimiter(
			ratelimitConf, "ip", 90, 60, 80, 1200,
			"Global ratelimit imposed on all connection types."
		);
		ConfigDataListener.ConfigRateLimit limitLogin = loadRatelimiter(
			ratelimitConf, "login", 50, 5, 10, 300,
			"Sets ratelimit on login (server join) attempts."
		);
		ConfigDataListener.ConfigRateLimit limitMOTD = loadRatelimiter(
			ratelimitConf, "motd", 30, 5, 15, 300,
			"Sets ratelimit on MOTD query types."
		);
		ConfigDataListener.ConfigRateLimit limitQuery = loadRatelimiter(
			ratelimitConf, "query", 30, 15, 25, 800,
			"Sets ratelimit on all other query types."
		);
		ConfigDataListener.ConfigRateLimit limitHTTP = loadRatelimiter(
			ratelimitConf, "http", 30, 10, 20, 300,
			"Sets ratelimit on non-WebSocket HTTP connections."
		);
		return new ConfigDataListener(name, injectAddress, dualStack, forwardIp, forwardIPHeader, enableTLS, requireTLS,
				tlsManagedByExternalPlugin, tlsPublicChainFile, tlsPrivateKeyFile, tlsPrivateKeyPassword,
				tlsAutoRefreshCert, redirectLegacyClientsTo, serverIcon, serverMOTD, allowMOTD, allowQuery,
				showMOTDPlayerList, allowCookieRevokeQuery, motdCacheTTL, motdCacheAnimation, motdCacheResults,
				motdCacheTrending, motdCachePortfolios, limitIP, limitLogin, limitMOTD, limitQuery, limitHTTP);
	}

	private static ConfigDataListener.ConfigRateLimit loadRatelimiter(IEaglerConfSection parent, String name,
			int period, int limit, int limitLockout, int lockoutDuration, String comment) {
		IEaglerConfSection limitCfg = parent.getSection(name);
		if(!limitCfg.exists()) {
			limitCfg.setComment(comment);
		}
		boolean enableConf = limitCfg.getBoolean(
			"enable", true,
			"If the rate limit should be enabled."
		);
		int periodConf = limitCfg.getInteger(
			"period", period,
			"Sets the period in the number of seconds."
		);
		int limitConf = limitCfg.getInteger(
			"limit", limit,
			"Sets the number of requests a single IP address can send in period seconds before being limited."
		);
		int limitLockoutConf = limitCfg.getInteger(
			"limit_lockout", limitLockout,
			"Sets the number of requests a single IP address can send in period seconds before being locked out."
		);
		int lockoutDurationConf = limitCfg.getInteger(
			"lockout_duration", lockoutDuration,
			"Sets the total number of seconds a \"lock out\" should last on this limiter."
		);
		IEaglerConfList exceptionsConf = limitCfg.getList("server_motd");
		if(!exceptionsConf.exists()) {
			exceptionsConf.setComment("List of up to 2 strings, default value is '&6An "
					+ "EaglercraftX server', sets the contents of the listener's MOTD, which is "
					+ "the text displayed along with the server_icon when players add this "
					+ "server's listener address to their client's Multiplayer menu server list.");
		}
		List<String> exceptionsConfList = ImmutableList
				.copyOf(exceptionsConf.getAsStringList(() -> Arrays.asList("127.*", "0:0:0:0:0:0:0:1")));
		return new ConfigDataListener.ConfigRateLimit(enableConf, periodConf, limitConf, limitLockoutConf,
				lockoutDurationConf, exceptionsConfList);
	}

	private static String mapPlatformName(EnumAdapterPlatformType platform) {
		switch(platform) {
		case BUNGEE:
			return "BungeeCord";
		case BUKKIT:
			return "Bukkit";
		case VELOCITY:
			return "Velocity";
		default:
			return platform.name();
		}
	}

	private static String mapDefaultListener(EnumAdapterPlatformType platform) {
		switch(platform) {
		case BUNGEE:
		case VELOCITY:
			return "0.0.0.0:25577";
		case BUKKIT:
			return "0.0.0.0:25565";
		default:
			return "0.0.0.0:8081";
		}
	}

	public static SocketAddress getAddr(String hostline) {
		URI uri = null;
		try {
			uri = new URI(hostline);
		} catch (URISyntaxException ex) {
		}

		if (uri != null && "unix".equals(uri.getScheme())) {
			return new DomainSocketAddress(uri.getPath());
		}

		if (uri == null || uri.getHost() == null) {
			try {
				uri = new URI("tcp://" + hostline);
			} catch (URISyntaxException ex) {
				throw new IllegalArgumentException("Bad hostline: " + hostline, ex);
			}
		}

		if (uri.getHost() == null) {
			throw new IllegalArgumentException("Invalid host/address: " + hostline);
		}

		return new InetSocketAddress(uri.getHost(), (uri.getPort()) == -1 ? 36900 : uri.getPort());
	}

}
