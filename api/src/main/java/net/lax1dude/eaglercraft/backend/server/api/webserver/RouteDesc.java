package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public final class RouteDesc {

	@Nonnull
	public static final RouteDesc DEFAULT_404 = new RouteDesc();

	@Nonnull
	public static final RouteDesc DEFAULT_429 = new RouteDesc();

	@Nonnull
	public static final RouteDesc DEFAULT_500 = new RouteDesc();

	@Nonnull
	public static RouteDesc create(@Nonnull String pattern) {
		return new RouteDesc(null, pattern, EnumRequestMethod.bits);
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String pattern, @Nonnull EnumRequestMethod method) {
		return new RouteDesc(null, pattern, method.bit());
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String pattern, @Nonnull EnumRequestMethod... method) {
		return new RouteDesc(null, pattern, EnumRequestMethod.toBits(method));
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String listenerName, @Nonnull String pattern) {
		return new RouteDesc(listenerName, pattern, EnumRequestMethod.bits);
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String listenerName, @Nonnull String pattern,
			@Nonnull EnumRequestMethod method) {
		return new RouteDesc(listenerName, pattern, method.bit());
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String listenerName, @Nonnull String pattern,
			@Nonnull EnumRequestMethod... method) {
		return new RouteDesc(listenerName, pattern, EnumRequestMethod.toBits(method));
	}

	private final String listenerName;
	private final String pattern;
	private final int methods;

	private RouteDesc() {
		this(null, null, 0);
	}

	private RouteDesc(String listenerName, String pattern, int methods) {
		this.listenerName = listenerName;
		this.pattern = pattern;
		this.methods = methods;
	}

	public boolean isAllListeners() {
		return listenerName == null;
	}

	@Nullable
	public String getListenerName() {
		return listenerName;
	}

	@Nonnull
	public String getPattern() {
		return pattern;
	}

	@Nonnull
	public EnumRequestMethod[] getMethods() {
		return EnumRequestMethod.fromBits(methods);
	}

	public boolean isMethod(@Nonnull EnumRequestMethod meth) {
		return (methods & meth.bit()) != 0;
	}

	public boolean isAllMethods() {
		return methods == EnumRequestMethod.bits;
	}

	public int hashCode() {
		int i = 0;
		if(listenerName != null) i += listenerName.hashCode();
		i *= 31;
		if(pattern != null) i += pattern.hashCode();
		i *= 31;
		return i + methods;
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof RouteDesc r) && Objects.equals(r.pattern, pattern)
				&& Objects.equals(r.listenerName, listenerName) && r.methods == methods);
	}

}
