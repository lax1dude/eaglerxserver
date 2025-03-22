package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import io.netty.buffer.ByteBufAllocator;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.*;

public class RewindMessageHandler implements GameMessageHandler {

	private final PlayerInstance<?> player;
	private final IOutboundInjector injector;

	public RewindMessageHandler(PlayerInstance<?> player) {
		this.player = player;
		this.injector = player.getOutboundInjector();
	}

	private ByteBufAllocator alloc() {
		return player.getChannel().alloc();
	}

	public void handleServer(SPacketEnableFNAWSkinsEAG packet) {
		
	}

	public void handleServer(SPacketOtherCapeCustomEAG packet) {
		
	}

	public void handleServer(SPacketOtherCapePresetEAG packet) {
		
	}

	public void handleServer(SPacketOtherSkinCustomV3EAG packet) {
		
	}

	public void handleServer(SPacketOtherSkinPresetEAG packet) {
		
	}

	public void handleServer(SPacketUpdateCertEAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalAllowedEAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalConnectV3EAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalConnectV4EAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalConnectAnnounceV4EAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalDescEAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalDisconnectPeerEAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalGlobalEAG packet) {
		
	}

	public void handleServer(SPacketVoiceSignalICEEAG packet) {
		
	}

}
