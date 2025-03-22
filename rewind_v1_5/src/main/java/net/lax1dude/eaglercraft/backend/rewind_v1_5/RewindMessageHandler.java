package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
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
		// nope
	}

	public void handleServer(SPacketUpdateCertEAG packet) {
		// nope
	}

	public void handleServer(SPacketVoiceSignalAllowedEAG packet) {
		injector.injectOutbound((out) -> {
			ByteBuf buf = alloc().buffer();
			try {
				// VOICE_SIGNAL_ALLOWED
				buf.writeByte(0);
				buf.writeBoolean(packet.allowed);
				if(!packet.allowed) {
					player.releaseVoiceGlobalMap();
				}
				buf.writeByte(packet.iceServers.length);
				for(String str : packet.iceServers) {
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
				}
				out.add(buf.retain());
			}finally {
				buf.release();
			}
		});
	}

	public void handleServer(SPacketVoiceSignalGlobalEAG packet) {
		injector.injectOutbound((out) -> {
			player.handleVoiceGlobal(packet.users);
			ByteBuf buf = alloc().buffer();
			try {
				// VOICE_SIGNAL_GLOBAL
				buf.writeByte(5);
				buf.writeInt(packet.users.size());
				for(SPacketVoiceSignalGlobalEAG.UserData user : packet.users) {
					byte[] bytes = user.username.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
				}
				out.add(buf.retain());
			}finally {
				buf.release();
			}
		});
	}

	public void handleServer(SPacketVoiceSignalConnectV4EAG packet) {
		injector.injectOutbound((out) -> {
			String str = player.getVoicePlayerByUUID(new UUID(packet.uuidMost, packet.uuidLeast));
			if(str != null) {
				ByteBuf buf = alloc().buffer();
				try {
					// VOICE_SIGNAL_CONNECT
					buf.writeByte(1);
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
					buf.writeBoolean(packet.offer);
					out.add(buf.retain());
				}finally {
					buf.release();
				}
			}else {
				out.add(Unpooled.EMPTY_BUFFER);
			}
		});
	}

	public void handleServer(SPacketVoiceSignalConnectAnnounceV4EAG packet) {
		injector.injectOutbound((out) -> {
			String str = player.getVoicePlayerByUUID(new UUID(packet.uuidMost, packet.uuidLeast));
			if(str != null) {
				ByteBuf buf = alloc().buffer();
				try {
					// VOICE_SIGNAL_CONNECT
					buf.writeByte(1);
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
					out.add(buf.retain());
				}finally {
					buf.release();
				}
			}else {
				out.add(Unpooled.EMPTY_BUFFER);
			}
		});
	}

	public void handleServer(SPacketVoiceSignalDescEAG packet) {
		injector.injectOutbound((out) -> {
			String str = player.getVoicePlayerByUUID(new UUID(packet.uuidMost, packet.uuidLeast));
			if(str != null) {
				ByteBuf buf = alloc().buffer();
				try {
					// VOICE_SIGNAL_DESC
					buf.writeByte(4);
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
					int descLen = packet.desc.length;
					if(descLen > 32750) {
						throw new IndexOutOfBoundsException("Voice signal packet DESC too long!");
					}
					buf.writeShort(descLen);
					buf.writeBytes(packet.desc);
					out.add(buf.retain());
				}finally {
					buf.release();
				}
			}else {
				out.add(Unpooled.EMPTY_BUFFER);
			}
		});
	}

	public void handleServer(SPacketVoiceSignalICEEAG packet) {
		injector.injectOutbound((out) -> {
			String str = player.getVoicePlayerByUUID(new UUID(packet.uuidMost, packet.uuidLeast));
			if(str != null) {
				ByteBuf buf = alloc().buffer();
				try {
					// VOICE_SIGNAL_ICE
					buf.writeByte(3);
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
					int descLen = packet.ice.length;
					if(descLen > 32750) {
						throw new IndexOutOfBoundsException("Voice signal packet ICE too long!");
					}
					buf.writeShort(descLen);
					buf.writeBytes(packet.ice);
					out.add(buf.retain());
				}finally {
					buf.release();
				}
			}else {
				out.add(Unpooled.EMPTY_BUFFER);
			}
		});
	}

	public void handleServer(SPacketVoiceSignalDisconnectPeerEAG packet) {
		injector.injectOutbound((out) -> {
			String str = player.getVoicePlayerByUUID(new UUID(packet.uuidMost, packet.uuidLeast));
			if(str != null) {
				ByteBuf buf = alloc().buffer();
				try {
					// VOICE_SIGNAL_DISCONNECT
					buf.writeByte(2);
					byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
					buf.writeShort(bytes.length);
					buf.writeBytes(bytes);
					out.add(buf.retain());
				}finally {
					buf.release();
				}
			}else {
				out.add(Unpooled.EMPTY_BUFFER);
			}
		});
	}

	public void handleServer(SPacketForceClientSkinPresetV4EAG packet) {
		// nope
	}

	public void handleServer(SPacketForceClientSkinCustomV4EAG packet) {
		// nope
	}

	public void handleServer(SPacketForceClientCapePresetV4EAG packet) {
		// nope
	}

	public void handleServer(SPacketForceClientCapeCustomV4EAG packet) {
		// nope
	}

	public void handleServer(SPacketRedirectClientV4EAG packet) {
		injector.injectOutbound((out) -> {
			ByteBuf buf = alloc().buffer();
			try {
				buf.writeByte(0xFA);
				BufferUtils.writeLegacyMCString(buf, "EAG|Reconnect", 255);
				byte[] bytes = packet.redirectURI.getBytes(StandardCharsets.UTF_8);
				buf.writeShort(bytes.length);
				buf.writeBytes(bytes);
				out.add(buf.retain());
			}finally {
				buf.release();
			}
		});
	}

	public void handleServer(SPacketInvalidatePlayerCacheV4EAG packet) {
		// nope
	}

	public void handleServer(SPacketUnforceClientV4EAG packet) {
		// nope
	}

	public void handleServer(SPacketOtherTexturesV5EAG packet) {
		injector.injectOutbound((out) -> {
			int cookie = player.removeSkinRequest(new UUID(packet.uuidMost, packet.uuidLeast));
			if(cookie < 0) {
				out.add(Unpooled.EMPTY_BUFFER);
				return;
			}
			ByteBuf buf = alloc().buffer();
			try {
				buf.writeByte(0xFA);
				BufferUtils.writeLegacyMCString(buf, "EAG|UserSkin", 255);
				int lengthAt = buf.writerIndex();
				buf.writeShort(0);
				buf.writeShort(cookie);
				if(packet.skinID >= 0) {
					buf.writeByte(4); // preset skin
					if(packet.skinID < 256) {
						buf.writeByte(SkinPacketUtils.rewritePresetSkinIdToLegacy(packet.skinID));
					}else {
						buf.writeByte(0);
					}
				}else {
					int modelId = -packet.skinID - 1;
					if(modelId == 0) {
						buf.writeByte(1);
						SkinPacketUtils.rewriteCustomSkinToLegacy(packet.customSkin, buf);
					}else if(modelId == 1) {
						buf.writeByte(5);
						SkinPacketUtils.rewriteCustomSkinToLegacy(packet.customSkin, buf);
					}else {
						buf.writeByte(4);
						buf.writeByte(0); // steve
					}
				}
				if(packet.capeID >= 0) {
					buf.writeByte(2); // preset cape
					if(packet.capeID < 256) {
						buf.writeByte(packet.capeID);
					}else {
						buf.writeByte(0);
					}
				}else {
					buf.writeByte(0);
					SkinPacketUtils.rewriteCustomCapeToLegacy(packet.customCape, buf);
				}
				buf.setShort(lengthAt, buf.writerIndex() - lengthAt - 2);
				out.add(buf.retain());
			}finally {
				buf.release();
			}
		});
	}

}
