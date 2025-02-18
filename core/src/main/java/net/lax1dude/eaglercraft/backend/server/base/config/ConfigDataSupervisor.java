package net.lax1dude.eaglercraft.backend.server.base.config;

import java.net.SocketAddress;

public class ConfigDataSupervisor {

	private final boolean enableSupervisor;
	private final SocketAddress supervisorAddress;
	private final String supervisorSecret;
	private final int supervisorConnectTimeout;
	private final int supervisorReadTimeout;
	private final String supervisorUnavailableMessage;
	private final int supervisorSkinAntagonistsRatelimit;
	private final int supervisorBrandAntagonistsRatelimit;
	private final boolean supervisorLookupIgnoreV2UUID;

	public ConfigDataSupervisor(boolean enableSupervisor, SocketAddress supervisorAddress, String supervisorSecret,
			int supervisorConnectTimeout, int supervisorReadTimeout, String supervisorUnavailableMessage,
			int supervisorSkinAntagonistsRatelimit, int supervisorBrandAntagonistsRatelimit,
			boolean supervisorLookupIgnoreV2UUID) {
		this.enableSupervisor = enableSupervisor;
		this.supervisorAddress = supervisorAddress;
		this.supervisorSecret = supervisorSecret;
		this.supervisorConnectTimeout = supervisorConnectTimeout;
		this.supervisorReadTimeout = supervisorReadTimeout;
		this.supervisorUnavailableMessage = supervisorUnavailableMessage;
		this.supervisorSkinAntagonistsRatelimit = supervisorSkinAntagonistsRatelimit;
		this.supervisorBrandAntagonistsRatelimit = supervisorBrandAntagonistsRatelimit;
		this.supervisorLookupIgnoreV2UUID = supervisorLookupIgnoreV2UUID;
	}

	public boolean isEnableSupervisor() {
		return enableSupervisor;
	}

	public SocketAddress getSupervisorAddress() {
		return supervisorAddress;
	}

	public String getSupervisorSecret() {
		return supervisorSecret;
	}

	public int getSupervisorConnectTimeout() {
		return supervisorConnectTimeout;
	}

	public int getSupervisorReadTimeout() {
		return supervisorReadTimeout;
	}

	public String getSupervisorUnavailableMessage() {
		return supervisorUnavailableMessage;
	}

	public int getSupervisorSkinAntagonistsRatelimit() {
		return supervisorSkinAntagonistsRatelimit;
	}

	public int getSupervisorBrandAntagonistsRatelimit() {
		return supervisorBrandAntagonistsRatelimit;
	}

	public boolean isSupervisorLookupIgnoreV2UUID() {
		return supervisorLookupIgnoreV2UUID;
	}

}
