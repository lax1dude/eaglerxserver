package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccess;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessEaglerV2;
import net.lax1dude.eaglercraft.backend.server.base.EaglerConnectionInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

public class EaglerPlayerRPCManager<PlayerObject> extends BasePlayerRPCManager<PlayerObject> {

	private final EaglerPlayerInstance<PlayerObject> player;

	EaglerPlayerRPCManager(BackendRPCService<PlayerObject> service, EaglerPlayerInstance<PlayerObject> player) {
		super(service);
		this.player = player;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	protected void handleEnabled(EaglerBackendRPCProtocol protocol) {
		if(protocol == EaglerBackendRPCProtocol.V1) {
			sendRPCInitPacket(new SPacketRPCEnabledSuccess(protocol.vers, player.getEaglerProtocol().ver));
		}else {
			EaglerConnectionInstance conn = player.connectionImpl();
			sendRPCInitPacket(new SPacketRPCEnabledSuccessEaglerV2(protocol.vers, player.getMinecraftProtocol(),
					player.getEaglerXServer().getSupervisorService().getNodeId(), player.getHandshakeEaglerProtocol(),
					player.getEaglerProtocol().ver, player.getRewindProtocolVersion(), conn.getCapabilityMask(),
					conn.getCapabilityVers(), conn.getExtCapabilities().entrySet().stream().map(
							(etr) -> new SPacketRPCEnabledSuccessEaglerV2.ExtCapability(etr.getKey(), etr.getValue() & 0xFF))
							.toList()));
		}
		handleEnableContext(new EaglerPlayerRPCContext<>(this, protocol));
	}

	private static final byte[] READY_DATA = new byte[] { 1 };

	@Override
	protected void sendReadyMessage() {
		player.getPlatformPlayer().sendDataBackend(service.getReadyChannel(), READY_DATA);
	}

}
