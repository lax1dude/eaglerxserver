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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketInvalidatePlayerCacheV4EAG;

class SkinManagerHelper {

	static <PlayerObject> void notifyOthers(BasePlayerInstance<PlayerObject> playerIn, boolean skin, boolean cape) {
		UUID uuid = playerIn.getUniqueId();
		SPacketInvalidatePlayerCacheV4EAG invalidatePacket = new SPacketInvalidatePlayerCacheV4EAG(skin, cape,
				uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
		IPlatformServer<PlayerObject> server = playerIn.getPlatformPlayer().getServer();
		if (server != null) {
			server.forEachPlayer((player) -> {
				EaglerPlayerInstance<PlayerObject> playerObj = player
						.<BasePlayerInstance<PlayerObject>>getPlayerAttachment().asEaglerPlayer();
				if (playerObj != null && playerObj != playerIn) {
					playerObj.writePacket(invalidatePacket);
				}
			});
		}
		playerIn.getEaglerXServer().getSupervisorService().notifySkinChange(uuid, server.getServerConfName(), skin,
				cape);
	}

}
