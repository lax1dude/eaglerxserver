package net.lax1dude.eaglercraft.backend.rpc.api.notifications;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;

public final class IconDef {

	public static IconDef create(UUID uuid, IPacketImageData icon) {
		return new IconDef(uuid, icon);
	}

	private final UUID uuid;
	private final IPacketImageData icon;

	private IconDef(UUID uuid, IPacketImageData icon) {
		this.uuid = uuid;
		this.icon = icon;
	}

	public UUID getUUID() {
		return uuid;
	}

	public IPacketImageData getIcon() {
		return icon;
	}

}
