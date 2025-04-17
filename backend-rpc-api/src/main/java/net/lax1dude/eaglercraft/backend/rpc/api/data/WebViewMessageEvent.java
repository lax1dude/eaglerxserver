package net.lax1dude.eaglercraft.backend.rpc.api.data;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;

public final class WebViewMessageEvent implements IRPCEvent {

	@Nonnull
	public static WebViewMessageEvent string(@Nonnull String channelName, @Nonnull byte[] messageContent) {
		if(channelName == null) {
			throw new NullPointerException("channelName");
		}
		if(messageContent == null) {
			throw new NullPointerException("messageContent");
		}
		return new WebViewMessageEvent(channelName, EnumMessageType.STRING, messageContent);
	}

	@Nonnull
	public static WebViewMessageEvent binary(@Nonnull String channelName, @Nonnull byte[] messageContent) {
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

	@Nonnull
	public String getChannelName() {
		return channelName;
	}

	@Nonnull
	public EnumMessageType getMessageType() {
		return messageType;
	}

	@Nonnull
	public byte[] getContentBytes() {
		return messageContent;
	}

	@Nonnull
	public String getContentStr() {
		if(asString == null) {
			asString = new String(messageContent, StandardCharsets.UTF_8);
		}
		return asString;
	}

	@Nonnull
	@Override
	public EnumSubscribeEvents getEventType() {
		return EnumSubscribeEvents.EVENT_WEBVIEW_MESSAGE;
	}

}
