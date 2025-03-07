package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchCallback;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
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
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
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
			cont.complete(null, t);
			return;
		}
		cont.complete((I) res, null);
	}

	@Override
	public void setAPI(IEaglerXServerAPI<ProxiedPlayer> api) {
		this.api = api;
	}

	@Override
	public void dispatchAuthCheckRequired(IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword,
			byte[] authUsername,
			IEventDispatchCallback<IEaglercraftAuthCheckRequiredEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new BungeeAuthCheckRequiredEventImpl(api, pendingConnection, clientSolicitingPassword,
				authUsername, transformCallback(onComplete)));
	}

	@Override
	public void dispatchAuthCookieEvent(IEaglerLoginConnection loginConnection, byte[] authUsername,
			boolean cookiesEnabled, byte[] cookieData, String profileUsername, UUID profileUUID, EnumAuthType authType,
			String authMessage, String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthCookieEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new BungeeAuthCookieEventImpl(api, loginConnection, authUsername, cookiesEnabled,
				cookieData, profileUsername, profileUUID, authType, authMessage, authRequestedServer,
				transformCallback(onComplete)));
	}

	@Override
	public void dispatchAuthPasswordEvent(IEaglerLoginConnection loginConnection, byte[] authUsername,
			byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled, byte[] cookieData,
			String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthPasswordEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new BungeeAuthPasswordEventImpl(api, loginConnection, authUsername, authSaltingData,
				authPasswordData, cookiesEnabled, cookieData, profileUsername, profileUUID, authType, authMessage,
				authRequestedServer, transformCallback(onComplete)));
	}

	@Override
	public void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> onComplete) {
		eventMgr.callEvent(new BungeeClientBrandEventImpl(api, pendingConnection, transformCallback(onComplete)));
	}

	@Override
	public void dispatchInitializePlayerEvent(IEaglerPlayer<ProxiedPlayer> player,
			IEventDispatchCallback<IEaglercraftInitializePlayerEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeInitializePlayerEventImpl(api, player), onComplete);
	}

	@Override
	public void dispatchDestroyPlayerEvent(IEaglerPlayer<ProxiedPlayer> player,
			IEventDispatchCallback<IEaglercraftDestroyPlayerEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeDestroyPlayerEventImpl(api, player), onComplete);
	}

	@Override
	public void dispatchMOTDEvent(IMOTDConnection connection,
			IEventDispatchCallback<IEaglercraftMOTDEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeMOTDEventImpl(api, connection), onComplete);
	}

	@Override
	public void dispatchRegisterSkinEvent(IEaglerLoginConnection loginConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<ProxiedPlayer>> onComplete) {
		eventMgr.callEvent(
				new BungeeRegisterSkinEventImpl(api, loginConnection, delegate, transformCallback(onComplete)));
	}

	@Override
	public void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>> onComplete) {
		eventMgr.callEvent(
				new BungeeRevokeSessionQueryEventImpl(api, query, cookieData, transformCallback(onComplete)));
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<ProxiedPlayer> player, EnumVoiceState voiceStateOld,
			IVoiceChannel voiceChannelOld, EnumVoiceState voiceStateNew, IVoiceChannel voiceChannelNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeVoiceChangeEventImpl(api, player, voiceStateOld, voiceChannelOld, voiceStateNew,
				voiceChannelNew), onComplete);
	}

	@Override
	public void dispatchWebSocketOpenEvent(IEaglerConnection connection,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeWebSocketOpenEventImpl(api, connection), onComplete);
	}

	@Override
	public void dispatchWebViewChannelEvent(IEaglerPlayer<ProxiedPlayer> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeWebViewChannelEventImpl(api, player, type, channel), onComplete);
	}

	@Override
	public void dispatchWebViewMessageEvent(IEaglerPlayer<ProxiedPlayer> player, EnumMessageType type, byte[] data,
			IEventDispatchCallback<IEaglercraftWebViewMessageEvent<ProxiedPlayer>> onComplete) {
		fireSync(new BungeeWebViewMessageEventImpl(api, player, type, data), onComplete);
	}

}
