package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IPacket2ClientProtocol;

public class RewindHandshakeServerCodec<PlayerObject> extends RewindChannelHandler.Codec<PlayerObject> {

	protected static final int STATE_STALLING = 0;
	protected static final int STATE_SENT_HANDSHAKE = 1;
	protected static final int STATE_SENT_REQUESTED_LOGIN = 2;
	protected static final int STATE_SENT_FINISH_LOGIN = 3;
	protected static final int STATE_COMPLETED = 4;

	protected int state = STATE_SENT_HANDSHAKE;
	protected String username;

	public RewindHandshakeServerCodec(IPacket2ClientProtocol firstPacket) {
		this.username = firstPacket.getUsername();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		throw new IllegalStateException("Received an unexpected packet before the handshake was initialized");
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		if(state != STATE_COMPLETED && buf.readableBytes() >= 1) {
			int type = buf.readUnsignedByte();
			switch(type) {
			case 0x02: // PROTOCOL_SERVER_VERISON
				handleServerVersion(ctx, buf);
				break;
			case 0x03: // PROTOCOL_VERISON_MISMATCH
				handleServerVersionMismatch(ctx, buf);
				break;
			case 0x05: // PROTOCOL_SERVER_ALLOW_LOGIN
				handleServerAllowLogin(ctx, buf);
				break;
			case 0x06: // PROTOCOL_SERVER_DENY_LOGIN
				handleServerDenyLogin(ctx, buf);
				break;
			case 0x09: // PROTOCOL_SERVER_FINISH_LOGIN
				handleServerFinishLogin(ctx, buf);
				break;
			case 0xFF: // PROTOCOL_SERVER_ERROR
				handleServerError(ctx, buf);
				break;
			default:
				handleUnexpectedPacket(ctx, type);
				break;
			}
		}
		out.add(Unpooled.EMPTY_BUFFER); // :(
	}

	private void handleServerVersion(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_HANDSHAKE) {
			state = STATE_STALLING;
			
		}else {
			handleUnexpectedPacket(ctx, 0x02);
		}
	}

	private void handleServerVersionMismatch(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_HANDSHAKE) {
			state = STATE_STALLING;
			
		}else {
			handleUnexpectedPacket(ctx, 0x03);
		}
	}

	private void handleServerAllowLogin(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_REQUESTED_LOGIN) {
			state = STATE_STALLING;
			
		}else {
			handleUnexpectedPacket(ctx, 0x05);
		}
	}

	private void handleServerDenyLogin(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_REQUESTED_LOGIN) {
			state = STATE_STALLING;
			
		}else {
			handleUnexpectedPacket(ctx, 0x06);
		}
	}

	private void handleServerFinishLogin(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_FINISH_LOGIN) {
			state = STATE_STALLING;
			
		}else {
			handleUnexpectedPacket(ctx, 0x09);
		}
	}

	private void handleServerError(ChannelHandlerContext ctx, ByteBuf buf) {
		state = STATE_COMPLETED;
		
	}

	private void handleUnexpectedPacket(ChannelHandlerContext ctx, int type) {
		state = STATE_COMPLETED;
		
	}

	protected void initClientConnection() {
		handler().setCodec(new RewindHandshakeClientCodec<PlayerObject>().begin(this));
	}

}
