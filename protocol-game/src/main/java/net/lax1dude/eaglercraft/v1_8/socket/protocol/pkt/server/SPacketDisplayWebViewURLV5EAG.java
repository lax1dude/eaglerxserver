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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class SPacketDisplayWebViewURLV5EAG implements GameMessagePacket {

	public static final int FLAG_PERMS_JAVASCRIPT = 1;
	public static final int FLAG_PERMS_MESSAGE_API = 2;
	public static final int FLAG_PERMS_STRICT_CSP = 4;

	public int flags;
	public String embedTitle;
	public String embedURL;

	public SPacketDisplayWebViewURLV5EAG() {
	}

	public SPacketDisplayWebViewURLV5EAG(int flags, String embedTitle, String embedURL) {
		this.flags = flags;
		this.embedTitle = embedTitle;
		this.embedURL = embedURL;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		flags = buffer.readUnsignedByte();
		embedTitle = buffer.readStringMC(255);
		embedURL = buffer.readStringEaglerASCII16();
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		buffer.writeByte(flags);
		buffer.writeStringMC(embedTitle);
		buffer.writeStringEaglerASCII16(embedURL);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
