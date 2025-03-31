package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

class TextureDataHelper {

	static PacketImageData packetImageDataRPCToCore(net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData data) {
		return new PacketImageData(data.width, data.height, data.rgba);
	}

	static net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData packetImageDataCoreToRPC(PacketImageData data) {
		return new net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData(data.width, data.height, data.rgba);
	}

	static byte[] encodeSkinData(IEaglerPlayerSkin skin) {
		return null;//TODO
	}

	static byte[] encodeCapeData(IEaglerPlayerCape cape) {
		return null;//TODO
	}

	static byte[] encodeTexturesData(IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		return null;//TODO
	}

	static IEaglerPlayerSkin decodeSkinData(byte[] data) {
		return null;//TODO
	}

	static IEaglerPlayerCape decodeCapeData(byte[] cape) {
		return null;//TODO
	}

	static IEaglerPlayerSkin decodeTexturesSkinData(byte[] data) {
		return null;//TODO
	}

	static IEaglerPlayerCape decodeTexturesCapeData(byte[] cape, boolean skinPreset) {
		return null;//TODO
	}

}
