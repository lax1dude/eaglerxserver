package net.lax1dude.eaglercraft.backend.server.api.notifications;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketNotifBadgeShowV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface INotificationService<PlayerObject> extends IPacketImageLoader {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nonnull
	<ComponentObject> INotificationBuilder<ComponentObject> createNotificationBuilder(@Nonnull Class<ComponentObject> componentObj);

	@Nullable
	default INotificationManager<PlayerObject> getNotificationManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getNotificationManager() : null;
	}

	@Nonnull
	INotificationManager<PlayerObject> getNotificationManagerAll();

	@Nonnull
	INotificationManager<PlayerObject> getNotificationManagerMulti(@Nonnull Collection<PlayerObject> players);

	@Nonnull
	INotificationManager<PlayerObject> getNotificationManagerMultiEagler(@Nonnull Collection<IEaglerPlayer<PlayerObject>> players);

	void registerNotificationIcon(@Nonnull UUID iconUUID, @Nonnull PacketImageData icon);

	void registerNotificationIcons(@Nonnull Collection<IconDef> icons);

	void unregisterNotificationIcon(@Nonnull UUID iconUUID);

	void unregisterNotificationIcons(@Nonnull Collection<UUID> iconUUIDs);

	void showNotificationBadge(@Nonnull INotificationBuilder<?> builder);

	void showNotificationBadge(@Nonnull SPacketNotifBadgeShowV4EAG packet);

}
