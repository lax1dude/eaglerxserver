package net.lax1dude.eaglercraft.backend.rpc.api.data;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;

public final class WebViewOpenCloseEvent implements IRPCEvent {

	@Nonnull
	public static WebViewOpenCloseEvent create(@Nonnull String channelName, boolean opened) {
		if(channelName == null) {
			throw new NullPointerException("channelName");
		}
		return new WebViewOpenCloseEvent(channelName, opened);
	}

	private final String channelName;
	private final boolean opened;

	private WebViewOpenCloseEvent(String channelName, boolean opened) {
		this.channelName = channelName;
		this.opened = opened;
	}

	@Nonnull
	public String getChannelName() {
		return channelName;
	}

	public boolean isOpened() {
		return opened;
	}

	@Nonnull
	@Override
	public EnumSubscribeEvents getEventType() {
		return EnumSubscribeEvents.EVENT_WEBVIEW_OPEN_CLOSE;
	}

}
