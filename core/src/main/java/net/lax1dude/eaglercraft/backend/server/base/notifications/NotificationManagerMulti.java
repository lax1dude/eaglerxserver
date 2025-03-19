package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public abstract class NotificationManagerMulti<PlayerObject> extends NotificationManagerBase<PlayerObject> {

	public NotificationManagerMulti(NotificationService<PlayerObject> service) {
		super(service);
	}

	protected abstract void forTargets(Consumer<NotificationManagerBase<PlayerObject>> mgr);

	@Override
	protected void touchIcon(UUID uuid) {
		forTargets((target) -> target.touchIcon(uuid));
	}

	@Override
	protected void touchIcons(GameMessagePacket packet, UUID uuidA, UUID uuidB) {
		forTargets((target) -> target.touchIcons(packet, uuidA, uuidB));
	}

	@Override
	protected void touchIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
		forTargets((target) -> target.touchIcons(uuids, tmp));
	}

	@Override
	protected void releaseIcon(UUID uuid) {
		forTargets((target) -> target.releaseIcon(uuid));
	}

	@Override
	protected void releaseIcons() {
		forTargets((target) -> target.releaseIcons());
	}

	@Override
	protected void releaseIcons(Collection<UUID> uuids, Collection<UUID> tmp) {
		forTargets((target) -> target.releaseIcons(uuids, tmp));
	}

	@Override
	protected void sendPacket(GameMessagePacket packet) {
		forTargets((target) -> target.sendPacket(packet));
	}

}
