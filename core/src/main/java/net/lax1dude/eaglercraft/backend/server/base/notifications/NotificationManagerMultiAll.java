package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public class NotificationManagerMultiAll<PlayerObject> extends NotificationManagerMulti<PlayerObject> {

	public NotificationManagerMultiAll(NotificationService<PlayerObject> service) {
		super(service);
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getPlayerList() {
		return Collections2.filter(service.server.getAllEaglerPlayers(), IEaglerPlayer::isNotificationSupported);
	}

	@Override
	protected void forTargets(Consumer<NotificationManagerBase<PlayerObject>> mgr) {
		service.server.forEachEaglerPlayerInternal((player) -> {
			NotificationManagerPlayer<PlayerObject> ret = player.getNotificationManagerOrNull();
			if(ret != null) {
				mgr.accept(ret);
			}
		});
	}

}
