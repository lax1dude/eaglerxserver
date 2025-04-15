package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifIconsRegisterV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public final class IconDef {

	@Nonnull
	public static IconDef create(@Nonnull UUID uuid, @Nonnull PacketImageData icon) {
		return new IconDef(uuid, icon);
	}

	private final UUID uuid;
	private final PacketImageData icon;

	private IconDef(UUID uuid, PacketImageData icon) {
		this.uuid = uuid;
		this.icon = icon;
	}

	@Nonnull
	public UUID getUUID() {
		return uuid;
	}

	@Nonnull
	public PacketImageData getIcon() {
		return icon;
	}

	@Nonnull
	public SPacketNotifIconsRegisterV4EAG.CreateIcon toPacket() {
		return new SPacketNotifIconsRegisterV4EAG.CreateIcon(uuid.getMostSignificantBits(),
				uuid.getLeastSignificantBits(), icon);
	}

}
