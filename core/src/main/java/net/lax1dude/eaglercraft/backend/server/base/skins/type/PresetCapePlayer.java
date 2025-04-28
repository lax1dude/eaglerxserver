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

package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapePresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetV5EAG;

public class PresetCapePlayer extends BasePresetCape {

	private final SPacketOtherCapePresetEAG packet;

	public PresetCapePlayer(long uuidMost, long uuidLeast, int presetId) {
		this.packet = new SPacketOtherCapePresetEAG(uuidMost, uuidLeast, presetId);
	}

	public PresetCapePlayer(SPacketOtherCapePresetEAG packet) {
		this.packet = packet;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		if(protocol.ver <= 4) {
			if(rewriteUUIDMost == packet.uuidMost && rewriteUUIDLeast == packet.uuidLeast) {
				return packet;
			}else {
				return new SPacketOtherCapePresetEAG(rewriteUUIDMost, rewriteUUIDLeast, packet.presetCape);
			}
		}else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getCapePacket(int requestId, GamePluginMessageProtocol protocol) {
		if(protocol.ver >= 5) {
			return new SPacketOtherCapePresetV5EAG(requestId, packet.presetCape);
		}else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getForceCapePacketV4() {
		return new SPacketForceClientCapePresetV4EAG(packet.presetCape);
	}

	@Override
	public boolean isCapeEnabled() {
		return packet.presetCape != 0;
	}

	@Override
	public boolean isCapePreset() {
		return true;
	}

	@Override
	public int getPresetCapeId() {
		return packet.presetCape;
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		return EnumPresetCapes.getByIdOrDefault(packet.presetCape);
	}

	@Override
	public boolean isCapeCustom() {
		return false;
	}

	@Override
	public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	protected int presetId() {
		return packet.presetCape;
	}

}
