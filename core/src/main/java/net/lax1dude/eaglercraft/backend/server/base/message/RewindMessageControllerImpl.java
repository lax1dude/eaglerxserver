package net.lax1dude.eaglercraft.backend.server.base.message;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class RewindMessageControllerImpl extends MessageController {

	private final RewindMessageControllerHandle handle;

	public RewindMessageControllerImpl(RewindMessageControllerHandle handle, GamePluginMessageProtocol protocol,
			GameMessageHandler handler, IExceptionCallback exceptionHandler) {
		super(protocol, handler, exceptionHandler, null, -1);
		this.handle = handle;
		this.handle.impl = this;
	}

	public RewindMessageControllerImpl(RewindMessageControllerHandle handle, GamePluginMessageProtocol protocol,
			IMessageHandler handler) {
		this(handle, protocol, handler, handler);
	}

	@Override
	protected void writePacket(GameMessagePacket packet) throws IOException {
		handle.recieveOutboundMessage(packet);
	}

	@Override
	protected void writeMultiPacket(GameMessagePacket[] packets) throws IOException {
		throw new IllegalStateException();
	}

}
