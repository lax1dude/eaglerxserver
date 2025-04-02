package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.voice.IVoiceManagerImpl;
import net.lax1dude.eaglercraft.backend.server.base.webview.WebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.*;

public class ServerV4MessageHandler extends ServerV3MessageHandler {

	public ServerV4MessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		super(eaglerHandle);
	}

	public void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {
		throw wrongPacket();
	}

	public void handleClient(CPacketVoiceSignalDisconnectV4EAG packet) {
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			mgr.handlePlayerSignalPacketTypeDisconnect();
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalDisconnectPeerV4EAG packet) {
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			mgr.handlePlayerSignalPacketTypeDisconnectPeer(packet.uuidMost, packet.uuidLeast);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketGetOtherClientUUIDV4EAG packet) {
		eaglerHandle.handlePacketGetOtherClientUUID(packet.playerUUIDMost, packet.playerUUIDLeast, packet.requestId);
	}

	public void handleClient(CPacketRequestServerInfoV4EAG packet) {
		WebViewManager<?> mgr = eaglerHandle.getWebViewManager();
		if(mgr != null) {
			mgr.handlePacketRequestData(packet.requestHash);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketWebViewMessageV4EAG packet) {
		WebViewManager<?> mgr = eaglerHandle.getWebViewManager();
		if(mgr != null) {
			mgr.handlePacketMessage(packet.data, packet.type != CPacketWebViewMessageV4EAG.TYPE_STRING);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketWebViewMessageEnV4EAG packet) {
		WebViewManager<?> mgr = eaglerHandle.getWebViewManager();
		if(mgr != null) {
			mgr.handlePacketChannel(packet.channelName, packet.messageChannelOpen);
		}else {
			throw notCapable();
		}
	}

}
