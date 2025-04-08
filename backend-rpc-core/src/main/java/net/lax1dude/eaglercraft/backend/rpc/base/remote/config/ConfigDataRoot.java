package net.lax1dude.eaglercraft.backend.rpc.base.remote.config;

public class ConfigDataRoot {

	private final ConfigDataSettings settings;
	private final ConfigDataICEServers iceServers;

	ConfigDataRoot(ConfigDataSettings settings, ConfigDataICEServers iceServers) {
		this.settings = settings;
		this.iceServers = iceServers;
	}

	public ConfigDataSettings getConfigSettings() {
		return settings;
	}

	public ConfigDataICEServers getConfigICEServers() {
		return iceServers;
	}

}
