package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent.EnumAuthType;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterCapeEvent;
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
import net.lax1dude.eaglercraft.backend.server.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.event.IEventDispatchCallback;
import net.lax1dude.eaglercraft.backend.server.event.IRegisterCapeDelegate;
import net.lax1dude.eaglercraft.backend.server.event.IRegisterSkinDelegate;
import net.lax1dude.eaglercraft.backend.server.event.IWebSocketOpenDelegate;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeEventDispatchAdapter implements IEventDispatchAdapter {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final PluginManager eventMgr;

	public BungeeEventDispatchAdapter(IEaglerXServerAPI<ProxiedPlayer> api, PluginManager eventMgr) {
		this.api = api;
		this.eventMgr = eventMgr;
	}

	private static final Callback<Object> NOP = (a, b) -> {};

	private static <T> Callback<T> transformCallback(IEventDispatchCallback<T> obj) {
		if(obj != null) {
			return obj::complete;
		}else {
			return (Callback<T>) NOP;
		}
	}

	private <I, T extends Event> void fireSync(T event, IEventDispatchCallback<I> cont) {
		T res;
		try {
			res = eventMgr.callEvent(event);
		}catch(Throwable t) {
			cont.complete(null, t);
			return;
		}
		cont.complete((I)res, null);
	}

	@Override
	public void dispatchAuthCheckRequired(IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword,
			byte[] authUsername, IEventDispatchCallback<IEaglercraftAuthCheckRequiredEvent<?, ?>> onComplete) {
		eventMgr.callEvent(new BungeeAuthCheckRequiredEventImpl(api, pendingConnection, clientSolicitingPassword,
				authUsername, transformCallback(onComplete)));
	}

	@Override
	public void dispatchAuthCookieEvent(IEaglerPendingConnection pendingConnection, byte[] authUsername,
			boolean cookiesEnabled, byte[] cookieData, String profileUsername, UUID profileUUID, EnumAuthType authType,
			String authMessage, String authRequestedServer, IEventDispatchCallback<IEaglercraftAuthCookieEvent<?, ?>> onComplete) {
		eventMgr.callEvent(new BungeeAuthCookieEventImpl(api, pendingConnection, authUsername, cookiesEnabled,
				cookieData, profileUsername, profileUUID, authType, authMessage, authRequestedServer,
				transformCallback(onComplete)));
	}

	@Override
	public void dispatchAuthPasswordEvent(IEaglerPendingConnection pendingConnection, byte[] authUsername,
			byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled, byte[] cookieData,
			String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer, IEventDispatchCallback<IEaglercraftAuthPasswordEvent<?, ?>> onComplete) {
		eventMgr.callEvent(new BungeeAuthPasswordEventImpl(api, pendingConnection, authUsername, authSaltingData,
				authPasswordData, cookiesEnabled, cookieData, profileUsername, profileUUID, authType, authMessage,
				authRequestedServer, transformCallback(onComplete)));
	}

	@Override
	public void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<?, ?>> onComplete) {
		eventMgr.callEvent(new BungeeClientBrandEventImpl(api, pendingConnection, transformCallback(onComplete)));
	}

	@Override
	public void dispatchMOTDEvent(IMOTDConnection connection, IEventDispatchCallback<IEaglercraftMOTDEvent<?>> onComplete) {
		fireSync(new BungeeMOTDEventImpl(api, connection), onComplete);
	}

	@Override
	public void dispatchRegisterSkinEvent(IEaglerPendingConnection pendingConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<?>> onComplete) {
		eventMgr.callEvent(new BungeeRegisterSkinEventImpl(api, pendingConnection, delegate, transformCallback(onComplete)));
	}

	@Override
	public void dispatchRegisterCapeEvent(IEaglerPendingConnection pendingConnection, IRegisterCapeDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterCapeEvent<?>> onComplete) {
		eventMgr.callEvent(new BungeeRegisterCapeEventImpl(api, pendingConnection, delegate, transformCallback(onComplete)));
	}

	@Override
	public void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<?>> onComplete) {
		eventMgr.callEvent(new BungeeRevokeSessionQueryEventImpl(api, query, cookieData, transformCallback(onComplete)));
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<?> player, EnumVoiceState voiceStateOld,
			IVoiceChannel voiceChannelOld, EnumVoiceState voiceStateNew, IVoiceChannel voiceChannelNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<?>> onComplete) {
		fireSync(new BungeeVoiceChangeEventImpl(api, (IEaglerPlayer<ProxiedPlayer>) player, voiceStateOld,
				voiceChannelOld, voiceStateNew, voiceChannelNew), onComplete);
	}

	@Override
	public void dispatchWebSocketOpenEvent(IWebSocketOpenDelegate delegate,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<?>> onComplete) {
		fireSync(new BungeeWebSocketOpenEventImpl(api, delegate), onComplete);
	}

	@Override
	public void dispatchWebViewChannelEvent(IEaglerPlayer<?> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<?>> onComplete) {
		fireSync(new BungeeWebViewChannelEventImpl(api, (IEaglerPlayer<ProxiedPlayer>) player, type, channel), onComplete);
	}

	@Override
	public void dispatchWebViewMessageEvent(IEaglerPlayer<?> player, EnumMessageType type, byte[] data,
			IEventDispatchCallback<IEaglercraftWebViewMessageEvent<?>> onComplete) {
		fireSync(new BungeeWebViewMessageEventImpl(api, (IEaglerPlayer<ProxiedPlayer>) player, type, data), onComplete);
	}

}
