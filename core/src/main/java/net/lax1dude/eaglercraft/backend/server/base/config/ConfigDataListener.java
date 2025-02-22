package net.lax1dude.eaglercraft.backend.server.base.config;

import java.net.SocketAddress;
import java.util.List;

public class ConfigDataListener {

	public static class ConfigRateLimit {

		private final boolean enable;
		private final int period;
		private final int limit;
		private final int limitLockout;
		private final int lockoutDuration;
		private final List<String> exceptions;

		public ConfigRateLimit(boolean enable, int period, int limit, int limitLockout, int lockoutDuration,
				List<String> exceptions) {
			this.enable = enable;
			this.period = period;
			this.limit = limit;
			this.limitLockout = limitLockout;
			this.lockoutDuration = lockoutDuration;
			this.exceptions = exceptions;
		}

		public boolean isEnabled() {
			return enable;
		}

		public int getPeriod() {
			return period;
		}

		public int getLimit() {
			return limit;
		}

		public int getLimitLockout() {
			return limitLockout;
		}

		public int getLockoutDuration() {
			return lockoutDuration;
		}

		public List<String> getExceptions() {
			return exceptions;
		}

	}

	private final String listenerName;
	private final SocketAddress injectAddress;
	private final boolean dualStack;
	private final boolean forwardIp;
	private final String forwardIPHeader;
	private final String redirectLegacyClientsTo;
	private final String serverIcon;
	private final List<String> serverMOTD;
	private final boolean allowMOTD;
	private final boolean allowQuery;
	private final boolean showMOTDPlayerList;
	private final boolean allowCookieRevokeQuery;
	private final int motdCacheTTL;
	private final boolean motdCacheAnimation;
	private final boolean motdCacheResults;
	private final boolean motdCacheTrending;
	private final boolean motdCachePortfolios;
	private final ConfigRateLimit limitIP;
	private final ConfigRateLimit limitLogin;
	private final ConfigRateLimit limitMOTD;
	private final ConfigRateLimit limitQuery;
	private final ConfigRateLimit limitHTTP;

	public ConfigDataListener(String listenerName, SocketAddress injectAddress, boolean dualStack, boolean forwardIp,
			String forwardIPHeader, String redirectLegacyClientsTo, String serverIcon, List<String> serverMOTD,
			boolean allowMOTD, boolean allowQuery, boolean showMOTDPlayerList, boolean allowCookieRevokeQuery,
			int motdCacheTTL, boolean motdCacheAnimation, boolean motdCacheResults, boolean motdCacheTrending,
			boolean motdCachePortfolios, ConfigRateLimit limitIP, ConfigRateLimit limitLogin, ConfigRateLimit limitMOTD,
			ConfigRateLimit limitQuery, ConfigRateLimit limitHTTP) {
		this.listenerName = listenerName;
		this.injectAddress = injectAddress;
		this.dualStack = dualStack;
		this.forwardIp = forwardIp;
		this.forwardIPHeader = forwardIPHeader;
		this.redirectLegacyClientsTo = redirectLegacyClientsTo;
		this.serverIcon = serverIcon;
		this.serverMOTD = serverMOTD;
		this.allowMOTD = allowMOTD;
		this.allowQuery = allowQuery;
		this.showMOTDPlayerList = showMOTDPlayerList;
		this.allowCookieRevokeQuery = allowCookieRevokeQuery;
		this.motdCacheTTL = motdCacheTTL;
		this.motdCacheAnimation = motdCacheAnimation;
		this.motdCacheResults = motdCacheResults;
		this.motdCacheTrending = motdCacheTrending;
		this.motdCachePortfolios = motdCachePortfolios;
		this.limitIP = limitIP;
		this.limitLogin = limitLogin;
		this.limitMOTD = limitMOTD;
		this.limitQuery = limitQuery;
		this.limitHTTP = limitHTTP;
	}

	public String getListenerName() {
		return listenerName;
	}

	public SocketAddress getInjectAddress() {
		return injectAddress;
	}

	public boolean isDualStack() {
		return dualStack;
	}

	public boolean isForwardIp() {
		return forwardIp;
	}

	public String getForwardIPHeader() {
		return forwardIPHeader;
	}

	public String getRedirectLegacyClientsTo() {
		return redirectLegacyClientsTo;
	}

	public String getServerIcon() {
		return serverIcon;
	}

	public List<String> getServerMOTD() {
		return serverMOTD;
	}

	public boolean isAllowMOTD() {
		return allowMOTD;
	}

	public boolean isAllowQuery() {
		return allowQuery;
	}

	public boolean isShowMOTDPlayerList() {
		return showMOTDPlayerList;
	}

	public boolean isAllowCookieRevokeQuery() {
		return allowCookieRevokeQuery;
	}

	public int getMotdCacheTTL() {
		return motdCacheTTL;
	}

	public boolean isMotdCacheAnimation() {
		return motdCacheAnimation;
	}

	public boolean isMotdCacheResults() {
		return motdCacheResults;
	}

	public boolean isMotdCacheTrending() {
		return motdCacheTrending;
	}

	public boolean isMotdCachePortfolios() {
		return motdCachePortfolios;
	}

	public ConfigRateLimit getLimitIP() {
		return limitIP;
	}

	public ConfigRateLimit getLimitLogin() {
		return limitLogin;
	}

	public ConfigRateLimit getLimitMOTD() {
		return limitMOTD;
	}

	public ConfigRateLimit getLimitQuery() {
		return limitQuery;
	}

	public ConfigRateLimit getLimitHTTP() {
		return limitHTTP;
	}

}
