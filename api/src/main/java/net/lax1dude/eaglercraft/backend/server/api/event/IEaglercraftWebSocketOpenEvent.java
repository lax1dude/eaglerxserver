package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;

public interface IEaglercraftWebSocketOpenEvent<PlayerObject> extends IEaglerXServerEvent<PlayerObject>, ICancellableEvent {

	String getHeader(EnumWebSocketHeader header);

	String getRealIP();

}
