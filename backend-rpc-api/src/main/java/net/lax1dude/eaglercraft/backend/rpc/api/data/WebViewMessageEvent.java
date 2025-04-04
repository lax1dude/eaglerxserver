package net.lax1dude.eaglercraft.backend.rpc.api.data;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;

public final class WebViewMessageEvent implements IRPCEvent {

	public static WebViewMessageEvent string(String channelName, byte[] messageContent) {
		if(channelName == null) {
			throw new NullPointerException("channelName");
		}
		if(messageContent == null) {
			throw new NullPointerException("messageContent");
		}
		return new WebViewMessageEvent(channelName, EnumMessageType.STRING, messageContent);
	}

	public static WebViewMessageEvent binary(String channelName, byte[] messageContent) {
		if(channelName == null) {
			throw new NullPointerException("channelName");
		}
		if(messageContent == null) {
			throw new NullPointerException("messageContent");
		}
		return new WebViewMessageEvent(channelName, EnumMessageType.BINARY, messageContent);
	}

	private final String channelName;
	private final EnumMessageType messageType;
	private final byte[] messageContent;
	private String asString;

	private WebViewMessageEvent(String channelName, EnumMessageType messageType, byte[] messageContent) {
		this.channelName = channelName;
		this.messageType = messageType;
		this.messageContent = messageContent;
	}

	public String getChannelName() {
		return channelName;
	}

	public EnumMessageType getMessageType() {
		return messageType;
	}

	public byte[] getContentBytes() {
		return messageType == EnumMessageType.BINARY ? messageContent : null;
	}

	public String getContentStr() {
		if(messageType == EnumMessageType.STRING) {
			if(asString == null) {
				asString = new String(messageContent, StandardCharsets.UTF_8);
			}
			return asString;
		}else {
			return null;
		}
	}

	@Override
	public EnumSubscribeEvents getEventType() {
		return EnumSubscribeEvents.EVENT_WEBVIEW_MESSAGE;
	}

}
