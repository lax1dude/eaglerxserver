package net.lax1dude.eaglercraft.backend.server.adapter.event;

import java.util.UUID;

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
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCookieEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthPasswordEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRegisterCapeEvent;
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

	void dispatchAuthCookieEvent(IEaglerPendingConnection pendingConnection, byte[] authUsername,
			boolean cookiesEnabled, byte[] cookieData, String profileUsername, UUID profileUUID, EnumAuthType authType,
			String authMessage, String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthCookieEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchAuthPasswordEvent(IEaglerPendingConnection pendingConnection, byte[] authUsername,
			byte[] authSaltingData, byte[] authPasswordData, boolean cookiesEnabled, byte[] cookieData,
			String profileUsername, UUID profileUUID, EnumAuthType authType, String authMessage,
			String authRequestedServer,
			IEventDispatchCallback<IEaglercraftAuthPasswordEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchClientBrandEvent(IEaglerPendingConnection pendingConnection,
			IEventDispatchCallback<IEaglercraftClientBrandEvent<PlayerObject, ComponentObject>> onComplete);

	void dispatchInitializePlayerEvent(IEaglerPlayer<PlayerObject> player,
			IEventDispatchCallback<IEaglercraftInitializePlayerEvent<PlayerObject>> onComplete);

	void dispatchDestroyPlayerEvent(IEaglerPlayer<PlayerObject> player,
			IEventDispatchCallback<IEaglercraftDestroyPlayerEvent<PlayerObject>> onComplete);

	void dispatchMOTDEvent(IMOTDConnection connection,
			IEventDispatchCallback<IEaglercraftMOTDEvent<PlayerObject>> onComplete);

	void dispatchRegisterSkinEvent(IEaglerPendingConnection pendingConnection, IRegisterSkinDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterSkinEvent<PlayerObject>> onComplete);

	void dispatchRegisterCapeEvent(IEaglerPendingConnection pendingConnection, IRegisterCapeDelegate delegate,
			IEventDispatchCallback<IEaglercraftRegisterCapeEvent<PlayerObject>> onComplete);

	void dispatchRevokeSessionQueryEvent(IQueryConnection query, byte[] cookieData,
			IEventDispatchCallback<IEaglercraftRevokeSessionQueryEvent<PlayerObject>> onComplete);

	void dispatchVoiceChangeEvent(IEaglerPlayer<PlayerObject> player, EnumVoiceState voiceStateOld,
			IVoiceChannel voiceChannelOld, EnumVoiceState voiceStateNew, IVoiceChannel voiceChannelNew,
			IEventDispatchCallback<IEaglercraftVoiceChangeEvent<PlayerObject>> onComplete);

	void dispatchWebSocketOpenEvent(IWebSocketOpenDelegate delegate,
			IEventDispatchCallback<IEaglercraftWebSocketOpenEvent<PlayerObject>> onComplete);

	void dispatchWebViewChannelEvent(IEaglerPlayer<PlayerObject> player, EnumEventType type, String channel,
			IEventDispatchCallback<IEaglercraftWebViewChannelEvent<PlayerObject>> onComplete);

	void dispatchWebViewMessageEvent(IEaglerPlayer<PlayerObject> player, EnumMessageType type, byte[] data,
			IEventDispatchCallback<IEaglercraftWebViewMessageEvent<PlayerObject>> onComplete);

}
