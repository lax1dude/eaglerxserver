package net.lax1dude.eaglercraft.backend.rpc.api;

import net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent;

public final class RPCEventType<T extends IRPCEvent> {

	public static final RPCEventType<WebViewOpenCloseEvent> EVENT_WEBVIEW_OPEN_CLOSE = new RPCEventType<>(
			EnumSubscribeEvents.EVENT_WEBVIEW_OPEN_CLOSE);

	public static final RPCEventType<WebViewOpenCloseEvent> EVENT_WEBVIEW_MESSAGE = new RPCEventType<>(
			EnumSubscribeEvents.EVENT_WEBVIEW_MESSAGE);

	public static final RPCEventType<WebViewOpenCloseEvent> EVENT_TOGGLE_VOICE = new RPCEventType<>(
			EnumSubscribeEvents.EVENT_VOICE_CHANGE);

	private final EnumSubscribeEvents eventType;

	private RPCEventType(EnumSubscribeEvents eventType) {
		this.eventType = eventType;
	}

	public EnumSubscribeEvents getEventType() {
		return eventType;
	}

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
