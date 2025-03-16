package net.lax1dude.eaglercraft.backend.eaglermotd.base.frame;

import net.lax1dude.eaglercraft.backend.server.api.query.IMOTDConnection;

public interface IFrameUpdater {
	boolean update(IMOTDConnection connection);
}
