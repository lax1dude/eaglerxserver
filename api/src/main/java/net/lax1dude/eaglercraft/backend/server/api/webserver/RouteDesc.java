package net.lax1dude.eaglercraft.backend.server.api.webserver;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public final class RouteDesc {

	public static final RouteDesc DEFAULT_404 = new RouteDesc();
	public static final RouteDesc DEFAULT_429 = new RouteDesc();
	public static final RouteDesc DEFAULT_500 = new RouteDesc();

	private final String listenerName;
	private final String pattern;
	private final int methods;

	public static RouteDesc create(String pattern) {
		return new RouteDesc(null, pattern, EnumRequestMethod.bits);
	}

	public static RouteDesc create(String pattern, EnumRequestMethod method) {
		return new RouteDesc(null, pattern, method.bit());
	}

	public static RouteDesc create(String pattern, EnumRequestMethod... method) {
		return new RouteDesc(null, pattern, EnumRequestMethod.toBits(method));
	}

	public static RouteDesc create(String listenerName, String pattern) {
		return new RouteDesc(listenerName, pattern, EnumRequestMethod.bits);
	}

	public static RouteDesc create(String listenerName, String pattern, EnumRequestMethod method) {
		return new RouteDesc(listenerName, pattern, method.bit());
	}

	public static RouteDesc create(String listenerName, String pattern, EnumRequestMethod... method) {
		return new RouteDesc(listenerName, pattern, EnumRequestMethod.toBits(method));
	}

	private RouteDesc() {
		this(null, null, 0);
	}

	private RouteDesc(String listenerName, String pattern, int methods) {
		this.listenerName = listenerName;
		this.pattern = pattern;
		this.methods = methods;
	}

	public String getListenerName() {
		return listenerName;
	}

	public String getPattern() {
		return pattern;
	}

	public EnumRequestMethod[] getMethods() {
		return EnumRequestMethod.fromBits(methods);
	}

	public boolean isMethod(EnumRequestMethod meth) {
		return (methods & meth.bit()) != 0;
	}

}
