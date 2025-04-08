package net.lax1dude.eaglercraft.backend.rpc.base.remote.config;

import java.util.Collection;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;

public class ConfigDataICEServers {

	private final boolean replaceICEServerList;
	private final Collection<ICEServerEntry> iceServers;

	ConfigDataICEServers(boolean replaceICEServerList, Collection<ICEServerEntry> iceServers) {
		this.replaceICEServerList = replaceICEServerList;
		this.iceServers = iceServers;
	}

	public boolean isReplaceICEServerList() {
		return replaceICEServerList;
	}

	public Collection<ICEServerEntry> getICEServers() {
		return iceServers;
	}

}
