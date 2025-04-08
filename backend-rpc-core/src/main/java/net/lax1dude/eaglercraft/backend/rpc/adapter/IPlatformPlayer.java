package net.lax1dude.eaglercraft.backend.rpc.adapter;

import java.util.UUID;

public interface IPlatformPlayer<PlayerObject> {

	PlayerObject getPlayerObject();

	<T> T getAttachment();

	String getUsername();

	UUID getUniqueId();

	boolean isConnected();

	void sendData(String channel, byte[] message);

	boolean isSetViewDistanceSupportedPaper();

	void setViewDistancePaper(int distance);

}
