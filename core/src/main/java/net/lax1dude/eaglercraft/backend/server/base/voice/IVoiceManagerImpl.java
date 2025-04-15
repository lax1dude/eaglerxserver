package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;

public interface IVoiceManagerImpl<PlayerObject> extends IVoiceManager<PlayerObject> {

	void handleServerPreConnect();

	void handleServerPostConnect(String serverName);

	void destroyVoiceManager();

	void handlePlayerSignalPacketTypeConnect();

	void handlePlayerSignalPacketTypeRequest(long playerUUIDMost, long playerUUIDLeast);

	void handlePlayerSignalPacketTypeICE(long playerUUIDMost, long playerUUIDLeast, byte[] str);

	void handlePlayerSignalPacketTypeDesc(long playerUUIDMost, long playerUUIDLeast, byte[] str);

	void handlePlayerSignalPacketTypeDisconnectPeer(long playerUUIDMost, long playerUUIDLeast);

	void handlePlayerSignalPacketTypeDisconnect();

	void handleBackendMessage(byte[] data);

}
