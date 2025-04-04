package net.lax1dude.eaglercraft.backend.rpc.api.data;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;

public final class WebViewOpenCloseEvent implements IRPCEvent {

	public static WebViewOpenCloseEvent create(String channelName, boolean opened) {
		return new WebViewOpenCloseEvent(channelName, opened);
	}

	private final String channelName;
	private final boolean opened;

	private WebViewOpenCloseEvent(String channelName, boolean opened) {
		this.channelName = channelName;
		this.opened = opened;
	}

	public String getChannelName() {
		return channelName;
	}

	public boolean isOpened() {
		return opened;
	}

	@Override
	public EnumSubscribeEvents getEventType() {
		return EnumSubscribeEvents.EVENT_WEBVIEW_OPEN_CLOSE;
	}

}
