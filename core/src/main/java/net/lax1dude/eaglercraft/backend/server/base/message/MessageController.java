package net.lax1dude.eaglercraft.backend.server.base.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public abstract class MessageController {

	public interface IExceptionCallback {
		void handleException(Exception ex);
	}

	public interface IMessageHandler extends GameMessageHandler, IExceptionCallback {
	}

	protected final GamePluginMessageProtocol protocol;
	protected final GameMessageHandler handler;
	protected final IExceptionCallback exceptionHandler;
	protected final EventLoop eventLoop;
	protected final int defragSendDelay;

	protected List<GameMessagePacket> sendQueue;
	protected final Callable<Void> handleFlush;
	protected ScheduledFuture<Void> futureSendTask = null;

	public MessageController(GamePluginMessageProtocol protocol, IMessageHandler handler,
			EventLoop eventLoop, int defragSendDelay) {
		this(protocol, handler, handler, eventLoop, defragSendDelay);
	}

	public MessageController(GamePluginMessageProtocol protocol, GameMessageHandler handler,
			IExceptionCallback exceptionHandler, EventLoop eventLoop, int defragSendDelay) {
		this.protocol = protocol;
		this.handler = handler;
		this.exceptionHandler = exceptionHandler;
		this.eventLoop = eventLoop;
		this.defragSendDelay = defragSendDelay;
		this.sendQueue = defragSendDelay > 0 ? new ArrayList<>() : null;
		this.handleFlush = defragSendDelay > 0 ? () -> {
			GameMessagePacket packet;
			eagler: {
				GameMessagePacket[] packets;
				synchronized(this) {
					futureSendTask = null;
					int len = sendQueue.size();
					if(len == 0) {
						return null;
					}else if(len == 1) {
						packet = sendQueue.remove(0);
						break eagler;
					}else {
						packets = sendQueue.toArray(new GameMessagePacket[len]);
						if(len < 64) {
							sendQueue.clear();
						}else {
							sendQueue = new ArrayList<>();
						}
					}
				}
				try {
					writeMultiPacket(packets);
				}catch(IOException ex) {
					onException(ex);
				}
				return null;
			}
			try {
				writePacket(packet);
			}catch(IOException ex) {
				onException(ex);
			}
			return null;
		} : null;
	}

	public boolean isSendQueueEnabled() {
		return defragSendDelay > 0;
	}

	public void sendPacket(GameMessagePacket packet) {
		if(defragSendDelay > 0) {
			synchronized(this) {
				sendQueue.add(packet);
				if(futureSendTask == null || futureSendTask.isDone()) {
					futureSendTask = eventLoop.schedule(handleFlush, defragSendDelay, TimeUnit.MILLISECONDS);
				}
			}
		}else {
			try {
				writePacket(packet);
			}catch(IOException ex) {
				onException(ex);
			}
		}
	}

	protected void handlePacket(GameMessagePacket packet) {
		try {
			packet.handlePacket(handler);
		}catch(Exception ex) {
			onException(ex);
		}
	}

	protected void onException(Exception ex) {
		exceptionHandler.handleException(ex);
	}

	protected abstract void writePacket(GameMessagePacket packet) throws IOException;

	protected abstract void writeMultiPacket(GameMessagePacket[] packets) throws IOException;

}
