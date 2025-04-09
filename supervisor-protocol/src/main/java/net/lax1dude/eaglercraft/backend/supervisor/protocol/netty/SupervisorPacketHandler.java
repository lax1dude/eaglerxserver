/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.supervisor.protocol.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.util.ILoggerSv;

public class SupervisorPacketHandler extends ChannelInboundHandlerAdapter {

	private final ILoggerSv logger;
	private EaglerSupervisorProtocol protocol = EaglerSupervisorProtocol.INIT;
	private final Channel channel;
	private final SupervisorDecoder decoder;
	private final SupervisorEncoder encoder;
	private EaglerSupervisorHandler handler;

	public SupervisorPacketHandler(ILoggerSv logger, Channel channel, SupervisorDecoder decoder, SupervisorEncoder encoder,
			EaglerSupervisorHandler handler) {
		this.logger = logger;
		this.channel = channel;
		this.decoder = decoder;
		this.encoder = encoder;
		this.handler = handler;
	}

	public Channel getChannel() {
		return channel;
	}

	public void channelWrite(Object msg) {
		channel.writeAndFlush(msg, channel.voidPromise());
	}

	public void setConnectionProtocol(EaglerSupervisorProtocol protocol) {
		this.protocol = protocol;
		decoder.setConnectionProtocol(protocol);
		encoder.setConnectionProtocol(protocol);
	}

	public EaglerSupervisorProtocol getConnectionProtocol() {
		return protocol;
	}

	public void setConnectionHandler(EaglerSupervisorHandler handler) {
		this.handler = handler;
	}

	public EaglerSupervisorHandler getConnectionHandler() {
		return handler;
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if(handler == null) {
				throw new IllegalStateException("Recieved packet with null handler!");
			}
			if(msg instanceof EaglerSupervisorPacket) {
				((EaglerSupervisorPacket)msg).handlePacket(handler);
			}else {
				logger.warn("Ignoring unknown packet type: " + msg.getClass().getSimpleName());
			}
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(ctx.channel().isActive()) {
			logger.error("[" + ctx.channel().remoteAddress() + "] Supervisor encountered an exception: ", cause);
			ctx.close();
		}
	}

	public void channelInactive(ChannelHandlerContext ctx) {
		logger.warn("[" + ctx.channel().remoteAddress() + "]: Supervisor connection lost!");
		if(handler != null) {
			handler.handleDisconnected();
		}
	}

}