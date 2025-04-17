package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.data.VoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent;

public final class RPCEventType<T extends IRPCEvent> {

	@Nonnull
	public static final RPCEventType<WebViewOpenCloseEvent> EVENT_WEBVIEW_OPEN_CLOSE = new RPCEventType<>(
			EnumSubscribeEvents.EVENT_WEBVIEW_OPEN_CLOSE);

	@Nonnull
	public static final RPCEventType<WebViewMessageEvent> EVENT_WEBVIEW_MESSAGE = new RPCEventType<>(
			EnumSubscribeEvents.EVENT_WEBVIEW_MESSAGE);

	@Nonnull
	public static final RPCEventType<VoiceChangeEvent> EVENT_VOICE_CHANGE = new RPCEventType<>(
			EnumSubscribeEvents.EVENT_VOICE_CHANGE);

	private final EnumSubscribeEvents eventType;

	private RPCEventType(EnumSubscribeEvents eventType) {
		this.eventType = eventType;
	}

	@Nonnull
	public EnumSubscribeEvents getEventType() {
		return eventType;
	}

	@Nonnull
	public String toString() {
		return eventType.toString();
	}

	public int hashCode() {
		return eventType.hashCode();
	}

	public boolean equals(Object o) {
		return o == this;
	}

}
