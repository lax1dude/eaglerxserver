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
		private final int skinCacheKeepObjectsDays;
		private final int skinCacheMaxObjects;
		private final int skinCacheAntagonistsRatelimit;
		private final boolean enableIsEaglerPlayerProperty;
		private final boolean enableFNAWSkinModelsGlobal;
		private final Set<String> enableFNAWSkinModelsOnServers;

		public ConfigDataSkinService(int skinLookupRatelimitPlayer, boolean downloadVanillaSkinsToClients,
				Set<String> validSkinDownloadURLs, int skinDownloadRatelimit, int skinDownloadRatelimitGlobal,
				String skinCacheDBURI, String skinCacheDriverClass, String skinCacheDriverPath,
				int skinCacheKeepObjectsDays, int skinCacheMaxObjects, int skinCacheAntagonistsRatelimit,
				boolean enableIsEaglerPlayerProperty, boolean enableFNAWSkinModelsGlobal,
				Set<String> enableFNAWSkinModelsOnServers) {
			this.skinLookupRatelimitPlayer = skinLookupRatelimitPlayer;
			this.downloadVanillaSkinsToClients = downloadVanillaSkinsToClients;
			this.validSkinDownloadURLs = validSkinDownloadURLs;
			this.skinDownloadRatelimit = skinDownloadRatelimit;
			this.skinDownloadRatelimitGlobal = skinDownloadRatelimitGlobal;
			this.skinCacheDBURI = skinCacheDBURI;
			this.skinCacheDriverClass = skinCacheDriverClass;
			this.skinCacheDriverPath = skinCacheDriverPath;
			this.skinCacheKeepObjectsDays = skinCacheKeepObjectsDays;
			this.skinCacheMaxObjects = skinCacheMaxObjects;
			this.skinCacheAntagonistsRatelimit = skinCacheAntagonistsRatelimit;
			this.enableIsEaglerPlayerProperty = enableIsEaglerPlayerProperty;
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

		public int getSkinCacheKeepObjectsDays() {
			return skinCacheKeepObjectsDays;
		}

		public int getSkinCacheMaxObjects() {
			return skinCacheMaxObjects;
		}

		public int getSkinCacheAntagonistsRatelimit() {
			return skinCacheAntagonistsRatelimit;
		}

		public boolean isEnableIsEaglerPlayerProperty() {
			return enableIsEaglerPlayerProperty;
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
	private final int httpMaxInitialLineLength;
	private final int httpMaxHeaderSize;
	private final int httpMaxChunkSize;
	private final int httpMaxContentLength;
	private final int httpWebSocketCompressionLevel;
	private final int httpWebSocketFragmentSize;
	private final int httpWebSocketMaxFrameLength;
	private final int tlsCertRefreshRate;
	private final boolean enableAuthenticationEvents;
	private final boolean enableBackendRPCAPI;
	private final boolean useModernizedChannelNames;
	private final String eaglerPlayersVanillaSkin;
	private final int protocolV4DefragSendDelay;
	private final ConfigDataProtocols protocols;
	private final ConfigDataSkinService skinService;
	private final ConfigDataVoiceService voiceService;
	private final ConfigDataUpdateService updateService;

	public ConfigDataSettings(String serverName, UUID serverUUID, int httpMaxInitialLineLength, int httpMaxHeaderSize,
			int httpMaxChunkSize, int httpMaxContentLength, int httpWebSocketCompressionLevel,
			int httpWebSocketFragmentSize, int httpWebSocketMaxFrameLength, int tlsCertRefreshRate,
			boolean enableAuthenticationEvents, boolean enableBackendRPCAPI, boolean useModernizedChannelNames,
			String eaglerPlayersVanillaSkin, int protocolV4DefragSendDelay, ConfigDataProtocols protocols,
			ConfigDataSkinService skinService, ConfigDataVoiceService voiceService,
			ConfigDataUpdateService updateService) {
		this.serverName = serverName;
		this.serverUUID = serverUUID;
		this.httpMaxInitialLineLength = httpMaxInitialLineLength;
		this.httpMaxHeaderSize = httpMaxHeaderSize;
		this.httpMaxChunkSize = httpMaxChunkSize;
		this.httpMaxContentLength = httpMaxContentLength;
		this.httpWebSocketCompressionLevel = httpWebSocketCompressionLevel;
		this.httpWebSocketFragmentSize = httpWebSocketFragmentSize;
		this.httpWebSocketMaxFrameLength = httpWebSocketMaxFrameLength;
		this.tlsCertRefreshRate = tlsCertRefreshRate;
		this.enableAuthenticationEvents = enableAuthenticationEvents;
		this.enableBackendRPCAPI = enableBackendRPCAPI;
		this.useModernizedChannelNames = useModernizedChannelNames;
		this.eaglerPlayersVanillaSkin = eaglerPlayersVanillaSkin;
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
