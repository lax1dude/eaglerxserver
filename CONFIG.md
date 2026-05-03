## EaglerXServer Config Reference
Generated from the source code of EaglerXServer 1.1.0

*(Placeholder extension ".cfg" replaced with ".yaml", ".toml", or ".gson")*

## <small>`/settings.cfg` &gt; `brand_lookup_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 240, sets the rate limit per minute for client brand lookup requests.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `240`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `debug_log_client_brands`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the server should log client brand information to the console. Can be nice for keeping track of what clients your players are using.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `debug_log_new_channels`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the server should log all inbound TCP connections on Eaglercraft listeners to the console, can be useful for diagnosing certain connection issues. Connection issues are your problem if there's nothing printed in the console with this setting enabled.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `debug_log_origin_headers`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the server should log origin headers to the console. This used to be default on EaglerXBungee and EaglerXVelocity.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `debug_log_real_ip_headers`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the server should log IP forwarding headers to the console, for verifying if IP forwarding is set up correctly.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `eagler_login_timeout`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 10000, sets the maximum age in milliseconds that a connection can stay in the login phase before being disconnected, this is necessary because WebSocket ping frames could be used to keep a connection from timing out forever without ever having to advance it to the next state&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `10000`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `eagler_players_vanilla_skin`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is '' but was originally 'lax1dude', can be used to set the skin to apply to EaglercraftX players when a player on Minecraft Java Edition sees them in game. The value is the username or (dashed) UUID of a premium Minecraft account to use the skin from. You cannot use a local PNG file due to the profile signature requirements in vanilla Minecraft clients.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `eagler_players_view_distance`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is -1, allows you to set a separate view distance to use for Eaglercraft players, must be between 3 an 15 chunks or -1 to use the same view distance as vanilla players. Only supported on Paper, if EaglerXServer is installed on the BungeeCord/Velocity proxy then EaglerXBackendRPC is required on the backend Paper servers for the setting to have any effect.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `-1`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `enable_authentication_events`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the events for hooking into the EaglercraftX client's authentication system and cookie system should be enabled&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `enable_backend_rpc_api`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if support for servers running the EaglerXBackendRPC plugin should be enabled or not.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `enable_is_eagler_player_property`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, can be used to control if the isEaglerPlayer GameProfile property should be added to EaglercraftX players, this property is primarily used to ensure that EaglercraftX players always only display their custom skins when viewed by another EaglercraftX players on the server instead of showing the skin attached to their Java Edition username, but this property has also caused plugins like ViaVersion to crash.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_max_chunk_size`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 16384, sets the maximum length of every request body chunk&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `16384`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_max_content_length`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 65536, sets the maximum total length of an incoming request body&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `65536`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_max_header_size`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 16384, sets the maximum combined length of all initial headers&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `16384`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_max_initial_line_length`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 4096, sets the maximum length for the initial request line&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `4096`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_websocket_compression_level`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 6, sets the ZLIB compression level (0-9) to use for compressing websocket frames, set to 0 to disable if HTTP compression is already handled through a reverse proxy. You almost definitely need some level of compression for the game to be playable on WiFi networks.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `6`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_websocket_fragment_size`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 65536, sets the size limit for websocket frames before a frame is split into multiple fragments&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `65536`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_websocket_max_frame_length`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 2097151, sets the max size for websocket frames&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `2097151`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `http_websocket_ping_intervention`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, can be used to make EaglerXServer "intervene" when it detects a connection about to time out due to inactivity, by sending a WebSocket ping frame to try and get a response from the client. Chrome has been shown to ignore WebSocket ping frames on an inactive tab, so this won't do anything to prevent the game from timing out when switching tabs.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocol_v4_defrag_max_packets`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 64, sets the maximum number of EaglercraftX plugin messages that a client can send in a single plugin message packet. The server will attempt to forward this parameter to V5+ clients joining the server so that they don't accidentally exceed it, but outdated clients will just ignore it. Therefore, any extra packets in an oversized multi-packet are currently just ignored without raising an exception to avoid kicking outdated clients.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `64`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocol_v4_defrag_send_delay`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 10, the number of milliseconds to wait before flushing all pending EaglercraftX plugin message packets, saves bandwidth by combining multiple messages into a single plugin message packet. Setting this to 0 has the same effect on clientbound packets as setting eaglerNoDelay to true does on a post-u37 client for all serverbound packets.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `10`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/settings.cfg` &gt; `protocols`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Sets the protocol versions Eaglercraft players should be allowed to join this server with.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `eaglerxrewind_allowed`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If legacy clients (like eagler 1.5.2) should be allowed to join (emulates an EaglercraftX 1.8 connection), has no effect unless the EaglerXRewind plugin is installed.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `max_minecraft_protocol`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 340, sets the maximum Minecraft protocol version that EaglercraftX-based clients are allowed to connect with (340 = 1.12.2)&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `340`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `max_minecraft_protocol_v5`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is -1, sets the maximum Minecraft protocol version that protocol v5 EaglercraftX-based clients are allowed to connect with (-1 = any Minecraft protocol version)&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `-1`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `min_minecraft_protocol`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 3, sets the minimum Minecraft protocol version that EaglercraftX-based clients are allowed to connect with (3 = 1.7)&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `3`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `protocol_legacy_allowed`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If v1 and v2 clients should be allowed to join.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `protocol_v3_allowed`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If v3 clients should be allowed to join.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `protocol_v4_allowed`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If v4 clients should be allowed to join.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `protocols` : `protocol_v5_allowed`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If v5 clients should be allowed to join.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `server_name`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the name of this EaglercraftX server that is sent with query responses and used for the default "404 websocket upgrade failure" page&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"EaglercraftXServer"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `server_uuid`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the UUID of this EaglercraftX server to send with query responses, has no official uses outside of server lists&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/settings.cfg` &gt; `skin_service`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Settings for the eagler skins and capes service, and for the skin cache database.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `cape_lookup_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 180, sets the primary rate limit per minute for player cape requests.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `180`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `download_vanilla_skins_to_clients`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, sets if the server should download the textures of custom skulls and skins of vanilla online-mode players from Mojang's servers to cache locally in an SQLite, MySQL, or MariaDB database, and send to all EaglercraftX clients on the server that attempt to render them.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `enable_fnaw_skin_models_global`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, set to false to make the Five Nights At Winston's skins render with regular player models, can be used to avoid confused people complaining about hitboxes.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `enable_fnaw_skin_models_servers`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If enable_fnaw_skin_models_global is false, sets the list of servers (by name) where the FNAW should be enabled&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `[  ]`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `enable_fnaw_skin_models_worlds`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If enable_fnaw_skin_models_global is false, sets the list of worlds (by name) where the FNAW should be enabled&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `[  ]`&emsp;<sub>(Bukkit)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `enable_skinsrestorer_apply_hook`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, sets if the skin service should listen for SkinsRestorer apply events on vanilla players to refresh their skins, usually required for SkinsRestorer skins to display properly to eagler clients.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_antagonists_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 15, sets the lockout limit for failing skin lookup requests, intended to reduce the effectiveness of some of the more simplistic types denial of service attacks that skids may attempt to perform on the skin download system, only relevant if download_vanilla_skins_to_clients is enabled.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `15`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_compression_level`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 6, sets the compression level to use for the skin cache database, value can be between 0-9.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `6`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_db_driver_class`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'internal', the full name of the JDBC driver class to use for the database&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"internal"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_db_driver_path`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'internal', the path to the JAR containing the JDBC driver to use for the database. If the driver is already on the classpath, set it to 'classpath'.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"internal"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_db_force_connection_pool`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the skin cache should always use a pool of multiple connections even when skin_cache_db_sqlite_compatible is true, you don't want want to enable this unless you're using the SQLite compatible syntax feature with a non-embedded database.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_db_sqlite_compatible`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if the skin cache should use SQL syntax compatible with SQLite, if false it is assumed you are using a MySQL or MariaDB database instead of the default setup.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_db_uri`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'jdbc:sqlite:eagler_skins_cache.db', the URI of JDBC database the cache to use for skins downloaded from Mojang, for MySQL databases this should include the username and password&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"jdbc:sqlite:eagler_skins_cache.db"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_disk_keep_objects_days`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 45, sets the max age for textures (skin/cape files) stored in the skin cache database, only relevant if download_vanilla_skins_to_clients is enabled.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `45`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_disk_max_objects`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 32768, sets the max number of textures (skin files) stored in the skin cache database before the oldest textures begin to be deleted, only relevant if download_vanilla_skins_to_clients is enabled.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `32768`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_memory_keep_objects_seconds`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 900, sets how many seconds skins and capes should be cached in memory after being downloaded/loaded from the database.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `900`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_memory_max_objects`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 4096, sets the maximum number of skins that should be cached in memory.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `4096`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_cache_thread_count`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is -1, sets the number of threads to use for database queries and compression. Set to -1 to use all available processors.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `-1`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `skin_lookup_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 240, sets the primary rate limit per minute for player skin requests, including requests for custom skull textures.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `240`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `skin_service` : `valid_skin_download_urls`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- List of strings, default includes only 'textures.minecraft.net', sets the allowed domains to download custom skulls and skins from that are requested by EaglercraftX clients, only relevant if download_vanilla_skins_to_clients is enabled.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `[ "textures.minecraft.net" ]`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `tls_certificate_refresh_rate`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 60, how often in seconds to check if any listener TLS certificates have been changed and need to be reloaded.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `60`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/settings.cfg` &gt; `update_checker`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Settings for the eagler server update checker, please keep your server updated!&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_checker` : `check_for_update_every`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 86400 seconds, defines how often EaglerXServer should check for updates, set to -1 to only check for updates at startup.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `86400`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_checker` : `enable_update_checker`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if EaglerXServer should check for plugin updates from lax1dude. Updates are never installed automatically.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_checker` : `print_chat_messages`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if the server should print reminders in the chat when a new plugin update is available.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/settings.cfg` &gt; `update_service`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Settings for the eagler update certificate service.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `cert_packet_data_rate_limit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 524288, can be used to set the global rate limit for how many bytes per second of certificates the server should send to all players.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `524288`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `check_for_update_every`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 28800 seconds, defines how often to check the URL list for updated certificates&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `28800`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `discard_login_packet_certs`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, can be used to prevent the server from relaying random crowdsourced update certificates that were recieved from players who joined the server using signed clients.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `download_certs_from`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- List of strings, defines the URLs to download the certificates from if download_latest_certs is enabled&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `[ "https://deev.is/eagler/backup.cert" ]`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `download_latest_certs`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, can be used to automaticlly download the latest certificates to the "eagcert" folder.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `enable_eagcert_folder`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, can be used to enable or disable the "eagcert" folder used for distributing specific certificates as locally provided .cert files.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `update_service` : `enable_update_system`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if relaying certificates for the client update system should be enabled.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `use_modernized_channel_names`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if "modernized" plugin channel names compatible with Minecraft 1.13+ should be used for EaglerXBackendRPC plugin message packets. Enable this if you use Minecraft 1.13+ on your backend Spigot servers.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/settings.cfg` &gt; `voice_service`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Settings for the eagler voice chat service.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `enable_voice_all_servers`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if voice chat should be enabled on all servers.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `enable_voice_all_worlds`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, if voice chat should be enabled on all servers.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `enable_voice_on_servers`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If enable_voice_all_servers is false, sets the list of servers (by name) where voice chat should be enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `[  ]`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `enable_voice_on_worlds`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If enable_voice_all_worlds is false, sets the list of worlds (by name) where voice chat should be enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `[  ]`&emsp;<sub>(Bukkit)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `enable_voice_service`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the voice service should be enabled, using voice chat on large public servers is discouraged since the eagler voice protocol is very easy to stress and abuse and lacks proper validation for certain packets&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `separate_server_voice_channels`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if each server should get its own global voice channel, or if players on all servers should share the same global voice channel.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `separate_world_voice_channels`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, if each world should get its own global voice channel, or if players in all worlds should share the same global voice channel.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `voice_backend_relayed_mode`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if voice packets should be relayed by the backend Spigot server instead of the EaglerXServer proxy, allows your Spigot plugins to take full control of the eagler voice service, also allows the voice service to work while using the supervisor. Requires the EaglerXBackendRPC plugin.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `voice_connect_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 20, sets the rate limit per minute for players to toggle voice.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `20`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `voice_ice_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 600, sets the rate limit per minute for players to exchange WebRTC descriptions and ICE candidates once handshaking.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `600`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `voice_service` : `voice_request_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 120, sets the rate limit per minute for players initiating a WebRTC handshake with another client on the server, ignored on older client versions due to a bug that causes the client to spam requests excessively.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `120`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `webview_download_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 8, sets the rate limit per minute for webview download requests.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `8`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/settings.cfg` &gt; `webview_message_ratelimit`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 120, sets the rate limit per minute for webview message packets.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `120`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/ice_servers.cfg` &gt; `ice_servers_no_passwd`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Defines a set of STUN/TURN server URIs to use that don't require a username and password.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `[ "stun:stun.l.google.com:19302", "stun:stun1.l.google.com:19302", "stun:stun2.l.google.com:19302", "stun:stun3.l.google.com:19302", "stun:stun4.l.google.com:19302" ]`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/ice_servers.cfg` &gt; [`ice_servers_passwd`]&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Defines a set of STUN/TURN server URIs to use that do require a username and password, along with the username and password to use with each one. Note that these 'openrelay' TURN servers are no longer working as of 2024, and are only provided as an example&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/ice_servers.cfg` &gt; [`ice_servers_passwd`] : `password`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `"openrelayproject"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/ice_servers.cfg` &gt; [`ice_servers_passwd`] : `url`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `"turn:openrelay.metered.ca:80"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
- `"turn:openrelay.metered.ca:443"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
- `"turn:openrelay.metered.ca:443?transport=tcp"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/ice_servers.cfg` &gt; [`ice_servers_passwd`] : `username`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `"openrelayproject"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/listener.cfg` &gt; `allow_cookie_revoke_query`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, If this listener should accept queries from post-u37 clients to revoke session tokens, you need to create your own BungeeCord/Velocity plugin to go with EaglerXServer that handles the EaglercraftRevokeSessionQueryEvent event it fires in order for this feature to work correctly.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `allow_motd`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, is this listener should respond to MOTD queries or not&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `allow_query`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, is this listener should respond to other query types or not&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `dual_stack`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, sets if this listener should accept both Eaglercraft WebSockets and regular Minecraft Java Edition TCP connections. The connection type is determined from the first packet, where an HTTP/1.1 request will be treated as an Eaglercraft connection, and anything else will be assumed to be an ordinary vanilla TCP connection.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `forward_ip`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is false, sets if connections to this listener will use an HTTP header to forward the player's real IP address from a reverse proxy (or CloudFlare) to the BungeeCord/Velocity server. This is required for EaglerXServer's rate limiting and a lot of plugins to work correctly if they are used behind a reverse HTTP proxy or CloudFlare.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `forward_ip_header`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is 'X-Real-IP', sets the name of the request header that contains the player's real IP address if the forward_ip option is enabled. This option is commonly set to X-Forwarded-For or CF-Connecting-IP for a lot of server setups.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `"X-Real-IP"`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `forward_secret`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is false, sets if HTTP and WebSocket connections to this listener require a header with a secret to be accepted, can be used to prevent someone from bypassing CloudFlare, nginx, or whatever and connecting directly to the EaglerXServer listener with a fake forward IP header.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `forward_secret_file`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is 'eagler_forwarding.secret', sets the name of the file that contains the secret if the forward_secret option is enabled, relative to the server's working directory.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `"eagler_forwarding.secret"`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `forward_secret_header`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is 'X-Eagler-Secret', sets the name of the request header that contains the secret if the forward_secret option is enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `"X-Eagler-Secret"`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `ratelimit`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `disable_ratelimit`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- List of IPv4 and IPv6 addresses to disable ratelimiting for, use CIDR notation to specify entire subnets, default value includes localhost to ensure ratelimiting is disabled by default when EaglerXServer is used with nginx and caddy. If forward_ip is true, the ratelimits will be applied based on the forwarded address instead of the raw socket address.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `[ "127.0.0.0/8", "::1/128" ]`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `ratelimit` : `http`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Sets ratelimit on non-WebSocket HTTP connections.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `http` : `enable`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `http` : `limit`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `10`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `http` : `limit_lockout`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `20`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `http` : `lockout_duration`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `300`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `http` : `period`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `30`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `ratelimit` : `ip`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Global ratelimit imposed on all connection types.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `ip` : `enable`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `ip` : `limit`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `60`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `ip` : `limit_lockout`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `80`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `ip` : `lockout_duration`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `1200`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `ip` : `period`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `90`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `ratelimit` : `login`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Sets ratelimit on login (server join) attempts.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `login` : `enable`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `login` : `limit`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `5`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `login` : `limit_lockout`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `10`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `login` : `lockout_duration`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `300`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `login` : `period`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `50`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `ratelimit` : `motd`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Sets ratelimit on MOTD query types.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `motd` : `enable`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `motd` : `limit`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `5`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `motd` : `limit_lockout`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `15`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `motd` : `lockout_duration`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `300`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `motd` : `period`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `30`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `ratelimit` : `query`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Sets ratelimit on all other query types.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `query` : `enable`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `query` : `limit`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `15`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `query` : `limit_lockout`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `25`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `query` : `lockout_duration`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `800`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `ratelimit` : `query` : `period`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `30`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `redirect_legacy_clients_to`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is '', sets the WebSocket address to redirect legacy Eaglercraft 1.5 clients to if they mistakenly try to join the server through this listener.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `""`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `request_motd_cache`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Section that defines caching hints for server lists that cache the MOTD via the 'MOTD.cache' query. As far as we know, not even the official Eaglercraft Server List on eaglercraft.com currently pays attention to these hints or attempts to cache MOTDs, so they can be ignored for now.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `request_motd_cache` : `cache_ttl`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is 7200, sets how many seconds for the server list to store the MOTD in cache.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `7200`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `request_motd_cache` : `online_server_list_animation`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default is false, if the MOTD should be cached in an "animated format" that is yet to be standardized.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `request_motd_cache` : `online_server_list_portfolios`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default is true, if the MOTD should be cached when viewing more details about the specific server.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `request_motd_cache` : `online_server_list_results`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default is true, if the MOTD should be cached when shown in search results.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `request_motd_cache` : `online_server_list_trending`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default is true, if the MOTD should be cached if the server makes it to the top of the homepage.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `server_icon`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is 'server-icon.png', sets the name of the 64x64 PNG file to display as this listener's server icon, relative to the working directory.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `"server-icon.png"`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `server_motd`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- List of up to 2 strings, default value is '&6An EaglercraftX server', sets the contents of the listener's MOTD, which is the text displayed along with the server_icon when players add this server's listener address to their client's Multiplayer menu server list.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `[ "&6An EaglercraftX server" ]`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `show_motd_player_list`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, if this listener's MOTD should list the names of online players or not&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `spoof_player_address_forwarded`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, if the effective remote addresses of Eaglercraft connections in the underlying server should be spoofed to the address received via the forward_ip header. Has no effect if forward_ip is not true, if false then plugins will need to use the EaglerXServer API to determine the forwarded IP address of an Eaglercraft player.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## `/listener.cfg` &gt; `tls_config`&emsp;<sub>(Bukkit)</sub>
**Summary:**
- Settings for HTTPS (WSS) connections, HTTPS is normally handled by nginx or caddy, but if you are trying to run EaglerXServer without any reverse HTTP proxies then this can be useful.&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `enable_tls`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is false, sets if this listener should accept HTTPS (WSS) connections.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `require_tls`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, sets if this listener should always assume connections to be HTTPS (WSS), requires enable_tls to be true.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `tls_auto_refresh_cert`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is true, if the certificate and private key should be reloaded when changes are detected.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `tls_managed_by_external_plugin`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- Default value is false, if the TLS certificates for this listener are managed by another plugin.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `tls_private_key_file`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- The PKCS#8 private key file in PEM format, relative to the working directory.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `"privatekey.pem"`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `tls_private_key_password`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- The password to the private key (if applicable), leave blank for none&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `""`&emsp;<sub>(Bukkit)</sub>

## <small>`/listener.cfg` &gt; `tls_config` : `tls_public_chain_file`&emsp;<sub>(Bukkit)</sub></small>
**Summary:**
- The X.509 certificate chain file in PEM format, relative to the working directory.&emsp;<sub>(Bukkit)</sub>

**Defaults:**
- `"fullchain.pem"`&emsp;<sub>(Bukkit)</sub>

## `/listeners.cfg` &gt; [`listener_list`]&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Defines the list of listeners to enable Eaglercraft support on, each listener's address must be the address of a listener you've also configured in the base proxy server.&emsp;<sub>(BungeeCord)</sub>
- Defines the list of listeners to enable Eaglercraft support on, each listener's address must be the address of a listener you've also configured in the base Velocity server, unless the listener's cloning option is also enabled.&emsp;<sub>(Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `allow_cookie_revoke_query`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, If this listener should accept queries from post-u37 clients to revoke session tokens, you need to create your own BungeeCord/Velocity plugin to go with EaglerXServer that handles the EaglercraftRevokeSessionQueryEvent event it fires in order for this feature to work correctly.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `allow_motd`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, is this listener should respond to MOTD queries or not&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `allow_query`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, is this listener should respond to other query types or not&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `dual_stack`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, sets if this listener should accept both Eaglercraft WebSockets and regular Minecraft Java Edition TCP connections. The connection type is determined from the first packet, where an HTTP/1.1 request will be treated as an Eaglercraft connection, and anything else will be assumed to be an ordinary vanilla TCP connection.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `dual_stack_haproxy_detection`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if HAProxy auto-detection should be enabled on this listener, if true then the first packet will be checked for a HAProxy PROXY protocol header, and will automatically disable HAProxy for the channel if it is not present. You must enable HAProxy on the underlying BungeeCord/Velocity listener for this to work properly.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `force_disable_haproxy`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if HAProxy should be forcefully disabled when its detected on a channel from this listener. Can be useful if the underlying server does not allow disabling HAProxy on a per-listener basis (like Velocity).&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `forward_ip`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, sets if connections to this listener will use an HTTP header to forward the player's real IP address from a reverse proxy (or CloudFlare) to the BungeeCord/Velocity server. This is required for EaglerXServer's rate limiting and a lot of plugins to work correctly if they are used behind a reverse HTTP proxy or CloudFlare.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `forward_ip_header`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'X-Real-IP', sets the name of the request header that contains the player's real IP address if the forward_ip option is enabled. This option is commonly set to X-Forwarded-For or CF-Connecting-IP for a lot of server setups.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"X-Real-IP"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `forward_secret`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, sets if HTTP and WebSocket connections to this listener require a header with a secret to be accepted, can be used to prevent someone from bypassing CloudFlare, nginx, or whatever and connecting directly to the EaglerXServer listener with a fake forward IP header.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `forward_secret_file`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'eagler_forwarding.secret', sets the name of the file that contains the secret if the forward_secret option is enabled, relative to the server's working directory.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"eagler_forwarding.secret"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `forward_secret_header`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'X-Eagler-Secret', sets the name of the request header that contains the secret if the forward_secret option is enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"X-Eagler-Secret"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `inject_address`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- The address of the listener to inject into, note that if no listeners with this address are configured on the underlying proxy, then this entry will not do anything&emsp;<sub>(BungeeCord)</sub>
- The address of the listener to inject into. If this is not the same as the underlying Velocity proxy's bind address, you need to enable the velocity_clone_listener option to make the server bind the additional ports in ProxyInitializeEvent.&emsp;<sub>(Velocity)</sub>

**Defaults:**
- `"0.0.0.0:25577"`&emsp;<sub>(BungeeCord)</sub>
- `"0.0.0.0:25565"`&emsp;<sub>(Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `listener_name`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- The unique name to use when identifying this listener&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"listener0"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `ratelimit`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `disable_ratelimit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- List of IPv4 and IPv6 addresses to disable ratelimiting for, use CIDR notation to specify entire subnets, default value includes localhost to ensure ratelimiting is disabled by default when EaglerXServer is used with nginx and caddy. If forward_ip is true, the ratelimits will be applied based on the forwarded address instead of the raw socket address.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `[ "127.0.0.0/8", "::1/128" ]`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `http`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Sets ratelimit on non-WebSocket HTTP connections.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `http` : `enable`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `http` : `limit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `10`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `http` : `limit_lockout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `20`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `http` : `lockout_duration`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `300`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `http` : `period`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `30`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `ip`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Global ratelimit imposed on all connection types.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `ip` : `enable`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `ip` : `limit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `60`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `ip` : `limit_lockout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `80`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `ip` : `lockout_duration`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `1200`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `ip` : `period`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `90`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `login`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Sets ratelimit on login (server join) attempts.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `login` : `enable`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `login` : `limit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `5`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `login` : `limit_lockout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `10`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `login` : `lockout_duration`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `300`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `login` : `period`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `50`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `motd`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Sets ratelimit on MOTD query types.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `motd` : `enable`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `motd` : `limit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `5`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `motd` : `limit_lockout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `15`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `motd` : `lockout_duration`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `300`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `motd` : `period`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `30`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `query`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Sets ratelimit on all other query types.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `query` : `enable`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- If the rate limit should be enabled.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `query` : `limit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being limited.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `15`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `query` : `limit_lockout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the number of requests a single IP address can send in period seconds before being locked out.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `25`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `query` : `lockout_duration`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the total number of seconds a "lock out" should last on this limiter.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `800`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `ratelimit` : `query` : `period`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the period in the number of seconds.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `30`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `redirect_legacy_clients_to`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is '', sets the WebSocket address to redirect legacy Eaglercraft 1.5 clients to if they mistakenly try to join the server through this listener.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `""`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `request_motd_cache`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Section that defines caching hints for server lists that cache the MOTD via the 'MOTD.cache' query. As far as we know, not even the official Eaglercraft Server List on eaglercraft.com currently pays attention to these hints or attempts to cache MOTDs, so they can be ignored for now.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `request_motd_cache` : `cache_ttl`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 7200, sets how many seconds for the server list to store the MOTD in cache.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `7200`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `request_motd_cache` : `online_server_list_animation`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default is false, if the MOTD should be cached in an "animated format" that is yet to be standardized.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `request_motd_cache` : `online_server_list_portfolios`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default is true, if the MOTD should be cached when viewing more details about the specific server.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `request_motd_cache` : `online_server_list_results`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default is true, if the MOTD should be cached when shown in search results.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `request_motd_cache` : `online_server_list_trending`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default is true, if the MOTD should be cached if the server makes it to the top of the homepage.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `server_icon`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is 'server-icon.png', sets the name of the 64x64 PNG file to display as this listener's server icon, relative to the working directory.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"server-icon.png"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `server_motd`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- List of up to 2 strings, default value is '&6An EaglercraftX server', sets the contents of the listener's MOTD, which is the text displayed along with the server_icon when players add this server's listener address to their client's Multiplayer menu server list.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `[ "&6An EaglercraftX server" ]`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `show_motd_player_list`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if this listener's MOTD should list the names of online players or not&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `spoof_player_address_forwarded`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if the effective remote addresses of Eaglercraft connections in the underlying server should be spoofed to the address received via the forward_ip header. Has no effect if forward_ip is not true, if false then plugins will need to use the EaglerXServer API to determine the forwarded IP address of an Eaglercraft player.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## `/listeners.cfg` &gt; [`listener_list`] : `tls_config`&emsp;<sub>(BungeeCord, Velocity)</sub>
**Summary:**
- Settings for HTTPS (WSS) connections, HTTPS is normally handled by nginx or caddy, but if you are trying to run EaglerXServer without any reverse HTTP proxies then this can be useful.&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `enable_tls`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, sets if this listener should accept HTTPS (WSS) connections.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `require_tls`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, sets if this listener should always assume connections to be HTTPS (WSS), requires enable_tls to be true.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `tls_auto_refresh_cert`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is true, if the certificate and private key should be reloaded when changes are detected.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `tls_managed_by_external_plugin`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if the TLS certificates for this listener are managed by another plugin.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `tls_private_key_file`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- The PKCS#8 private key file in PEM format, relative to the working directory.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"privatekey.pem"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `tls_private_key_password`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- The password to the private key (if applicable), leave blank for none&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `""`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `tls_config` : `tls_public_chain_file`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- The X.509 certificate chain file in PEM format, relative to the working directory.&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"fullchain.pem"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/listeners.cfg` &gt; [`listener_list`] : `velocity_clone_listener`&emsp;<sub>(Velocity)</sub></small>
**Summary:**
- Default value is false, can be used to make Velocity bind an additional port besides the primary bind address specified in velocity.toml. This may cause problems due to the additional ports being bound early during ProxyInitializeEvent instead of waiting until after the proxy has been fully initialized.&emsp;<sub>(Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Velocity)</sub>

## `/pause_menu.cfg` &gt; `custom_images`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Section, map of custom images to display on the pause menu, paths are relative to this config file.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_achievements_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_achievements_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_backToGame_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_backToGame_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_background_all`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `"test_image.png"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_background_pause`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `"test_image.png"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_disconnect_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_disconnect_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_discord_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_discord_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_options_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_options_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_serverInfo_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_serverInfo_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_statistics_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_statistics_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_title_L`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_title_R`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_watermark_all`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `custom_images` : `icon_watermark_pause`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/pause_menu.cfg` &gt; `discord_button`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Section, can be used to turn the "Invite" (formerly "Open to LAN") button on the pause menu into a "Discord" button that players can click to join your discord server&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `discord_button` : `button_text`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the text that should be displayed on the button&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"Discord"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `discord_button` : `button_url`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Defines the URL to open when the button is pressed&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"https://invite url here"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `discord_button` : `enable_button`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets if the discord button should be enabled or not.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `enable_custom_pause_menu`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Default value is false, if pause menu customization should be enabled on supported clients or not&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/pause_menu.cfg` &gt; `server_info_button`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>
**Summary:**
- Defines properties of the "Server Info" button, which is always hidden unless pause menu customization is enabled&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `allow_embed_template_eval_macro`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If the template processor should allow the "eval" macro to be used in the server info markup file (not to be confused with the JavaScript function, although there is never a good reason to use JavaScript's eval function in your code either)&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `button_mode_embed_file`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Determines if the "Server Info" button should download the webview markup directly from EaglerXServer over WebSocket instead of loading an external URL. Cannot be used with button_mode_open_new_tab!&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `button_mode_open_new_tab`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Can be used to make the "Server Info" button act as a hyperlink that opens a URL in a new tab instead of displaying content in an embedded webview iframe in the client.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `button_text`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- The text to display on the button, useful if you want to use this feature for something other than a "Server Info" button&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"Server Info"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `enable_button`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If the button should be shown or not&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `enable_template_macros`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- if the server info markup should be processed for any eagler template macros (defined like {% arg1 `arg2` ... %})&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `enable_webview_javascript`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If the server info webview should allow JavaScript to be executed or not. This will display an "allow JavaScript" screen to your players the first time they attempt to view it.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `enable_webview_message_api`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If the server info webview has JavaScript enabled and should be permitted to open a message channel back to your server to exchange arbitrary message packets. This can be used, for example, to implement a dynamic menu on your server using JavaScript and HTML that players can access through the server info webview that integrates directly with your gamemodes.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `enable_webview_strict_csp`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- If the csp attribute on the webview iframe should be set or not for added security, beware this is not supported on all browsers and will be silently disabled when the client detects it as unsupported.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_file`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the name of the local file/template containing the markup to display in the "Server Info" webview if it is not in URL mode, relative to this config file.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"server_info.html"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_screen_title`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the title string of the screen that displays the webview.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `"Server Info"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_send_chunk_rate`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Defines how many chunks of server info data to send per 250ms when downloading the server info markup to a client.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `1`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_send_chunk_size`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Defines the size of each chunk of server info data when it is being downloaded to a client.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `24576`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## `/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_template_globals`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_template_globals` : `example_global`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Defaults:**
- `"eagler"`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/pause_menu.cfg` &gt; `server_info_button` : `server_info_embed_url`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub></small>
**Summary:**
- Sets the URL for the "Server Info" button to use if it should open a URL in a new tab or the webview instead of directly downloading the markup to display from EaglerXServer itself over the WebSocket.&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

**Defaults:**
- `""`&emsp;<sub>(Bukkit, BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `enable_supervisor`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Set to true to run the plugin in multi-proxy mode with a supervisor server (EaglerXSupervisor)&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `false`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_address`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- The ip:port combo of the supervisor server, unix sockets are also supported via unix://&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"0.0.0.0:36900"`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_brand_antagonists_ratelimit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Same as skin antagonist ratelimit, except for client brand lookup requests (default: 40)&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `40`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_connect_timeout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Connection timeout in milliseconds of the supervisor server connection (default: 30000)&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `30000`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_lookup_ignore_v2_uuid`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Workaround for NPCs, ignores v2 UUIDs in eagler skin, cape, and brand uuid lookups to avoid antagonist ratelimits (default: true)&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `true`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_read_timeout`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Read timeout in milliseconds of the supervisor server connection (default: 30000)&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `30000`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_secret`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Login secret, can be left blank, used as a last resort to protect the supervisor without a proper firewall if you're a dumbass&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `""`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_skin_antagonists_ratelimit`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- How many fake skin/cape lookup requests to nonexistant players or URLs the proxy should tolerate in a minute before rate limiting a malicious player for attempting a denial-of-service (default: 20)&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `20`&emsp;<sub>(BungeeCord, Velocity)</sub>

## <small>`/supervisor.cfg` &gt; `supervisor_unavailable_message`&emsp;<sub>(BungeeCord, Velocity)</sub></small>
**Summary:**
- Kick message displayed when a player attempts to login while the supervisor is down&emsp;<sub>(BungeeCord, Velocity)</sub>

**Defaults:**
- `"Supervisor server is down"`&emsp;<sub>(BungeeCord, Velocity)</sub>
