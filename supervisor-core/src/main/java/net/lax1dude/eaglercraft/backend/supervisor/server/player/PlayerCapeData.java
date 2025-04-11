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
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherCapeCustom;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherCapeError;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherCapePreset;

public abstract class PlayerCapeData {

	public static final PlayerCapeData ERROR = new Preset(0) {
		@Override
		public EaglerSupervisorPacket makeResponse(UUID playerUUID) {
			return new SPacketSvOtherCapeError(playerUUID);
		}
	};

	private PlayerCapeData() {
	}

	public static class Preset extends PlayerCapeData {

		public final int presetId;

		public Preset(int presetId) {
			this.presetId = presetId;
		}

		@Override
		public EaglerSupervisorPacket makeResponse(UUID playerUUID) {
			return new SPacketSvOtherCapePreset(playerUUID, presetId);
		}

	}

	public static PlayerCapeData create(int presetId) {
		return new Preset(presetId);
	}

	public static class Custom extends PlayerCapeData {

		public final byte[] customCape;

		public Custom(byte[] customCape) {
			this.customCape = customCape;
		}

		@Override
		public EaglerSupervisorPacket makeResponse(UUID playerUUID) {
			return new SPacketSvOtherCapeCustom(playerUUID, customCape);
		}

	}

	public static PlayerCapeData create(byte[] customSkin) {
		return new Custom(customSkin);
	}

	public abstract EaglerSupervisorPacket makeResponse(UUID playerUUID);

}