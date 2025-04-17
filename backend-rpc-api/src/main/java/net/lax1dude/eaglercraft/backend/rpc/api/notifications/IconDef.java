package net.lax1dude.eaglercraft.backend.rpc.api.notifications;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;

public final class IconDef {

	@Nonnull
	public static IconDef create(@Nonnull UUID uuid, @Nonnull IPacketImageData icon) {
		if(uuid == null) {
			throw new NullPointerException("uuid");
		}
		if(icon == null) {
			throw new NullPointerException("icon");
		}
		return new IconDef(uuid, icon);
	}

	private final UUID uuid;
	private final IPacketImageData icon;

	private IconDef(UUID uuid, IPacketImageData icon) {
		this.uuid = uuid;
		this.icon = icon;
	}

	@Nonnull
	public UUID getUUID() {
		return uuid;
	}

	@Nonnull
	public IPacketImageData getIcon() {
		return icon;
	}

}
