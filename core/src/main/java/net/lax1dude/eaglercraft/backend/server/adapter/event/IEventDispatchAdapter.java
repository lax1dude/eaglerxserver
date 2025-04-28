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

package net.lax1dude.eaglercraft.backend.server.adapter.event;

import java.util.Map;
import java.util.UUID;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent.EnumEventType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent.EnumMessageType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftLoginEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;

public interface IEventDispatchAdapter<PlayerObject, ComponentObject> {

	void setAPI(IEaglerXServerAPI<PlayerObject> api);

	void dispatchAuthCheckRequired(IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword,
			byte[] authUsername,
			IEventDispatchCallback<IEaglercraftAuthCheckRequiredEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchAuthCookieEvent(IEaglerLoginConnection pendingConnection, byte[] authUsername,
			boolean nicknameSelectionEnabled, boolean cookiesEnabled, byte[] cookieData, String requestedUsername,
			String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthCookieEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchAuthPasswordEvent(IEaglerLoginConnection pendingConnection, byte[] authUsername,
			boolean nicknameSelectionEnabled, byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled,
			byte[] cookieData, String requestedUsername, String profileUsername, UUID profileUUID,
			EnumAuthType authType, String authMessage, String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthPasswordEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchLoginEvent(IEaglerLoginConnection loginConnection, boolean redirectSupport, String requestedServer,
			IEventDispatchCallback<IEaglercraftLoginEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchInitializePlayerEvent(IEaglerPlayer<PlayerObject> player, Map<String, byte[]> extraProfileData,
			IEventDispatchCallback<IEaglercraftInitializePlayerEvent<PlayerObject>> onComplete);

	void dispatchDestroyPlayerEvent(IEaglerPlayer<PlayerObject> player,
			IEventDispatchCallback<IEaglercraftDestroyPlayerEvent<PlayerObject>> onComplete);

	void dispatchMOTDEvent(IMOTDConnection connection,
			IEventDispatchCallback<IEaglercraftMOTDEvent<PlayerObject>> onComplete);

	void dispatchRegisterSkinEvent(IEaglerLoginConnection pendingConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<PlayerObject>> onComplete);

	void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<PlayerObject>> onComplete);

	void dispatchVoiceChangeEvent(IEaglerPlayer<PlayerObject> player, EnumVoiceState voiceStateOld,
			EnumVoiceState voiceStateNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<PlayerObject>> onComplete);

	void dispatchWebSocketOpenEvent(IEaglerConnection delegate, FullHttpRequest request,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<PlayerObject>> onComplete);

	void dispatchWebViewChannelEvent(IEaglerPlayer<PlayerObject> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<PlayerObject>> onComplete);

	void dispatchWebViewMessageEvent(IEaglerPlayer<PlayerObject> player, String channel, EnumMessageType type,
			byte[] data, IEventDispatchCallback<IEaglercraftWebViewMessageEvent<PlayerObject>> onComplete);

}
