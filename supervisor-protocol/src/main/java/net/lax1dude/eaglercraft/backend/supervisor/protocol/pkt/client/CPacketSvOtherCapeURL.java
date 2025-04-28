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
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class CPacketSvOtherCapeURL implements EaglerSupervisorPacket {

	public UUID uuid;
	public String url;

	public CPacketSvOtherCapeURL() {
	}

	public CPacketSvOtherCapeURL(UUID uuid, String url) {
		this.uuid = uuid;
		this.url = url;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		int urlLen = buffer.readUnsignedShort();
		url = buffer.readCharSequence(urlLen, StandardCharsets.US_ASCII).toString();
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		byte[] urlBytes = url.getBytes(StandardCharsets.US_ASCII);
		int len = urlBytes.length;
		if (len > 0xFFFF) {
			throw new UnsupportedOperationException("Cape URL is longer than 65535 bytes");
		}
		buffer.writeShort(len);
		buffer.writeBytes(urlBytes);
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleClient(this);
	}

}