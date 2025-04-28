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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class CPacketSvHandshake implements EaglerSupervisorPacket {

	public int[] supportedProtocols;
	public String secretKey;

	public CPacketSvHandshake() {
	}

	public CPacketSvHandshake(int[] supportedProtocols, String secretKey) {
		this.supportedProtocols = supportedProtocols;
		this.secretKey = secretKey;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		supportedProtocols = new int[buffer.readUnsignedShort()];
		for (int i = 0; i < supportedProtocols.length; ++i) {
			supportedProtocols[i] = buffer.readUnsignedShort();
		}
		int keyLen = buffer.readUnsignedShort();
		if (keyLen > 0) {
			secretKey = buffer.readCharSequence(keyLen, StandardCharsets.UTF_8).toString();
		} else {
			secretKey = null;
		}
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeShort(supportedProtocols.length);
		for (int i = 0; i < supportedProtocols.length; ++i) {
			buffer.writeShort(supportedProtocols[i]);
		}
		if (secretKey != null) {
			byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
			int keyLen = bytes.length;
			if (keyLen > 0xFFFF) {
				throw new UnsupportedOperationException("Secret key is longer than 65535 bytes");
			}
			buffer.writeShort(keyLen);
			if (keyLen > 0) {
				buffer.writeBytes(bytes);
			}
		} else {
			buffer.writeShort(0);
		}
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleClient(this);
	}

}