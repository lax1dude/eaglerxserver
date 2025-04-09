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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class SupervisorEncoder extends MessageToByteEncoder<EaglerSupervisorPacket> {

	private EaglerSupervisorProtocol protocol = EaglerSupervisorProtocol.INIT;
	private final int dir;

	public SupervisorEncoder(int dir) {
		this.dir = dir;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, EaglerSupervisorPacket pktIn, ByteBuf bytesOut) throws Exception {
		Class<? extends EaglerSupervisorPacket> cls = pktIn.getClass();
		int id = protocol.getPacketID(cls, dir);
		if(id != -1) {
			bytesOut.writeByte(id);
			pktIn.writePacket(bytesOut);
		}else {
			throw new IllegalStateException("Sent wrong packet type " + cls.getSimpleName() + " on "
					+ EaglerSupervisorProtocol.dirStr(dir) + " type " + protocol.name() + " encoder");
		}
	}

	public void setConnectionProtocol(EaglerSupervisorProtocol protocol) {
		this.protocol = protocol;
	}

}