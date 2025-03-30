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
			rpcManager.context().handleRequestTextureData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_CLIENT_BRAND_DATA:
			rpcManager.context().handleRequestBrandData(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_MINECRAFT_BRAND:
			rpcManager.context().handleRequestMinecraftBrand(packet.requestID);
			break;
		case CPacketRPCRequestPlayerInfo.REQUEST_PLAYER_AUTH_USERNAME:
			rpcManager.context().handleRequestAuthUsername(packet.requestID);
			break;
		default:
			super.handleClient(packet);
			break;
		}
	}

	public void handleClient(CPacketRPCInjectRawBinaryFrameV2 packet) {
		rpcManager.context().handleInjectRawBinaryFrame(packet.messageData);
	}

	public void handleClient(CPacketRPCInjectRawEaglerFrameV2 packet) {
		rpcManager.context().handleInjectRawEaglerFrame(packet.packetID, packet.messageData);
	}

}
