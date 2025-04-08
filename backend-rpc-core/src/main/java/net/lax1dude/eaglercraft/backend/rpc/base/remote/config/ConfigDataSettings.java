package net.lax1dude.eaglercraft.backend.rpc.base.remote.config;

public class ConfigDataSettings {

	public static class ConfigDataBackendRPC {

		private final int baseRequestTimeoutSec;
		private final double timeoutResolutionSec;

		ConfigDataBackendRPC(int baseRequestTimeoutSec, double timeoutResolutionSec) {
			this.baseRequestTimeoutSec = baseRequestTimeoutSec;
			this.timeoutResolutionSec = timeoutResolutionSec;
		}

		public int getBaseRequestTimeoutSec() {
			return baseRequestTimeoutSec;
		}

		public double getTimeoutResolutionSec() {
			return timeoutResolutionSec;
		}

	}

	public static class ConfigDataBackendVoice {

		private final boolean enableBackendVoiceService;

		ConfigDataBackendVoice(boolean enableBackendVoiceService) {
			this.enableBackendVoiceService = enableBackendVoiceService;
		}

		public boolean isEnableBackendVoiceService() {
			return enableBackendVoiceService;
		}

	}

	private final boolean forceModernizedChannelNames;
	private final ConfigDataBackendRPC configBackendRPC;
	private final ConfigDataBackendVoice configBackendVoice;

	ConfigDataSettings(boolean forceModernizedChannelNames, ConfigDataBackendRPC configBackendRPC,
			ConfigDataBackendVoice configBackendVoice) {
		this.forceModernizedChannelNames = forceModernizedChannelNames;
		this.configBackendRPC = configBackendRPC;
		this.configBackendVoice = configBackendVoice;
	}

	public boolean isForceModernizedChannelNames() {
		return forceModernizedChannelNames;
	}

	public ConfigDataBackendRPC getConfigBackendRPC() {
		return configBackendRPC;
	}

	public ConfigDataBackendVoice getConfigBackendVoice() {
		return configBackendVoice;
	}

}
