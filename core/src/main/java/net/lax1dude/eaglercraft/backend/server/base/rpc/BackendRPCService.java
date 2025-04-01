package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class BackendRPCService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final String rpcChannel;
	private final String readyChannel;
	final SerializationContext handshakeCtx;

	public BackendRPCService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.rpcChannel = BackendChannelHelper.getRPCChannel(server);
		this.readyChannel = BackendChannelHelper.getReadyChannel(server);
		this.handshakeCtx = new SerializationContext(EaglerBackendRPCProtocol.INIT) {
			@Override
			protected IPlatformLogger logger() {
				return server.logger();
			}
		};
	}

	public VanillaPlayerRPCManager<PlayerObject> createVanillaPlayerRPCManager(BasePlayerInstance<PlayerObject> player) {
		return new VanillaPlayerRPCManager<>(this, player);
	}

	public EaglerPlayerRPCManager<PlayerObject> createEaglerPlayerRPCManager(EaglerPlayerInstance<PlayerObject> player) {
		return new EaglerPlayerRPCManager<>(this, player);
	}

	public String getRPCChannel() {
		return rpcChannel;
	}

	public String getReadyChannel() {
		return readyChannel;
	}

}
