package net.lax1dude.eaglercraft.backend.server.api.voice;

public final class ICEServerEntry {

	public static ICEServerEntry create(String uri) {
		return new ICEServerEntry(validate(uri, "uri"), false, null, null);
	}

	public static ICEServerEntry create(String uri, String username, String password) {
		return new ICEServerEntry(validate(uri, "uri"), true, validate(username, "username"),
				validate(password, "password"));
	}

	private static String validate(String str, String name) {
		if(str == null) {
			throw new NullPointerException(name + " cannot be null");
		}
		if(str.indexOf(';') != -1) {
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

	public String getURI() {
		return uri;
	}

	public boolean isAuthenticated() {
		return auth;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

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
		if (!(obj instanceof ICEServerEntry))
			return false;
		ICEServerEntry other = (ICEServerEntry) obj;
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
