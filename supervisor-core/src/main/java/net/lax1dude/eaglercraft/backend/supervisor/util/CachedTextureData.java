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

package net.lax1dude.eaglercraft.backend.supervisor.util;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherCapeCustom;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherCapeError;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherSkinCustom;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvOtherSkinError;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerCapeData;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.PlayerSkinData;

public class CachedTextureData {

	public static EaglerSupervisorPacket makeSkinResponse(byte[] data, UUID uuid, int modelId) {
		if(data == ISkinCacheService.ERROR) {
			return new SPacketSvOtherSkinError(uuid);
		}
		if(data.length != 12288) {
			throw new IllegalArgumentException("Skin data is the wrong length");
		}
		return new SPacketSvOtherSkinCustom(uuid, modelId, data);
	}

	public static EaglerSupervisorPacket makeCapeResponse(byte[] data, UUID uuid) {
		if(data == ISkinCacheService.ERROR) {
			return new SPacketSvOtherCapeError(uuid);
		}
		if(data.length != 1173) {
			throw new IllegalArgumentException("Cape data is the wrong length");
		}
		return new SPacketSvOtherCapeCustom(uuid, data);
	}

	public static PlayerSkinData toSkinData(byte[] data, int modelId) {
		if(data == ISkinCacheService.ERROR) {
			return PlayerSkinData.ERROR;
		}
		if(data.length != 12288) {
			throw new IllegalArgumentException("Skin data is the wrong length");
		}
		return PlayerSkinData.create(modelId, data);
	}

	public static PlayerCapeData toCapeData(byte[] data) {
		if(data == ISkinCacheService.ERROR) {
			return PlayerCapeData.ERROR;
		}
		if(data.length != 1173) {
			throw new IllegalArgumentException("Cape data is the wrong length");
		}
		return PlayerCapeData.create(data);
	}

}