package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;

public class ServerV2RPCProtocolHandler extends ServerV1RPCProtocolHandler {

	public ServerV2RPCProtocolHandler(BasePlayerRPCContext<?> rpcContext) {
		super(rpcContext);
	}

	public void handleClient(CPacketRPCDisabled packet) {
		throw wrongPacket();
	}

	public void handleClient(CPacketRPCRequestPlayerInfo packet) {
		switch(packet.requestType) {
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS:
			throw wrongPacket();
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_TEXTURE_DATA:
			rpcContext.handleRequestTextureData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_DATA:
			rpcContext.handleRequestBrandData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_MINECRAFT_BRAND:
			rpcContext.handleRequestMinecraftBrand(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_AUTH_USERNAME:
			rpcContext.handleRequestAuthUsername(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_WEBVIEW_STATUS_V2:
			rpcContext.handleRequestWebViewStatus(packet.requestID);
			break;
		default:
			super.handleClient(packet);
			break;
		}
	}

	public void handleClient(CPacketRPCSetPlayerSkinPresetV2 packet) {
		rpcContext.handleSetPlayerSkinPreset(packet.presetSkinId, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerCapePresetV2 packet) {
		rpcContext.handleSetPlayerCapePreset(packet.presetCapeId, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCInjectRawBinaryFrameV2 packet) {
		rpcContext.handleInjectRawBinaryFrame(packet.messageData);
	}

	public void handleClient(CPacketRPCSetPlayerTexturesV2 packet) {
		rpcContext.handleSetPlayerTextures(packet.texturesPacket, packet.notifyOthers);
	}

	public void handleClient(CPacketRPCSetPlayerTexturesPresetV2 packet) {
		rpcContext.handleSetPlayerTexturesPreset(packet.presetSkinId, packet.presetCapeId, packet.notifyOthers);
	}

}
