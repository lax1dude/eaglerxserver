/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

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
		if(pattern == null) throw new NullPointerException("pattern");
		return new RouteDesc(null, pattern, EnumRequestMethod.bits);
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String pattern, @Nonnull EnumRequestMethod method) {
		if(pattern == null) throw new NullPointerException("pattern");
		if(method == null) throw new NullPointerException("method");
		return new RouteDesc(null, pattern, method.bit());
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String pattern, @Nonnull EnumRequestMethod... method) {
		if(pattern == null) throw new NullPointerException("pattern");
		return new RouteDesc(null, pattern, EnumRequestMethod.toBits(method));
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String listenerName, @Nonnull String pattern) {
		if(listenerName == null) throw new NullPointerException("listenerName");
		if(pattern == null) throw new NullPointerException("pattern");
		return new RouteDesc(listenerName, pattern, EnumRequestMethod.bits);
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String listenerName, @Nonnull String pattern,
			@Nonnull EnumRequestMethod method) {
		if(listenerName == null) throw new NullPointerException("listenerName");
		if(pattern == null) throw new NullPointerException("pattern");
		if(method == null) throw new NullPointerException("method");
		return new RouteDesc(listenerName, pattern, method.bit());
	}

	@Nonnull
	public static RouteDesc create(@Nonnull String listenerName, @Nonnull String pattern,
			@Nonnull EnumRequestMethod... method) {
		if(listenerName == null) throw new NullPointerException("listenerName");
		if(pattern == null) throw new NullPointerException("pattern");
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
