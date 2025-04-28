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

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinPresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;

public class PresetSkinPlayer extends BasePresetSkin {

	private final SPacketOtherSkinPresetEAG packet;

	public PresetSkinPlayer(long uuidMost, long uuidLeast, int presetId) {
		this.packet = new SPacketOtherSkinPresetEAG(uuidMost, uuidLeast, presetId);
	}

	public PresetSkinPlayer(SPacketOtherSkinPresetEAG packet) {
		this.packet = packet;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		if(rewriteUUIDMost == packet.uuidMost && rewriteUUIDLeast == packet.uuidLeast) {
			return packet;
		}else {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, packet.presetSkin);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		if(rewriteUUIDMost == packet.uuidMost && rewriteUUIDLeast == packet.uuidLeast) {
			return packet;
		}else {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, packet.presetSkin);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, int rewriteModelIdRaw,
			GamePluginMessageProtocol protocol) {
		if(rewriteUUIDMost == packet.uuidMost && rewriteUUIDLeast == packet.uuidLeast) {
			return packet;
		}else {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, packet.presetSkin);
		}
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinPresetV4EAG(packet.presetSkin);
	}

	@Override
	public boolean isSkinPreset() {
		return true;
	}

	@Override
	public int getPresetSkinId() {
		return packet.presetSkin;
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		return EnumPresetSkins.getByIdOrDefault(packet.presetSkin);
	}

	@Override
	public boolean isSkinCustom() {
		return false;
	}

	@Override
	public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public int getCustomSkinRawModelId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	protected int presetId() {
		return packet.presetSkin;
	}

}
