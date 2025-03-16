package net.lax1dude.eaglercraft.backend.eaglerfilter.base;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;

public class EaglerFilter<PlayerObject, ComponentObject> {

	private final IEaglerFilterPlatform<PlayerObject, ComponentObject> platform;

	public EaglerFilter(IEaglerFilterPlatform<PlayerObject, ComponentObject> platform) {
		this.platform = platform;
	}

	public void onEnable(IEaglerXServerAPI<PlayerObject> server) {
		platform.setOnWebSocketOpen(this::onWebSocketOpen);
		platform.setOnClientBrand(this::onClientBrand);
	}

	public void onDisable(IEaglerXServerAPI<PlayerObject> server) {
		platform.setOnWebSocketOpen(null);
		platform.setOnClientBrand(null);
	}

	public void onWebSocketOpen(IEaglercraftWebSocketOpenEvent<PlayerObject> event) {
		
	}

	public void onClientBrand(IEaglercraftClientBrandEvent<PlayerObject, ComponentObject> event) {
		
	}

}
