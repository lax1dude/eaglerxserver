package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketServerInfoDataChunkV4EAG;

public interface IWebViewBlob {

	byte[] getHash();

	List<SPacketServerInfoDataChunkV4EAG> loadChunks();

}
