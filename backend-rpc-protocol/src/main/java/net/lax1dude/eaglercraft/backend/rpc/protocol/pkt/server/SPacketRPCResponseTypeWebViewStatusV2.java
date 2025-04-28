/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCResponseTypeWebViewStatusV2 implements EaglerBackendRPCPacket {

	public int requestID;
	public boolean webViewAllowed;
	public boolean channelAllowed;
	public Collection<String> openChannels;

	public SPacketRPCResponseTypeWebViewStatusV2() {
	}

	public SPacketRPCResponseTypeWebViewStatusV2(int requestID, boolean webViewAllowed, boolean channelAllowed,
			Collection<String> openChannels) {
		this.requestID = requestID;
		this.webViewAllowed = webViewAllowed;
		this.channelAllowed = channelAllowed;
		this.openChannels = openChannels;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		int bits = buffer.readUnsignedByte();
		webViewAllowed = (bits & 1) != 0;
		channelAllowed = (bits & 2) != 0;
		int channelCnt = buffer.readUnsignedByte();
		if (channelCnt > 0) {
			String[] ch = new String[channelCnt];
			for (int i = 0; i < channelCnt; ++i) {
				ch[i] = EaglerBackendRPCPacket.readString(buffer, 255, false, StandardCharsets.US_ASCII);
			}
			openChannels = Arrays.asList(ch);
		} else {
			openChannels = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		buffer.writeByte((webViewAllowed ? 1 : 0) | (channelAllowed ? 2 : 0));
		if (openChannels != null) {
			int cnt = openChannels.size();
			if (cnt > 255) {
				throw new IOException("Too many open channels!");
			}
			buffer.writeByte(cnt);
			if (cnt > 0) {
				if (openChannels instanceof RandomAccess) {
					List<String> lst = (List<String>) openChannels;
					for (int i = 0; i < cnt; ++i) {
						EaglerBackendRPCPacket.writeString(buffer, lst.get(i), false, StandardCharsets.US_ASCII);
					}
				} else {
					for (String str : openChannels) {
						EaglerBackendRPCPacket.writeString(buffer, str, false, StandardCharsets.US_ASCII);
					}
				}
			}
		} else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int l = 6;
		if (openChannels != null) {
			int cnt = openChannels.size();
			if (cnt > 0) {
				l += cnt;
				if (openChannels instanceof RandomAccess) {
					List<String> lst = (List<String>) openChannels;
					for (int i = 0; i < cnt; ++i) {
						l += lst.get(i).length();
					}
				} else {
					for (String str : openChannels) {
						l += str.length();
					}
				}
			}
		}
		return l;
	}

}