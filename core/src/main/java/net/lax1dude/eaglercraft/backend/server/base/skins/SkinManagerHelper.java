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
		if(server != null) {
			server.forEachPlayer((player) -> {
				EaglerPlayerInstance<PlayerObject> playerObj = player
						.<BasePlayerInstance<PlayerObject>>getPlayerAttachment().asEaglerPlayer();
				if (playerObj != null && playerObj != playerIn) {
					playerObj.writePacket(invalidatePacket);
				}
			});
		}
		playerIn.getEaglerXServer().getSupervisorService().notifySkinChange(uuid, server.getServerConfName(), skin, cape);
	}

}
