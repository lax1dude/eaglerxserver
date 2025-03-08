package net.lax1dude.eaglercraft.backend.server.api.webserver;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public final class RouteDesc {

	public static final RouteDesc DEFAULT_404 = new RouteDesc();
	public static final RouteDesc DEFAULT_429 = new RouteDesc();
	public static final RouteDesc DEFAULT_500 = new RouteDesc();

	public static RouteDesc create(String pattern) {
		return null;
	}

	public static RouteDesc create(String pattern, EnumRequestMethod method) {
		return null;
	}

	public static RouteDesc create(String pattern, EnumRequestMethod... method) {
		return null;
	}

	public static RouteDesc create(String listenerName, String pattern) {
		return null;
	}

	public static RouteDesc create(String listenerName, String pattern, EnumRequestMethod method) {
		return null;
	}

	public static RouteDesc create(String listenerName, String pattern, EnumRequestMethod... method) {
		return null;
	}

	private RouteDesc() {
	}

}
