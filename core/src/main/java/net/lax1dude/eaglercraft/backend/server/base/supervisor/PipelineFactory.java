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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.net.SocketAddress;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorDecoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorEncoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.Varint21FrameDecoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.Varint21FrameEncoder;

public class PipelineFactory {

	public static final AttributeKey<SupervisorPacketHandler> HANDLER = AttributeKey.valueOf("Handler");

	public static final WriteBufferWaterMark MARK = new WriteBufferWaterMark(524288, 1048576);

	public static void initiateConnection(EaglerXServer<?> server, SocketAddress addr, SupervisorService<?> controller,
			int connectTimeout, int readTimeout) {
		server.bootstrapClient(addr).handler(getChildInitializer(controller, readTimeout))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout).option(ChannelOption.TCP_NODELAY, true)
				.connect().addListener((future) -> {
					if (future.isSuccess()) {
						controller.handleChannelOpen(((ChannelFuture) future).channel().attr(HANDLER).get());
					} else {
						controller.handleChannelFailure();
					}
				});
	}

	public static ChannelInitializer<Channel> getChildInitializer(SupervisorService<?> controller, int readTimeout) {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.config().setAllocator(PooledByteBufAllocator.DEFAULT).setWriteBufferWaterMark(MARK);
				try {
					channel.config().setOption(ChannelOption.IP_TOS, 24);
				} catch (ChannelException var3) {
				}
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(readTimeout));
				pipeline.addLast("Varint21FrameDecoder", new Varint21FrameDecoder());
				pipeline.addLast("Varint21FrameEncoder", new Varint21FrameEncoder());
				SupervisorDecoder dec = new SupervisorDecoder(EaglerSupervisorProtocol.SERVER_TO_CLIENT);
				pipeline.addLast("SupervisorDecoder", dec);
				SupervisorEncoder enc = new SupervisorEncoder(EaglerSupervisorProtocol.CLIENT_TO_SERVER);
				pipeline.addLast("SupervisorEncoder", enc);
				SupervisorPacketHandler h = new SupervisorPacketHandler(controller.logger(), channel, dec, enc, null);
				h.setConnectionHandler(new SupervisorClientHandshakeHandler(controller, h));
				pipeline.channel().attr(HANDLER).set(h);
				pipeline.addLast("SupervisorPacketHandler", h);
			}
		};
	}

}