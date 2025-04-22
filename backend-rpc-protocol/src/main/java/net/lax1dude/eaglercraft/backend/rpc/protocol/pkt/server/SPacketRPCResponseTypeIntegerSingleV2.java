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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.IInteger;

public class SPacketRPCResponseTypeIntegerSingleV2 implements EaglerBackendRPCPacket, IInteger {

	public int requestID;
	public int value;

	public SPacketRPCResponseTypeIntegerSingleV2() {
	}

	public SPacketRPCResponseTypeIntegerSingleV2(int requestID, int value) {
		this.requestID = requestID;
		this.value = value;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		value = buffer.readInt();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		buffer.writeInt(value);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 8;
	}

	@Override
	public int getIntValue() {
		return value;
	}

}
