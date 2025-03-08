package net.lax1dude.eaglercraft.backend.server.adapter;

import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;

public interface IPlatformZlib extends INativeZlib, INativeZlib.NettyUnsafe {

	default NettyUnsafe netty() {
		return this;
	}

}
