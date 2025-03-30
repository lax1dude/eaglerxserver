package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;

public class ServerV2RPCProtocolHandler extends ServerV1RPCProtocolHandler {

	public ServerV2RPCProtocolHandler(BasePlayerRPCManager<?> rpcManager) {
		super(rpcManager);
	}

	public void handleClient(CPacketRPCDisabled packet) {
		throw wrongPacket();
	}

	public void handleClient(CPacketRPCRequestPlayerInfo packet) {
		switch(packet.requestType) {
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_TEXTURE_DATA:
			rpcManager.handleRequestTextureData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_DATA:
			rpcManager.handleRequestBrandData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_MINECRAFT_BRAND:
			rpcManager.handleRequestMinecraftBrand(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_AUTH_USERNAME:
			rpcManager.handleRequestAuthUsername(packet.requestID);
			break;
		default:
			super.handleClient(packet);
			break;
		}
	}

	public void handleClient(CPacketRPCInjectRawBinaryFrameV2 packet) {
		rpcManager.handleInjectRawBinaryFrame(packet.messageData);
	}

	public void handleClient(CPacketRPCInjectRawEaglerFrameV2 packet) {
		rpcManager.handleInjectRawEaglerFrame(packet.packetID, packet.messageData);
	}

}
