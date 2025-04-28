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

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class SupervisorDecoder extends MessageToMessageDecoder<ByteBuf> {

	private EaglerSupervisorProtocol protocol = EaglerSupervisorProtocol.INIT;
	private final int dir;

	public SupervisorDecoder(int dir) {
		this.dir = dir;
	}

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
		int id = arg1.readUnsignedByte();
		EaglerSupervisorPacket pkt = protocol.createPacket(id, dir);
		if (pkt != null) {
			pkt.readPacket(arg1);
			arg2.add(pkt);
		} else {
			throw new IllegalStateException("Recieved wrong packet type " + id + " on "
					+ EaglerSupervisorProtocol.dirStr(id) + " type " + protocol.name() + " decoder");
		}
	}

	public void setConnectionProtocol(EaglerSupervisorProtocol protocol) {
		this.protocol = protocol;
	}

}