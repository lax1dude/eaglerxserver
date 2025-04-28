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

package net.lax1dude.eaglercraft.backend.server.api.voice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ICEServerEntry {

	@Nonnull
	public static ICEServerEntry create(@Nonnull String uri) {
		return new ICEServerEntry(validate(uri, "uri"), false, null, null);
	}

	@Nonnull
	public static ICEServerEntry create(@Nonnull String uri, @Nonnull String username, @Nonnull String password) {
		return new ICEServerEntry(validate(uri, "uri"), true, validate(username, "username"),
				validate(password, "password"));
	}

	private static String validate(String str, String name) {
		if (str == null) {
			throw new NullPointerException(name + " cannot be null");
		}
		if (str.indexOf(';') != -1) {
			throw new IllegalArgumentException("Illegal semicolon in " + name);
		}
		return str;
	}

	private final String uri;
	private final boolean auth;
	private final String username;
	private final String password;

	private ICEServerEntry(String uri, boolean auth, String username, String password) {
		this.uri = uri;
		this.auth = auth;
		this.username = username;
		this.password = password;
	}

	@Nonnull
	public String getURI() {
		return uri;
	}

	public boolean isAuthenticated() {
		return auth;
	}

	@Nullable
	public String getUsername() {
		return username;
	}

	@Nullable
	public String getPassword() {
		return password;
	}

	@Nonnull
	@Override
	public String toString() {
		return auth ? (uri + ';' + username + ';' + password) : uri;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + (auth ? 1231 : 1237);
		result = 31 * result + uri.hashCode();
		result = 31 * result + (auth ? password.hashCode() : 0);
		result = 31 * result + (auth ? username.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ICEServerEntry other))
			return false;
		if (auth != other.auth)
			return false;
		if (!uri.equals(other.uri))
			return false;
		if (auth) {
			if (!password.equals(other.password))
				return false;
			if (!username.equals(other.username))
				return false;
		}
		return true;
	}

}
