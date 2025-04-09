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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class SPacketSvHandshakeFailure implements EaglerSupervisorPacket {

	public static final int FAILURE_CODE_OUTDATED_SERVER = 0;
	public static final int FAILURE_CODE_OUTDATED_CLIENT = 1;
	public static final int FAILURE_CODE_INVALID_SECRET = 2;
	public static final int FAILURE_CODE_INTERNAL_ERROR = 0xFF;

	public int failureCode;

	public SPacketSvHandshakeFailure() {
	}

	public SPacketSvHandshakeFailure(int failureCode) {
		this.failureCode = failureCode;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		failureCode = buffer.readUnsignedByte();
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeByte(failureCode);
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleServer(this);
	}

	public static String failureCodeToString(int failureCode) {
		switch(failureCode) {
		case FAILURE_CODE_OUTDATED_SERVER:
			return "FAILURE_CODE_OUTDATED_SERVER";
		case FAILURE_CODE_OUTDATED_CLIENT:
			return "FAILURE_CODE_OUTDATED_CLIENT";
		case FAILURE_CODE_INVALID_SECRET:
			return "FAILURE_CODE_INVALID_SECRET";
		case FAILURE_CODE_INTERNAL_ERROR:
			return "FAILURE_CODE_INTERNAL_ERROR";
		default:
			return null;
		}
	}

}