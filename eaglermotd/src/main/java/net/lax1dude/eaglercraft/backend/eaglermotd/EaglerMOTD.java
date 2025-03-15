package net.lax1dude.eaglercraft.backend.eaglermotd;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

public class EaglerMOTD<PlayerObject> {

	private final IEaglerMOTDPlatform<PlayerObject> platform;
	private IEaglerXServerAPI<PlayerObject> server;

	public EaglerMOTD(IEaglerMOTDPlatform<PlayerObject> platform) {
		this.platform = platform;
	}

	public IEaglerMOTDPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	public void onEnable(IEaglerXServerAPI<PlayerObject> server) {
		this.server = server;
	}

	public void onDisable(IEaglerXServerAPI<PlayerObject> server) {
		
	}

	public void onMOTD(IEaglercraftMOTDEvent<PlayerObject> event) {
		
	}

	public IEaglerMOTDLogger logger() {
		return platform.logger();
	}

}
