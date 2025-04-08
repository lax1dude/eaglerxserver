package net.lax1dude.eaglercraft.backend.server.base.config;

import java.util.List;
import java.util.Map;

import net.lax1dude.eaglercraft.backend.server.api.voice.ICEServerEntry;

public class ConfigDataRoot {

	private final ConfigDataSettings settings;
	private final Map<String, ConfigDataListener> listeners;
	private final ConfigDataSupervisor supervisor;
	private final List<ICEServerEntry> iceServers;
	private final ConfigDataPauseMenu pauseMenu;

	public ConfigDataRoot(ConfigDataSettings settings, Map<String, ConfigDataListener> listeners,
			ConfigDataSupervisor supervisor, List<ICEServerEntry> iceServers,
			ConfigDataPauseMenu pauseMenu) {
		this.settings = settings;
		this.listeners = listeners;
		this.supervisor = supervisor;
		this.iceServers = iceServers;
		this.pauseMenu = pauseMenu;
	}

	public ConfigDataSettings getSettings() {
		return settings;
	}

	public Map<String, ConfigDataListener> getListeners() {
		return listeners;
	}

	public ConfigDataSupervisor getSupervisor() {
		return supervisor;
	}

	public List<ICEServerEntry> getICEServers() {
		return iceServers;
	}

	public ConfigDataPauseMenu getPauseMenu() {
		return pauseMenu;
	}

}
