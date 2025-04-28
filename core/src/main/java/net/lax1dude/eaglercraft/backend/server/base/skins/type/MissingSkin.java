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

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinPresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetV5EAG;

public class MissingSkin extends BasePresetSkin {

	public static final IEaglerPlayerSkin MISSING_SKIN = new MissingSkin(EnumPresetSkins.DEFAULT_STEVE);
	public static final IEaglerPlayerSkin MISSING_SKIN_ALEX = new MissingSkin(EnumPresetSkins.DEFAULT_ALEX);

	// used for supervisor
	public static final IEaglerPlayerSkin UNAVAILABLE_SKIN = new MissingSkin(EnumPresetSkins.DEFAULT_STEVE);

	private final int skinId;
	private final EnumPresetSkins enumSkin;

	private MissingSkin(EnumPresetSkins enumSkin) {
		this.skinId = enumSkin.getId();
		this.enumSkin = enumSkin;
	}

	public static IEaglerPlayerSkin forPlayerUUID(UUID playerUUID) {
		return (playerUUID.hashCode() & 1) != 0 ? MISSING_SKIN_ALEX : MISSING_SKIN;
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		if (protocol.ver <= 4) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, skinId);
		} else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		if (protocol.ver <= 4) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, skinId);
		} else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, int rewriteModelIdRaw,
			GamePluginMessageProtocol protocol) {
		if (protocol.ver <= 4) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, skinId);
		} else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(int requestId, GamePluginMessageProtocol protocol) {
		if (protocol.ver >= 5) {
			return new SPacketOtherSkinPresetV5EAG(requestId, skinId);
		} else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(int requestId, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		if (protocol.ver >= 5) {
			return new SPacketOtherSkinPresetV5EAG(requestId, skinId);
		} else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(int requestId, int rewriteModelIdRaw, GamePluginMessageProtocol protocol) {
		if (protocol.ver >= 5) {
			return new SPacketOtherSkinPresetV5EAG(requestId, skinId);
		} else {
			throw UnsafeUtil.wrongProtocol(protocol);
		}
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinPresetV4EAG(0);
	}

	@Override
	public boolean isSkinPreset() {
		return true;
	}

	@Override
	public int getPresetSkinId() {
		return skinId;
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		return enumSkin;
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
		return skinId;
	}

}
