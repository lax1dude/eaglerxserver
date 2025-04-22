/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

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
