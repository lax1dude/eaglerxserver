package net.lax1dude.eaglercraft.backend.eaglermotd.base.frame;

import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public class CompoundFrameUpdater implements IFrameUpdater {

	private final List<IFrameUpdater> updaters;

	public CompoundFrameUpdater(List<IFrameUpdater> updaters) {
		this.updaters = updaters;
	}

	@Override
	public boolean update(IMOTDConnection connection) {
		boolean ret = false;
		for(int i = 0, l = updaters.size(); i < l; ++i) {
			ret |= updaters.get(i).update(connection);
		}
		return ret;
	}

}
