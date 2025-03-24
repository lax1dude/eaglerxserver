package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class BackendChannelHelper {

	public static <PlayerObject> Collection<IEaglerXServerMessageChannel<PlayerObject>> getBackendChannels(EaglerXServer<PlayerObject> server) {
		ImmutableList.Builder<IEaglerXServerMessageChannel<PlayerObject>> backendChannelBuilder = ImmutableList.builder();
		//TODO
		return backendChannelBuilder.build();
	}

}
