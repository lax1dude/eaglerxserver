package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
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

public class VelocityEventDispatchAdapter implements IEventDispatchAdapter<Player, Component> {

	private IEaglerXServerAPI<Player> api;
	private final EventManager eventMgr;

	public VelocityEventDispatchAdapter(EventManager eventMgr) {
		this.eventMgr = eventMgr;
	}

	private static class DispatchCallbackWrapper<I, T extends I> implements Consumer<T>, Function<Throwable, T> {

		private final IEventDispatchCallback<I> event;

		protected DispatchCallbackWrapper(IEventDispatchCallback<I> cb) {
			this.event = cb;
		}

		@Override
		public T apply(Throwable t) {
			event.complete(null, t);
			return null;
		}

		@Override
		public void accept(T t) {
			if(t != null) {
				event.complete(t, null);
			}
		}

	}

	private <I, T extends I> void fire(T event, IEventDispatchCallback<I> cont) {
		if (cont != null) {
			DispatchCallbackWrapper<I, T> cb = new DispatchCallbackWrapper<>(cont);
			eventMgr.fire(event).exceptionally(cb).thenAccept(cb);
		} else {
			eventMgr.fireAndForget(event);
		}
	}

	@Override
	public void setAPI(IEaglerXServerAPI<Player> api) {
		this.api = api;
	}

	@Override
	public void dispatchAuthCheckRequired(IEaglerPendingConnection pendingConnection, boolean clientSolicitingPassword,
			byte[] authUsername,
			IEventDispatchCallback<IEaglercraftAuthCheckRequiredEvent<Player, Component>> onComplete) {
		fire(new VelocityAuthCheckRequiredEventImpl(api, pendingConnection, clientSolicitingPassword, authUsername),
				onComplete);
	}

	@Override
	public void dispatchAuthCookieEvent(IEaglerLoginConnection loginConnection, byte[] authUsername,
			boolean nicknameSelectionEnabled, boolean cookiesEnabled, byte[] cookieData, String requestedUsername,
			String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthCookieEvent<Player, Component>> onComplete) {
		fire(new VelocityAuthCookieEventImpl(api, loginConnection, authUsername, nicknameSelectionEnabled,
				cookiesEnabled, cookieData, requestedUsername, profileUsername, profileUUID, authType, authMessage,
				authRequestedServer), onComplete);
	}

	@Override
	public void dispatchAuthPasswordEvent(IEaglerLoginConnection loginConnection, byte[] authUsername,
			boolean nicknameSelectionEnabled, byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled,
			byte[] cookieData, String requestedUsername, String profileUsername, UUID profileUUID,
			EnumAuthType authType, String authMessage, String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthPasswordEvent<Player, Component>> onComplete) {
		fire(new VelocityAuthPasswordEventImpl(api, loginConnection, authUsername, nicknameSelectionEnabled,
				authSaltingData, authPasswordData, cookiesEnabled, cookieData, requestedUsername, profileUsername,
				profileUUID, authType, authMessage, authRequestedServer), onComplete);
	}

	@Override
	public void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<Player, Component>> onComplete) {
		fire(new VelocityClientBrandEventImpl(api, pendingConnection), onComplete);
	}

	@Override
	public void dispatchInitializePlayerEvent(IEaglerPlayer<Player> player,
			IEventDispatchCallback<IEaglercraftInitializePlayerEvent<Player>> onComplete) {
		fire(new VelocityInitializePlayerEventImpl(api, player), onComplete);
	}

	@Override
	public void dispatchDestroyPlayerEvent(IEaglerPlayer<Player> player,
			IEventDispatchCallback<IEaglercraftDestroyPlayerEvent<Player>> onComplete) {
		fire(new VelocityDestroyPlayerEventImpl(api, player), onComplete);
	}

	@Override
	public void dispatchMOTDEvent(IMOTDConnection connection,
			IEventDispatchCallback<IEaglercraftMOTDEvent<Player>> onComplete) {
		fire(new VelocityMOTDEventImpl(api, connection), onComplete);
	}

	@Override
	public void dispatchRegisterSkinEvent(IEaglerLoginConnection loginConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<Player>> onComplete) {
		fire(new VelocityRegisterSkinEventImpl(api, loginConnection, delegate), onComplete);
	}

	@Override
	public void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<Player>> onComplete) {
		fire(new VelocityRevokeSessionQueryEventImpl(api, query, cookieData), onComplete);
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<Player> player, EnumVoiceState voiceStateOld,
			IVoiceChannel voiceChannelOld, EnumVoiceState voiceStateNew, IVoiceChannel voiceChannelNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<Player>> onComplete) {
		fire(new VelocityVoiceChangeEventImpl(api, player, voiceStateOld, voiceChannelOld, voiceStateNew,
				voiceChannelNew), onComplete);
	}

	@Override
	public void dispatchWebSocketOpenEvent(IEaglerConnection connection,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<Player>> onComplete) {
		fire(new VelocityWebSocketOpenEventImpl(api, connection), onComplete);
	}

	@Override
	public void dispatchWebViewChannelEvent(IEaglerPlayer<Player> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<Player>> onComplete) {
		fire(new VelocityWebViewChannelEventImpl(api, player, type, channel), onComplete);
	}

	@Override
	public void dispatchWebViewMessageEvent(IEaglerPlayer<Player> player, EnumMessageType type, byte[] data,
			IEventDispatchCallback<IEaglercraftWebViewMessageEvent<Player>> onComplete) {
		fire(new VelocityWebViewMessageEventImpl(api, player, type, data), onComplete);
	}

}
