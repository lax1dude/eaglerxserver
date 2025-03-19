package net.lax1dude.eaglercraft.backend.server.base.notifications;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public class NotificationManagerMultiPlayers<PlayerObject> extends NotificationManagerMulti<PlayerObject> {

	private final Collection<NotificationManagerPlayer<PlayerObject>> players;

	public NotificationManagerMultiPlayers(NotificationService<PlayerObject> service,
			Collection<NotificationManagerPlayer<PlayerObject>> players) {
		super(service);
		this.players = players;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getPlayerList() {
		return Collections2.transform(players, (mgr) -> mgr.player);
	}

	@Override
	protected void forTargets(Consumer<NotificationManagerBase<PlayerObject>> mgr) {
		players.forEach(mgr);
	}

}
