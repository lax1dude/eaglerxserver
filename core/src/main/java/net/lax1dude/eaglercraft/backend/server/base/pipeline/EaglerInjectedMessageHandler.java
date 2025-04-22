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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.lax1dude.eaglercraft.backend.server.base.message.InjectedMessage;
import net.lax1dude.eaglercraft.backend.server.base.message.InjectedMessageController;

public class EaglerInjectedMessageHandler extends MessageToMessageCodec<ByteBuf, InjectedMessage> {

	private final InjectedMessageController injectedController;

	public EaglerInjectedMessageHandler(InjectedMessageController injectedController) {
		this.injectedController = injectedController;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, InjectedMessage msg, List<Object> output) throws Exception {
		msg.writePacket(output);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> output) throws Exception {
		if(msg.readableBytes() > 0 && msg.getUnsignedByte(0) == 0xEE) {
			injectedController.readPacket(msg);
		}else {
			output.add(msg);
		}
	}

}
