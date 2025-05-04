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

package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.util.Map;
import java.util.UUID;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchCallback;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftLoginEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftRegisterSkinEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftRevokeSessionQueryEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
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
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent.EnumEventType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent.EnumMessageType;
import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeEventDispatchAdapter implements IEventDispatchAdapter<ProxiedPlayer, BaseComponent> {

	private IEaglerXServerAPI<ProxiedPlayer> api;
	private final PluginManager eventMgr;

	public BungeeEventDispatchAdapter(PluginManager eventMgr) {
		this.eventMgr = eventMgr;
	}

	private static final Callback<Object> NOP = (a, b) -> {
	};

	private static <T> Callback<T> transformCallback(IEventDispatchCallback<T> obj) {
		if (obj != null) {
			return obj::complete;
		} else {
			return (Callback<T>) NOP;
		}
	}

	private <I, T extends Event> void fireSync(T event, IEventDispatchCallback<I> cont) {
		T res;
		try {
			res = eventMgr.callEvent(event);
		} catch (Throwable t) {
			if (cont != null) {
				cont.complete(null, t);
			}
			return;
		}
		if (cont != null) {
			cont.complete((I) res, null);
		}
	}

	@Override
	public void setAPI(IEaglerXServerAPI<ProxiedPlayer> api) {
		this.api = api;
	}

	@Override
	public void dispatchAuthCheckRequired(IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword,
			byte[] authUsername,
			IEventDispatchCallback<IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new EaglercraftAuthCheckRequiredEvent(api, pendingConnection, clientSolicitingPassword,
				authUsername, transformCallback(onComplete)));
	}

	@Override
	public void dispatchAuthCookieEvent(IEaglerLoginConnection loginConnection, byte[] authUsername,
			boolean nicknameSelectionEnabled, boolean cookiesEnabled, byte[] cookieData, String requestedUsername,
			String profileUsername, UUID profileUUID, byte authType, String authMessage, String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new EaglercraftAuthCookieEvent(api, loginConnection, authUsername, nicknameSelectionEnabled,
				cookiesEnabled, cookieData, requestedUsername, profileUsername, profileUUID, authType, authMessage,
				authRequestedServer, transformCallback(onComplete)));
	}

	@Override
	public void dispatchAuthPasswordEvent(IEaglerLoginConnection loginConnection, byte[] authUsername,
			boolean nicknameSelectionEnabled, byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled,
			byte[] cookieData, String requestedUsername, String profileUsername, UUID profileUUID, byte authType,
			String authMessage, String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthPasswordEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new EaglercraftAuthPasswordEvent(api, loginConnection, authUsername,
				nicknameSelectionEnabled, authSaltingData, authPasswordData, cookiesEnabled, cookieData,
				requestedUsername, profileUsername, profileUUID, authType, authMessage, authRequestedServer,
				transformCallback(onComplete)));
	}

	@Override
	public void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new EaglercraftClientBrandEvent(api, pendingConnection, transformCallback(onComplete)));
	}

	@Override
	public void dispatchLoginEvent(IEaglerLoginConnection loginConnection, boolean redirectSupport,
			String requestedServer,
			IEventDispatchCallback<IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new EaglercraftLoginEvent(api, loginConnection, redirectSupport, requestedServer,
				transformCallback(onComplete)));
	}

	@Override
	public void dispatchInitializePlayerEvent(IEaglerPlayer<ProxiedPlayer> player, Map<String, byte[]> extraProfileData,
			IEventDispatchCallback<IEaglercraftInitializePlayerEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftInitializePlayerEvent(api, player, extraProfileData), onComplete);
	}

	@Override
	public void dispatchDestroyPlayerEvent(IEaglerPlayer<ProxiedPlayer> player,
			IEventDispatchCallback<IEaglercraftDestroyPlayerEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftDestroyPlayerEvent(api, player), onComplete);
	}

	@Override
	public void dispatchMOTDEvent(IMOTDConnection connection,
			IEventDispatchCallback<IEaglercraftMOTDEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftMOTDEvent(api, connection), onComplete);
	}

	@Override
	public void dispatchRegisterSkinEvent(IEaglerLoginConnection loginConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> onComplete) {
		eventMgr.callEvent(
				new EaglercraftRegisterSkinEvent(api, loginConnection, delegate, transformCallback(onComplete)));
	}

	@Override
	public void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>> onComplete) {
		eventMgr.callEvent(
				new EaglercraftRevokeSessionQueryEvent(api, query, cookieData, transformCallback(onComplete)));
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<ProxiedPlayer> player, EnumVoiceState voiceStateOld,
			EnumVoiceState voiceStateNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftVoiceChangeEvent(api, player, voiceStateOld, voiceStateNew), onComplete);
	}

	@Override
	public void dispatchWebSocketOpenEvent(IEaglerConnection connection, FullHttpRequest request,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftWebSocketOpenEvent(api, connection, request), onComplete);
	}

	@Override
	public void dispatchWebViewChannelEvent(IEaglerPlayer<ProxiedPlayer> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftWebViewChannelEvent(api, player, type, channel), onComplete);
	}

	@Override
	public void dispatchWebViewMessageEvent(IEaglerPlayer<ProxiedPlayer> player, String channel, EnumMessageType type,
			byte[] data, IEventDispatchCallback<IEaglercraftWebViewMessageEvent<ProxiedPlayer>> onComplete) {
		fireSync(new EaglercraftWebViewMessageEvent(api, player, channel, type, data), onComplete);
	}

}
