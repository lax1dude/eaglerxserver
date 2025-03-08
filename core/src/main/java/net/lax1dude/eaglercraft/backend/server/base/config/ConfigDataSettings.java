package net.lax1dude.eaglercraft.backend.server.base.config;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConfigDataSettings {

	public static class ConfigDataProtocols {

		private final int minMinecraftProtocol;
		private final int maxMinecraftProtocol;
		private final boolean eaglerXRewindAllowed;
		private final boolean protocolLegacyAllowed;
		private final boolean protocolV3Allowed;
		private final boolean protocolV4Allowed;
		private final boolean protocolV5Allowed;
		private final int minEaglerProtocol;
		private final int maxEaglerProtocol;

		public ConfigDataProtocols(int minMinecraftProtocol, int maxMinecraftProtocol, boolean eaglerXRewindAllowed,
				boolean protocolLegacyAllowed, boolean protocolV3Allowed, boolean protocolV4Allowed,
				boolean protocolV5Allowed) {
			this.minMinecraftProtocol = minMinecraftProtocol;
			this.maxMinecraftProtocol = maxMinecraftProtocol;
			this.eaglerXRewindAllowed = eaglerXRewindAllowed;
			this.protocolLegacyAllowed = protocolLegacyAllowed;
			this.protocolV3Allowed = protocolV3Allowed;
			this.protocolV4Allowed = protocolV4Allowed;
			this.protocolV5Allowed = protocolV5Allowed;
			if(protocolLegacyAllowed) {
				minEaglerProtocol = 1;
			}else if(protocolV3Allowed) {
				minEaglerProtocol = 3;
			}else if(protocolV4Allowed) {
				minEaglerProtocol = 4;
			}else if(protocolV5Allowed) {
				minEaglerProtocol = 5;
			}else {
				minEaglerProtocol = Integer.MAX_VALUE;
			}
			if(protocolV5Allowed) {
				maxEaglerProtocol = 5;
			}else if(protocolV4Allowed) {
				maxEaglerProtocol = 4;
			}else if(protocolV3Allowed) {
				maxEaglerProtocol = 3;
			}else if(protocolLegacyAllowed) {
				maxEaglerProtocol = 1;
			}else {
				maxEaglerProtocol = Integer.MIN_VALUE;
			}
		}

		public int getMinMinecraftProtocol() {
			return minMinecraftProtocol;
		}

		public int getMaxMinecraftProtocol() {
			return maxMinecraftProtocol;
		}

		public boolean isEaglerXRewindAllowed() {
			return eaglerXRewindAllowed;
		}

		public boolean isProtocolLegacyAllowed() {
			return protocolLegacyAllowed;
		}

		public boolean isProtocolV3Allowed() {
			return protocolV3Allowed;
		}

		public boolean isProtocolV4Allowed() {
			return protocolV4Allowed;
		}

		public boolean isProtocolV5Allowed() {
			return protocolV5Allowed;
		}

		public int getMinEaglerProtocol() {
			return minEaglerProtocol;
		}

		public int getMaxEaglerProtocol() {
			return maxEaglerProtocol;
		}

		public boolean isEaglerHandshakeSupported(int vers) {
			switch(vers) {
			case 1:
			case 2:
				return protocolLegacyAllowed;
			case 3:
				return protocolV3Allowed;
			case 4:
				return protocolV4Allowed;
			case 5:
				return protocolV5Allowed;
			default:
				return false;
			}
		}

		public boolean isEaglerProtocolSupported(int vers) {
			switch(vers) {
			case 3:
				return protocolLegacyAllowed || protocolV3Allowed;
			case 4:
				return protocolV4Allowed;
			case 5:
				return protocolV5Allowed;
			default:
				return false;
			}
		}

		public boolean isMinecraftProtocolSupported(int vers) {
			return minMinecraftProtocol >= vers && maxMinecraftProtocol <= vers;
		}

	}

	public static class ConfigDataSkinService {

		private final int skinLookupRatelimitPlayer;
		private final boolean downloadVanillaSkinsToClients;
		private final Set<String> validSkinDownloadURLs;
		private final int skinDownloadRatelimit;
		private final int skinDownloadRatelimitGlobal;
		private final String skinCacheDBURI;
		private final String skinCacheDriverClass;
		private final String skinCacheDriverPath;
		private final boolean skinCacheSQLiteCompatible;
		private final int skinCacheThreadCount;
		private final int skinCacheCompressionLevel;
		private final int skinCacheMemoryKeepSeconds;
		private final int skinCacheMemoryMaxObjects;
		private final int skinCacheDiskKeepObjectsDays;
		private final int skinCacheDiskMaxObjects;
		private final int skinCacheAntagonistsRatelimit;
		private final boolean enableFNAWSkinModelsGlobal;
		private final Set<String> enableFNAWSkinModelsOnServers;

		public ConfigDataSkinService(int skinLookupRatelimitPlayer, boolean downloadVanillaSkinsToClients,
				Set<String> validSkinDownloadURLs, int skinDownloadRatelimit, int skinDownloadRatelimitGlobal,
				String skinCacheDBURI, String skinCacheDriverClass, String skinCacheDriverPath,
				boolean skinCacheSQLiteCompatible, int skinCacheThreadCount, int skinCacheCompressionLevel,
				int skinCacheMemoryKeepSeconds, int skinCacheMemoryMaxObjects, int skinCacheDiskKeepObjectsDays,
				int skinCacheDiskMaxObjects, int skinCacheAntagonistsRatelimit, boolean enableFNAWSkinModelsGlobal,
				Set<String> enableFNAWSkinModelsOnServers) {
			this.skinLookupRatelimitPlayer = skinLookupRatelimitPlayer;
			this.downloadVanillaSkinsToClients = downloadVanillaSkinsToClients;
			this.validSkinDownloadURLs = validSkinDownloadURLs;
			this.skinDownloadRatelimit = skinDownloadRatelimit;
			this.skinDownloadRatelimitGlobal = skinDownloadRatelimitGlobal;
			this.skinCacheDBURI = skinCacheDBURI;
			this.skinCacheDriverClass = skinCacheDriverClass;
			this.skinCacheDriverPath = skinCacheDriverPath;
			this.skinCacheSQLiteCompatible = skinCacheSQLiteCompatible;
			this.skinCacheThreadCount = skinCacheThreadCount;
			this.skinCacheCompressionLevel = skinCacheCompressionLevel;
			this.skinCacheMemoryKeepSeconds = skinCacheMemoryKeepSeconds;
			this.skinCacheMemoryMaxObjects = skinCacheMemoryMaxObjects;
			this.skinCacheDiskKeepObjectsDays = skinCacheDiskKeepObjectsDays;
			this.skinCacheDiskMaxObjects = skinCacheDiskMaxObjects;
			this.skinCacheAntagonistsRatelimit = skinCacheAntagonistsRatelimit;
			this.enableFNAWSkinModelsGlobal = enableFNAWSkinModelsGlobal;
			this.enableFNAWSkinModelsOnServers = enableFNAWSkinModelsOnServers;
		}

		public int getSkinLookupRatelimitPlayer() {
			return skinLookupRatelimitPlayer;
		}

		public boolean isDownloadVanillaSkinsToClients() {
			return downloadVanillaSkinsToClients;
		}

		public Set<String> getValidSkinDownloadURLs() {
			return validSkinDownloadURLs;
		}

		public int getSkinDownloadRatelimit() {
			return skinDownloadRatelimit;
		}

		public int getSkinDownloadRatelimitGlobal() {
			return skinDownloadRatelimitGlobal;
		}

		public String getSkinCacheDBURI() {
			return skinCacheDBURI;
		}

		public String getSkinCacheDriverClass() {
			return skinCacheDriverClass;
		}

		public String getSkinCacheDriverPath() {
			return skinCacheDriverPath;
		}

		public boolean isSkinCacheSQLiteCompatible() {
			return skinCacheSQLiteCompatible;
		}

		public int getSkinCacheThreadCount() {
			return skinCacheThreadCount;
		}

		public int getSkinCacheCompressionLevel() {
			return skinCacheCompressionLevel;
		}

		public int getSkinCacheMemoryKeepSeconds() {
			return skinCacheMemoryKeepSeconds;
		}

		public int getSkinCacheMemoryMaxObjects() {
			return skinCacheMemoryMaxObjects;
		}

		public int getSkinCacheDiskKeepObjectsDays() {
			return skinCacheDiskKeepObjectsDays;
		}

		public int getSkinCacheDiskMaxObjects() {
			return skinCacheDiskMaxObjects;
		}

		public int getSkinCacheAntagonistsRatelimit() {
			return skinCacheAntagonistsRatelimit;
		}

		public boolean isEnableFNAWSkinModelsGlobal() {
			return enableFNAWSkinModelsGlobal;
		}

		public Set<String> getEnableFNAWSkinModelsOnServers() {
			return enableFNAWSkinModelsOnServers;
		}

	}

	public static class ConfigDataVoiceService {

		private final boolean enableVoiceChatGlobal;
		private final Set<String> enableFNAWSkinsOnServers;
		private final boolean separateVoiceChannelsPerServer;

		public ConfigDataVoiceService(boolean enableVoiceChatGlobal, Set<String> enableFNAWSkinsOnServers,
				boolean separateVoiceChannelsPerServer) {
			this.enableVoiceChatGlobal = enableVoiceChatGlobal;
			this.enableFNAWSkinsOnServers = enableFNAWSkinsOnServers;
			this.separateVoiceChannelsPerServer = separateVoiceChannelsPerServer;
		}

		public boolean isEnableVoiceChatGlobal() {
			return enableVoiceChatGlobal;
		}

		public Set<String> getEnableFNAWSkinsOnServers() {
			return enableFNAWSkinsOnServers;
		}

		public boolean isSeparateVoiceChannelsPerServer() {
			return separateVoiceChannelsPerServer;
		}

	}

	public static class ConfigDataUpdateService {

		private final boolean enableUpdateSystem;
		private final boolean discardLoginPacketCerts;
		private final int certPacketDataRateLimit;
		private final boolean enableEagcertFolder;
		private final boolean downloadLatestCerts;
		private final List<String> downloadCertsFrom;
		private final int checkForUpdateEvery;

		public ConfigDataUpdateService(boolean enableUpdateSystem, boolean discardLoginPacketCerts,
				int certPacketDataRateLimit, boolean enableEagcertFolder, boolean downloadLatestCerts,
				List<String> downloadCertsFrom, int checkForUpdateEvery) {
			this.enableUpdateSystem = enableUpdateSystem;
			this.discardLoginPacketCerts = discardLoginPacketCerts;
			this.certPacketDataRateLimit = certPacketDataRateLimit;
			this.enableEagcertFolder = enableEagcertFolder;
			this.downloadLatestCerts = downloadLatestCerts;
			this.downloadCertsFrom = downloadCertsFrom;
			this.checkForUpdateEvery = checkForUpdateEvery;
		}

		public boolean isEnableUpdateSystem() {
			return enableUpdateSystem;
		}

		public boolean isDiscardLoginPacketCerts() {
			return discardLoginPacketCerts;
		}

		public int getCertPacketDataRateLimit() {
			return certPacketDataRateLimit;
		}

		public boolean isEnableEagcertFolder() {
			return enableEagcertFolder;
		}

		public boolean isDownloadLatestCerts() {
			return downloadLatestCerts;
		}

		public List<String> getDownloadCertsFrom() {
			return downloadCertsFrom;
		}

		public int getCheckForUpdateEvery() {
			return checkForUpdateEvery;
		}

	}

	private final String serverName;
	private final UUID serverUUID;
	private final String serverUUIDString;
	private final int eaglerLoginTimeout;
	private final int httpMaxInitialLineLength;
	private final int httpMaxHeaderSize;
	private final int httpMaxChunkSize;
	private final int httpMaxContentLength;
	private final int httpWebSocketCompressionLevel;
	private final int httpWebSocketFragmentSize;
	private final int httpWebSocketMaxFrameLength;
	private final boolean httpAllowKeepAlive;
	private final int tlsCertRefreshRate;
	private final boolean enableAuthenticationEvents;
	private final boolean enableBackendRPCAPI;
	private final boolean useModernizedChannelNames;
	private final String eaglerPlayersVanillaSkin;
	private final boolean enableIsEaglerPlayerPropery;
	private final int protocolV4DefragSendDelay;
	private final ConfigDataProtocols protocols;
	private final ConfigDataSkinService skinService;
	private final ConfigDataVoiceService voiceService;
	private final ConfigDataUpdateService updateService;

	public ConfigDataSettings(String serverName, UUID serverUUID, int eaglerLoginTimeout, int httpMaxInitialLineLength,
			int httpMaxHeaderSize, int httpMaxChunkSize, int httpMaxContentLength, int httpWebSocketCompressionLevel,
			int httpWebSocketFragmentSize, int httpWebSocketMaxFrameLength, boolean httpAllowKeepAlive,
			int tlsCertRefreshRate, boolean enableAuthenticationEvents, boolean enableBackendRPCAPI,
			boolean useModernizedChannelNames, String eaglerPlayersVanillaSkin, boolean enableIsEaglerPlayerPropery,
			int protocolV4DefragSendDelay, ConfigDataProtocols protocols, ConfigDataSkinService skinService,
			ConfigDataVoiceService voiceService, ConfigDataUpdateService updateService) {
		this.serverName = serverName;
		this.serverUUID = serverUUID;
		this.serverUUIDString = serverUUID.toString();
		this.eaglerLoginTimeout = eaglerLoginTimeout;
		this.httpMaxInitialLineLength = httpMaxInitialLineLength;
		this.httpMaxHeaderSize = httpMaxHeaderSize;
		this.httpMaxChunkSize = httpMaxChunkSize;
		this.httpMaxContentLength = httpMaxContentLength;
		this.httpWebSocketCompressionLevel = httpWebSocketCompressionLevel;
		this.httpWebSocketFragmentSize = httpWebSocketFragmentSize;
		this.httpWebSocketMaxFrameLength = httpWebSocketMaxFrameLength;
		this.httpAllowKeepAlive = httpAllowKeepAlive;
		this.tlsCertRefreshRate = tlsCertRefreshRate;
		this.enableAuthenticationEvents = enableAuthenticationEvents;
		this.enableBackendRPCAPI = enableBackendRPCAPI;
		this.useModernizedChannelNames = useModernizedChannelNames;
		this.eaglerPlayersVanillaSkin = eaglerPlayersVanillaSkin;
		this.enableIsEaglerPlayerPropery = enableIsEaglerPlayerPropery;
		this.protocolV4DefragSendDelay = protocolV4DefragSendDelay;
		this.protocols = protocols;
		this.skinService = skinService;
		this.voiceService = voiceService;
		this.updateService = updateService;
	}

	public String getServerName() {
		return serverName;
	}

	public UUID getServerUUID() {
		return serverUUID;
	}

	public String getServerUUIDString() {
		return serverUUIDString;
	}

	public int getEaglerLoginTimeout() {
		return eaglerLoginTimeout;
	}

	public int getHTTPMaxInitialLineLength() {
		return httpMaxInitialLineLength;
	}

	public int getHTTPMaxHeaderSize() {
		return httpMaxHeaderSize;
	}

	public int getHTTPMaxChunkSize() {
		return httpMaxChunkSize;
	}

	public int getHTTPMaxContentLength() {
		return httpMaxContentLength;
	}

	public int getHTTPWebSocketCompressionLevel() {
		return httpWebSocketCompressionLevel;
	}

	public int getHTTPWebSocketFragmentSize() {
		return httpWebSocketFragmentSize;
	}

	public int getHTTPWebSocketMaxFrameLength() {
		return httpWebSocketMaxFrameLength;
	}

	public boolean isHTTPAllowKeepAlive() {
		return httpAllowKeepAlive;
	}

	public int getTLSCertRefreshRate() {
		return tlsCertRefreshRate;
	}

	public boolean isEnableAuthenticationEvents() {
		return enableAuthenticationEvents;
	}

	public boolean isEnableBackendRPCAPI() {
		return enableBackendRPCAPI;
	}

	public boolean isUseModernizedChannelNames() {
		return useModernizedChannelNames;
	}

	public String getEaglerPlayersVanillaSkin() {
		return eaglerPlayersVanillaSkin;
	}

	public boolean isEnableIsEaglerPlayerProperty() {
		return enableIsEaglerPlayerPropery;
	}

	public int getProtocolV4DefragSendDelay() {
		return protocolV4DefragSendDelay;
	}

	public ConfigDataProtocols getProtocols() {
		return protocols;
	}

	public ConfigDataSkinService getSkinService() {
		return skinService;
	}

	public ConfigDataVoiceService getVoiceService() {
		return voiceService;
	}

	public ConfigDataUpdateService getUpdateService() {
		return updateService;
	}

}
