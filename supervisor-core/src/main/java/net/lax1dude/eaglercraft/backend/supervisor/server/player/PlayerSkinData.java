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

package net.lax1dude.eaglercraft.backend.supervisor.server.player;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherSkinCustom;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherSkinError;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherSkinPreset;

public abstract class PlayerSkinData {

	public static final PlayerSkinData ERROR = new Preset(0) {
		@Override
		public EaglerSupervisorPacket makeResponse(UUID playerUUID) {
			return new SPacketSvOtherSkinError(playerUUID);
		}
	};

	private PlayerSkinData() {
	}

	public static class Preset extends PlayerSkinData {

		public final int presetId;

		public Preset(int presetId) {
			this.presetId = presetId;
		}

		@Override
		public EaglerSupervisorPacket makeResponse(UUID playerUUID) {
			return new SPacketSvOtherSkinPreset(playerUUID, presetId);
		}

	}

	public static PlayerSkinData create(int presetId) {
		return new Preset(presetId);
	}

	public static class Custom extends PlayerSkinData {

		public final int modelId;
		public final byte[] customSkin;

		public Custom(int modelId, byte[] customSkin) {
			this.modelId = modelId;
			this.customSkin = customSkin;
		}

		@Override
		public EaglerSupervisorPacket makeResponse(UUID playerUUID) {
			return new SPacketSvOtherSkinCustom(playerUUID, modelId, customSkin);
		}

	}

	public static PlayerSkinData create(int modelId, byte[] customSkin) {
		return new Custom(modelId, customSkin);
	}

	public abstract EaglerSupervisorPacket makeResponse(UUID playerUUID);

}