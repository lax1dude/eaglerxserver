package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.Player;

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

public class VelocityEventDispatchAdapter implements IEventDispatchAdapter {

	private final IEaglerXServerAPI<Player> api;
	private final EventManager eventMgr;

	public VelocityEventDispatchAdapter(IEaglerXServerAPI<Player> api, EventManager eventMgr) {
		this.api = api;
		this.eventMgr = eventMgr;
	}

	private static class DispatchCallbackWrapper<I, T extends I> implements Consumer<T>, Function<Throwable, Void> {

		private final IEventDispatchCallback<I> event;

		protected DispatchCallbackWrapper(IEventDispatchCallback<I> cb) {
			this.event = cb;
		}

		@Override
		public Void apply(Throwable t) {
			event.complete(null, t);
			return null;
		}

		@Override
		public void accept(T t) {
			event.complete(t, null);
		}

	}

	private <I, T extends I> void fire(T event, IEventDispatchCallback<I> cont) {
		if(cont != null) {
			DispatchCallbackWrapper<I, T> cb = new DispatchCallbackWrapper<>(cont);
			eventMgr.fire(event).thenAccept(cb).exceptionally(cb);
		}else {
			eventMgr.fireAndForget(event);
		}
	}

	@Override
	public void dispatchAuthCheckRequired(IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword,
			byte[] authUsername, IEventDispatchCallback<IEaglercraftAuthCheckRequiredEvent<?, ?>> onComplete) {
		fire(new VelocityAuthCheckRequiredEventImpl(api, pendingConnection, clientSolicitingPassword, authUsername),
				onComplete);
	}

	@Override
	public void dispatchAuthCookieEvent(IEaglerPendingConnection pendingConnection, byte[] authUsername,
			boolean cookiesEnabled, byte[] cookieData, String profileUsername, UUID profileUUID, EnumAuthType authType,
			String authMessage, String authRequestedServer, IEventDispatchCallback<IEaglercraftAuthCookieEvent<?, ?>> onComplete) {
		fire(new VelocityAuthCookieEventImpl(api, pendingConnection, authUsername, cookiesEnabled, cookieData,
				profileUsername, profileUUID, authType, authMessage, authRequestedServer), onComplete);
	}

	@Override
	public void dispatchAuthPasswordEvent(IEaglerPendingConnection pendingConnection, byte[] authUsername,
			byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled, byte[] cookieData,
			String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer, IEventDispatchCallback<IEaglercraftAuthPasswordEvent<?, ?>> onComplete) {
		fire(new VelocityAuthPasswordEventImpl(api, pendingConnection, authUsername, authSaltingData, authPasswordData,
				cookiesEnabled, cookieData, profileUsername, profileUUID, authType, authMessage, authRequestedServer),
				onComplete);
	}

	@Override
	public void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<?, ?>> onComplete) {
		fire(new VelocityClientBrandEventImpl(api, pendingConnection), onComplete);
	}

	@Override
	public void dispatchMOTDEvent(IMOTDConnection connection, IEventDispatchCallback<IEaglercraftMOTDEvent<?>> onComplete) {
		fire(new VelocityMOTDEventImpl(api, connection), onComplete);
	}

	@Override
	public void dispatchRegisterSkinEvent(IEaglerPendingConnection pendingConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<?>> onComplete) {
		fire(new VelocityRegisterSkinEventImpl(api, pendingConnection, delegate), onComplete);
	}

	@Override
	public void dispatchRegisterCapeEvent(IEaglerPendingConnection pendingConnection, IRegisterCapeDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterCapeEvent<?>> onComplete) {
		fire(new VelocityRegisterCapeEventImpl(api, pendingConnection, delegate), onComplete);
	}

	@Override
	public void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<?>> onComplete) {
		fire(new VelocityRevokeSessionQueryEventImpl(api, query, cookieData), onComplete);
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<?> player, EnumVoiceState voiceStateOld,
			IVoiceChannel voiceChannelOld, EnumVoiceState voiceStateNew, IVoiceChannel voiceChannelNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<?>> onComplete) {
		fire(new VelocityVoiceChangeEventImpl(api, (IEaglerPlayer<Player>) player, voiceStateOld, voiceChannelOld,
				voiceStateNew, voiceChannelNew), onComplete);
	}

	@Override
	public void dispatchWebSocketOpenEvent(IWebSocketOpenDelegate delegate,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<?>> onComplete) {
		fire(new VelocityWebSocketOpenEventImpl(api, delegate), onComplete);
	}

	@Override
	public void dispatchWebViewChannelEvent(IEaglerPlayer<?> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<?>> onComplete) {
		fire(new VelocityWebViewChannelEventImpl(api, (IEaglerPlayer<Player>) player, type, channel), onComplete);
	}

	@Override
	public void dispatchWebViewMessageEvent(IEaglerPlayer<?> player, EnumMessageType type, byte[] data,
			IEventDispatchCallback<IEaglercraftWebViewMessageEvent<?>> onComplete) {
		fire(new VelocityWebViewMessageEventImpl(api, (IEaglerPlayer<Player>) player, type, data), onComplete);
	}

}
