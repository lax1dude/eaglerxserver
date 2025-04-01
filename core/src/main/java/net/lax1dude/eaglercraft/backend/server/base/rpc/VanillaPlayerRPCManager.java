package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledFailure;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledSuccessVanillaV2;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public class VanillaPlayerRPCManager<PlayerObject> extends BasePlayerRPCManager<PlayerObject> {

	private final BasePlayerInstance<PlayerObject> player;

	VanillaPlayerRPCManager(BackendRPCService<PlayerObject> service, BasePlayerInstance<PlayerObject> player) {
		super(service);
		this.player = player;
	}

	@Override
	public BasePlayerInstance<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	protected void handleEnabled(EaglerBackendRPCProtocol protocol) {
		if(protocol == EaglerBackendRPCProtocol.V1) {
			sendRPCInitPacket(new SPacketRPCEnabledFailure(SPacketRPCEnabledFailure.FAILURE_CODE_NOT_EAGLER_PLAYER));
		}else {
			sendRPCInitPacket(new SPacketRPCEnabledSuccessVanillaV2(protocol.vers, player.getMinecraftProtocol(),
					player.getEaglerXServer().getSupervisorService().getNodeId()));
			handleEnableContext(new VanillaPlayerRPCContext<>(this, protocol));
		}
	}

	private static final byte[] READY_DATA = new byte[] { 0 };

	@Override
	protected void sendReadyMessage() {
		player.getPlatformPlayer().sendDataBackend(service.getReadyChannel(), READY_DATA);
	}

}
