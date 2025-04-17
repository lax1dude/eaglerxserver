package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;

public interface IRPCEvent {

	@Nonnull
	EnumSubscribeEvents getEventType();

}
