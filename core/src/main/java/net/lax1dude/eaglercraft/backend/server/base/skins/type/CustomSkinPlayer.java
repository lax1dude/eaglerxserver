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
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV3EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV5EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class CustomSkinPlayer extends BaseCustomSkin implements IModelRewritable {

	private SPacketOtherSkinCustomV3EAG packetV3;
	private SPacketOtherSkinCustomV4EAG packetV4;

	private CustomSkinPlayer(SPacketOtherSkinCustomV3EAG packetV3, SPacketOtherSkinCustomV4EAG packetV4) {
		this.packetV3 = packetV3;
		this.packetV4 = packetV4;
	}

	public static CustomSkinPlayer createV3(long uuidMost, long uuidLeast, int modelId, byte[] textureDataV3) {
		return new CustomSkinPlayer(new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, modelId, textureDataV3), null);
	}

	public static CustomSkinPlayer createV4(long uuidMost, long uuidLeast, int modelId, byte[] textureDataV4) {
		return new CustomSkinPlayer(null, new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, modelId, textureDataV4));
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		switch(protocol) {
		case V3:
			SPacketOtherSkinCustomV3EAG packetV3 = packetV3();
			if(rewriteUUIDMost == packetV3.uuidMost && rewriteUUIDLeast == packetV3.uuidLeast) {
				return packetV3;
			}else {
				return new SPacketOtherSkinCustomV3EAG(rewriteUUIDMost, rewriteUUIDLeast, packetV3.modelID, packetV3.customSkin);
			}
		case V4:
			SPacketOtherSkinCustomV4EAG packetV4 = packetV4();
			if(rewriteUUIDMost == packetV4.uuidMost && rewriteUUIDLeast == packetV4.uuidLeast) {
				return packetV4;
			}else {
				return new SPacketOtherSkinCustomV4EAG(rewriteUUIDMost, rewriteUUIDLeast, packetV4.modelID, packetV4.customSkin);
			}
		default:
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelId.getId(), protocol);
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, int rewriteModelIdRaw,
			GamePluginMessageProtocol protocol) {
		switch(protocol) {
		case V3:
			SPacketOtherSkinCustomV3EAG packetV3 = packetV3();
			if(rewriteUUIDMost == packetV3.uuidMost && rewriteUUIDLeast == packetV3.uuidLeast && rewriteModelIdRaw == packetV3.modelID) {
				return packetV3;
			}else {
				return new SPacketOtherSkinCustomV3EAG(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelIdRaw, packetV3.customSkin);
			}
		case V4:
			SPacketOtherSkinCustomV4EAG packetV4 = packetV4();
			if(rewriteUUIDMost == packetV4.uuidMost && rewriteUUIDLeast == packetV4.uuidLeast && rewriteModelIdRaw == packetV4.modelID) {
				return packetV4;
			}else {
				return new SPacketOtherSkinCustomV4EAG(rewriteUUIDMost, rewriteUUIDLeast, rewriteModelIdRaw, packetV4.customSkin);
			}
		default:
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(int requestId, GamePluginMessageProtocol protocol) {
		if(protocol.ver >= 5) {
			SPacketOtherSkinCustomV4EAG packetV4 = packetV4();
			return new SPacketOtherSkinCustomV5EAG(requestId, packetV4.modelID, packetV4.customSkin);
		}else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(int requestId, EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
		return getSkinPacket(requestId, rewriteModelId.getId(), protocol);
	}

	@Override
	public GameMessagePacket getSkinPacket(int requestId, int rewriteModelIdRaw, GamePluginMessageProtocol protocol) {
		if(protocol.ver >= 5) {
			return new SPacketOtherSkinCustomV5EAG(requestId, rewriteModelIdRaw, textureDataV4());
		}else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinCustomV4EAG(modelId(), textureDataV4());
	}

	@Override
	public boolean isSkinPreset() {
		return false;
	}

	@Override
	public int getPresetSkinId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public boolean isSkinCustom() {
		return true;
	}

	@Override
	public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
		System.arraycopy(textureDataV3(), 0, array, offset, 16384);
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		System.arraycopy(textureDataV4(), 0, array, offset, 12288);
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		return EnumSkinModel.getById(modelId());
	}

	@Override
	public int getCustomSkinRawModelId() {
		return modelId();
	}

	@Override
	protected int modelId() {
		if(packetV4 != null) {
			return packetV4.modelID;
		}else {
			return packetV3.modelID;
		}
	}

	protected SPacketOtherSkinCustomV3EAG packetV3() {
		if(packetV3 != null) {
			return packetV3;
		}else {
			return packetV3 = (SPacketOtherSkinCustomV3EAG) SkinPacketVersionCache.convertToV3(packetV4);
		}
	}

	protected SPacketOtherSkinCustomV4EAG packetV4() {
		if(packetV4 != null) {
			return packetV4;
		}else {
			return packetV4 = (SPacketOtherSkinCustomV4EAG) SkinPacketVersionCache.convertToV4(packetV3);
		}
	}

	@Override
	protected byte[] textureDataV3() {
		return packetV3().customSkin;
	}

	@Override
	protected byte[] textureDataV4() {
		return packetV4().customSkin;
	}

	@Override
	public IEaglerPlayerSkin rewriteModelInternal(int modelId) {
		if(modelId != modelId()) {
			return new CustomSkinModelRw(this, modelId);
		}else {
			return this;
		}
	}

}
