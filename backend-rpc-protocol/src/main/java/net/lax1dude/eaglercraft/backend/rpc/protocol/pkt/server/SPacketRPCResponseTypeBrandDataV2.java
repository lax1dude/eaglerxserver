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
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCResponseTypeBrandDataV2 implements EaglerBackendRPCPacket {

	public String brand;
	public String version;
	public UUID uuid;

	public SPacketRPCResponseTypeBrandDataV2() {
	}

	public SPacketRPCResponseTypeBrandDataV2(String brand, String version, UUID uuid) {
		this.brand = brand;
		this.version = version;
		this.uuid = uuid;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		brand = EaglerBackendRPCPacket.readString(buffer, 255, false, StandardCharsets.US_ASCII);
		version = EaglerBackendRPCPacket.readString(buffer, 255, false, StandardCharsets.US_ASCII);
		uuid = new UUID(buffer.readLong(), buffer.readLong());
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		EaglerBackendRPCPacket.writeString(buffer, brand, false, StandardCharsets.US_ASCII);
		EaglerBackendRPCPacket.writeString(buffer, version, false, StandardCharsets.US_ASCII);
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 18 + brand.length() + version.length();
	}

}