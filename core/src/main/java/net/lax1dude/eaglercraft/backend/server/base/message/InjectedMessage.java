package net.lax1dude.eaglercraft.backend.server.base.message;

import java.util.List;

public abstract class InjectedMessage {

	public abstract void writePacket(List<Object> output);

}
