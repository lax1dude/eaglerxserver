package net.lax1dude.eaglercraft.backend.rpc.api.data;

public class WebViewStateData {

	public static WebViewStateData create(EnumWebViewState state, String channel) {
		if(state == null) {
			throw new NullPointerException("state");
		}
		if(channel == null) {
			throw new NullPointerException("channel");
		}
		return new WebViewStateData(state, channel);
	}

	private final EnumWebViewState state;
	private final String channel;

	private WebViewStateData(EnumWebViewState state, String channel) {
		this.state = state;
		this.channel = channel;
	}

	public EnumWebViewState getState() {
		return state;
	}

	public String getChannel() {
		return channel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + channel.hashCode();
		result = prime * result + state.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WebViewStateData))
			return false;
		WebViewStateData other = (WebViewStateData) obj;
		if (!channel.equals(other.channel))
			return false;
		if (state != other.state)
			return false;
		return true;
	}

}
