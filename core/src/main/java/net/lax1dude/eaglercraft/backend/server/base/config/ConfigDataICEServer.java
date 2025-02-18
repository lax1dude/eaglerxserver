package net.lax1dude.eaglercraft.backend.server.base.config;

public class ConfigDataICEServer {

	private final boolean hasPassword;
	private final String url;
	private final String username;
	private final String password;

	public ConfigDataICEServer(String url) {
		this.hasPassword = false;
		this.url = url;
		this.username = null;
		this.password = null;
	}

	public ConfigDataICEServer(String url, String username, String password) {
		this.hasPassword = true;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public boolean hasPassword() {
		return hasPassword;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
