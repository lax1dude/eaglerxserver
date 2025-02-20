package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformCommandSender<PlayerObject> {

	boolean checkPermission(String permission);

	<ComponentObject> void sendMessage(ComponentObject component);

	boolean isPlayer();

	IPlatformPlayer<PlayerObject> asPlayer();

}
