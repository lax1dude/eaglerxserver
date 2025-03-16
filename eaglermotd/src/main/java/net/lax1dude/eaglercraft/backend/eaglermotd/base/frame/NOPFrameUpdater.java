package net.lax1dude.eaglercraft.backend.eaglermotd.base.frame;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public final class NOPFrameUpdater implements IFrameUpdater {

	public static final NOPFrameUpdater INSTANCE = new NOPFrameUpdater();

	private NOPFrameUpdater() {
	}

	@Override
	public boolean update(IMOTDConnection connection) {
		return false;
	}

}
