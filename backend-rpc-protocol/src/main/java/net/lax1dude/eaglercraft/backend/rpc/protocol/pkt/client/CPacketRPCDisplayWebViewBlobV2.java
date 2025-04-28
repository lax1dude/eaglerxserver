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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCDisplayWebViewBlobV2 implements EaglerBackendRPCPacket {

	public static final int FLAG_PERMS_JAVASCRIPT = 1;
	public static final int FLAG_PERMS_MESSAGE_API = 2;
	public static final int FLAG_PERMS_STRICT_CSP = 4;

	public int flags;
	public String embedTitle;
	public byte[] embedHash;

	public CPacketRPCDisplayWebViewBlobV2() {
	}

	public CPacketRPCDisplayWebViewBlobV2(int flags, String embedTitle, byte[] embedHash) {
		this.flags = flags;
		this.embedTitle = embedTitle;
		this.embedHash = embedHash;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		flags = buffer.readUnsignedByte();
		embedTitle = EaglerBackendRPCPacket.readString(buffer, 255, true, StandardCharsets.UTF_8);
		embedHash = new byte[20];
		buffer.readFully(embedHash);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeByte(flags);
		EaglerBackendRPCPacket.writeString(buffer, embedTitle, true, StandardCharsets.UTF_8);
		if (embedHash.length != 20) {
			throw new IOException("Hash is not 20 bytes");
		}
		buffer.write(embedHash);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
