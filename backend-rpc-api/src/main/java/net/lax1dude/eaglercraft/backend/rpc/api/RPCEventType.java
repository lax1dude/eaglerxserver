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
