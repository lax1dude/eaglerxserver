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

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

public class HAProxyDetectionHandler extends ByteToMessageDecoder {

	private static final Class<? extends ChannelHandler> clzHAProxyHandler;

	static {
		try {
			clzHAProxyHandler = (Class<? extends ChannelHandler>) Class
					.forName("io.netty.handler.codec.haproxy.HAProxyMessageDecoder");
		} catch (ClassNotFoundException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readable = in.readableBytes();
		if (!ctx.channel().isActive()) {
			in.skipBytes(readable);
		} else {
			boolean proxy = false;
			eagler: {
				int readerIndex = in.readerIndex();
				if (readable >= 12) {
					proxy = in.getByte(readerIndex) == (byte) 0x0D && in.getByte(readerIndex + 1) == (byte) 0x0A
							&& in.getByte(readerIndex + 2) == (byte) 0x0D && in.getByte(readerIndex + 3) == (byte) 0x0A
							&& in.getByte(readerIndex + 4) == (byte) 0x00 && in.getByte(readerIndex + 5) == (byte) 0x0D
							&& in.getByte(readerIndex + 6) == (byte) 0x0A && in.getByte(readerIndex + 7) == (byte) 0x51
							&& in.getByte(readerIndex + 8) == (byte) 0x55 && in.getByte(readerIndex + 9) == (byte) 0x49
							&& in.getByte(readerIndex + 10) == (byte) 0x54
							&& in.getByte(readerIndex + 11) == (byte) 0x0A;
					if (proxy) {
						break eagler;
					}
				}
				if (readable >= 6) {
					proxy = in.getByte(readerIndex) == (byte) 'P' && in.getByte(readerIndex + 1) == (byte) 'R'
							&& in.getByte(readerIndex + 2) == (byte) 'O' && in.getByte(readerIndex + 3) == (byte) 'X'
							&& in.getByte(readerIndex + 4) == (byte) 'Y' && in.getByte(readerIndex + 5) == (byte) ' ';
					break eagler;
				}
				return;
			}
			ChannelPipeline pipeline = ctx.pipeline();
			if (!proxy) {
				pipeline.remove(clzHAProxyHandler);
			}
			ctx.fireChannelRead(BufferUtils.readRetainedSlice(in, readable));
			pipeline.remove(this);
		}
	}

}
