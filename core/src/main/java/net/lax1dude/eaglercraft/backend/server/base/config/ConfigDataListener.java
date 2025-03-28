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

		public ConfigRateLimit(boolean enable, int period, int limit, int limitLockout, int lockoutDuration) {
			this.enable = enable;
			this.period = period;
			this.limit = limit;
			this.limitLockout = limitLockout;
			this.lockoutDuration = lockoutDuration;
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

	}

	private final String listenerName;
	private final SocketAddress injectAddress;
	private final boolean dualStack;
	private final boolean forwardIP;
	private final String forwardIPHeader;
	private final boolean forwardSecret;
	private final String forwardSecretHeader;
	private final String forwardSecretFile;
	private final String forwardSecretValue;
	private final boolean spoofPlayerAddressForwarded;
	private final boolean enableTLS;
	private final boolean requireTLS;
	private final boolean tlsManagedByExternalPlugin;
	private final String tlsPublicChainFile;
	private final String tlsPrivateKeyFile;
	private final String tlsPrivateKeyPassword;
	private final boolean tlsAutoRefreshCert;
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
	private final boolean motdCacheAny;
	private final ConfigRateLimit limitIP;
	private final ConfigRateLimit limitLogin;
	private final ConfigRateLimit limitMOTD;
	private final ConfigRateLimit limitQuery;
	private final ConfigRateLimit limitHTTP;
	private final List<String> limitExclusions;

	public ConfigDataListener(String listenerName, SocketAddress injectAddress, boolean dualStack, boolean forwardIp,
			String forwardIPHeader, boolean forwardSecret, String forwardSecretHeader, String forwardSecretFile,
			String forwardSecretValue, boolean spoofPlayerAddressForwarded, boolean enableTLS, boolean requireTLS,
			boolean tlsManagedByExternalPlugin, String tlsPublicChainFile, String tlsPrivateKeyFile,
			String tlsPrivateKeyPassword, boolean tlsAutoRefreshCert, String redirectLegacyClientsTo, String serverIcon,
			List<String> serverMOTD, boolean allowMOTD, boolean allowQuery, boolean showMOTDPlayerList,
			boolean allowCookieRevokeQuery, int motdCacheTTL, boolean motdCacheAnimation, boolean motdCacheResults,
			boolean motdCacheTrending, boolean motdCachePortfolios, ConfigRateLimit limitIP, ConfigRateLimit limitLogin,
			ConfigRateLimit limitMOTD, ConfigRateLimit limitQuery, ConfigRateLimit limitHTTP,
			List<String> limitExclusions) {
		this.listenerName = listenerName;
		this.injectAddress = injectAddress;
		this.dualStack = dualStack;
		this.forwardIP = forwardIp;
		this.forwardIPHeader = forwardIPHeader;
		this.forwardSecret = forwardSecret;
		this.forwardSecretHeader = forwardSecretHeader;
		this.forwardSecretFile = forwardSecretFile;
		this.forwardSecretValue = forwardSecretValue;
		this.spoofPlayerAddressForwarded = spoofPlayerAddressForwarded;
		this.enableTLS = enableTLS;
		this.requireTLS = requireTLS;
		this.tlsManagedByExternalPlugin = tlsManagedByExternalPlugin;
		this.tlsPublicChainFile = tlsPublicChainFile;
		this.tlsPrivateKeyFile = tlsPrivateKeyFile;
		this.tlsPrivateKeyPassword = tlsPrivateKeyPassword;
		this.tlsAutoRefreshCert = tlsAutoRefreshCert;
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
		this.motdCacheAny = motdCacheAnimation || motdCacheResults || motdCacheTrending || motdCachePortfolios;
		this.limitIP = limitIP;
		this.limitLogin = limitLogin;
		this.limitMOTD = limitMOTD;
		this.limitQuery = limitQuery;
		this.limitHTTP = limitHTTP;
		this.limitExclusions = limitExclusions;
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

	public boolean isForwardIP() {
		return forwardIP;
	}

	public String getForwardIPHeader() {
		return forwardIPHeader;
	}

	public boolean isForwardSecret() {
		return forwardSecret;
	}

	public String getForwardSecretHeader() {
		return forwardSecretHeader;
	}

	public String getForwardSecretFile() {
		return forwardSecretFile;
	}

	public String getForwardSecretValue() {
		return forwardSecretValue;
	}

	public boolean isSpoofPlayerAddressForwarded() {
		return spoofPlayerAddressForwarded;
	}

	public boolean isEnableTLS() {
		return enableTLS;
	}

	public boolean isRequireTLS() {
		return requireTLS;
	}

	public boolean isTLSManagedByExternalPlugin() {
		return tlsManagedByExternalPlugin;
	}

	public boolean isTLSAutoRefreshCert() {
		return tlsAutoRefreshCert;
	}

	public String getTLSPublicChainFile() {
		return tlsPublicChainFile;
	}

	public String getTLSPrivateKeyFile() {
		return tlsPrivateKeyFile;
	}

	public String getTLSPrivateKeyPassword() {
		return tlsPrivateKeyPassword;
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

	public boolean isMotdCacheAny() {
		return motdCacheAny;
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

	public List<String> getLimitExclusions() {
		return limitExclusions;
	}

}
