package net.lax1dude.eaglercraft.backend.rpc.api.data;

import java.util.Set;

public final class WebViewStateData {

	public static WebViewStateData create(boolean webViewAllowed, boolean channelAllowed,
			Set<String> openChannels) {
		if(openChannels == null) {
			throw new NullPointerException("openChannels");
		}
		return new WebViewStateData(webViewAllowed, channelAllowed, openChannels);
	}

	private final boolean webViewAllowed;
	private final boolean channelAllowed;
	private final Set<String> openChannels;

	private WebViewStateData(boolean webViewAllowed, boolean channelAllowed,
			Set<String> openChannels) {
		this.webViewAllowed = webViewAllowed;
		this.channelAllowed = channelAllowed;
		this.openChannels = openChannels;
	}

	public boolean isWebViewAllowed() {
		return webViewAllowed;
	}

	public boolean isChannelAllowed() {
		return channelAllowed;
	}

	public Set<String> getOpenChannels() {
		return openChannels;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + (channelAllowed ? 1231 : 1237);
		result = 31 * result + openChannels.hashCode();
		result = 31 * result + (webViewAllowed ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WebViewStateData))
			return false;
		WebViewStateData other = (WebViewStateData) obj;
		if (channelAllowed != other.channelAllowed)
			return false;
		if (!openChannels.equals(other.openChannels))
			return false;
		if (webViewAllowed != other.webViewAllowed)
			return false;
		return true;
	}

}
