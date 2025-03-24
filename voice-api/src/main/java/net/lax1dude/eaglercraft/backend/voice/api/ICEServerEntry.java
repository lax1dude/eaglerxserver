package net.lax1dude.eaglercraft.backend.voice.api;

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

}
