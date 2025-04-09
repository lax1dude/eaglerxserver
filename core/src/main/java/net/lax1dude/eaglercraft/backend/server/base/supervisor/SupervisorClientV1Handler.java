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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.*;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class SupervisorClientV1Handler implements EaglerSupervisorHandler {

	private final SupervisorService<?> service;
	private final SupervisorConnection connection;

	public SupervisorClientV1Handler(SupervisorConnection connection) {
		this.service = connection.service;
		this.connection = connection;
	}

	@Override
	public void handleServer(SPacketSvPing pkt) {
		connection.sendSupervisorPacket(new CPacketSvPong());
	}

	@Override
	public void handleServer(SPacketSvPong pkt) {
		connection.onPongPacket();
	}

	@Override
	public void handleServer(SPacketSvTotalPlayerCount pkt) {
		connection.onPlayerCount(pkt.playerCount, pkt.playerMax);
	}

	@Override
	public void handleServer(SPacketSvDropPlayer pkt) {
		service.onDropPlayer(pkt.uuid);
	}

	@Override
	public void handleServer(SPacketSvDropPlayerPartial pkt) {
		if(pkt.bitmask != 0) {
			service.loadPlayer(pkt.uuid).onDropPartial(pkt.serverNotify,
					(pkt.bitmask & SPacketSvDropPlayerPartial.DROP_PLAYER_SKIN) != 0,
					(pkt.bitmask & SPacketSvDropPlayerPartial.DROP_PLAYER_CAPE) != 0);
		}
	}

	@Override
	public void handleServer(SPacketSvGetOtherSkin pkt) {
		connection.lookupHandler.handleSupervisorSkinLookup(pkt.uuid);
	}

	@Override
	public void handleServer(SPacketSvOtherSkinPreset pkt) {
		SupervisorPlayer player = service.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onSkinReceived(SkinPacketVersionCache.createPreset(pkt.uuid.getMostSignificantBits(),
					pkt.uuid.getLeastSignificantBits(), pkt.presetSkin));
		}else {
			if(!service.resolver.onForeignSkinReceivedPreset(pkt.uuid, pkt.presetSkin)) {
				service.logger().warn("Received skin response from supervisor for unknown skin " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherSkinCustom pkt) {
		SupervisorPlayer player = service.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onSkinReceived(SkinPacketVersionCache.createCustomV4(pkt.uuid.getMostSignificantBits(),
					pkt.uuid.getLeastSignificantBits(), pkt.model, pkt.customSkin));
		}else {
			if(!service.resolver.onForeignSkinReceivedCustom(pkt.uuid, pkt.model, pkt.customSkin)) {
				service.logger().warn("Received skin response from supervisor for unknown skin " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherSkinError pkt) {
		SupervisorPlayer player = service.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onSkinError();
		}else {
			if(!service.resolver.onForeignSkinReceivedError(pkt.uuid)) {
				service.logger().warn("Received skin error from supervisor for unknown skin " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvGetOtherCape pkt) {
		connection.lookupHandler.handleSupervisorCapeLookup(pkt.uuid);
	}

	@Override
	public void handleServer(SPacketSvOtherCapePreset pkt) {
		SupervisorPlayer player = service.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onCapeReceived(new SPacketOtherCapePresetEAG(pkt.uuid.getMostSignificantBits(),
					pkt.uuid.getLeastSignificantBits(), pkt.presetCape));
		}else {
			if(!service.resolver.onForeignCapeReceivedPreset(pkt.uuid, pkt.presetCape)) {
				service.logger().warn("Received cape response from supervisor for unknown cape " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherCapeCustom pkt) {
		SupervisorPlayer player = service.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onCapeReceived(new SPacketOtherCapeCustomEAG(pkt.uuid.getMostSignificantBits(),
					pkt.uuid.getLeastSignificantBits(), pkt.customCape));
		}else {
			if(!service.resolver.onForeignCapeReceivedCustom(pkt.uuid, pkt.customCape)) {
				service.logger().warn("Received cape response from supervisor for unknown cape " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherCapeError pkt) {
		SupervisorPlayer player = service.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onCapeError();
		}else {
			if(!service.resolver.onForeignCapeReceivedError(pkt.uuid)) {
				service.logger().warn("Received cape error from supervisor for unknown cape " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvPlayerNodeID pkt) {
		service.loadPlayer(pkt.playerUUID).onNodeIDReceived(pkt.nodeId, pkt.brandUUID);
	}

	@Override
	public void handleServer(SPacketSvDropAllPlayers pkt) {
		service.onDropAllPlayers(pkt.nodeId);
	}

	@Override
	public void handleServer(SPacketSvAcceptPlayer pkt) {
		service.onPlayerAccept(pkt.playerUUID, EnumAcceptPlayer.ACCEPT);
	}

	@Override
	public void handleServer(SPacketSvRejectPlayer pkt) {
		EnumAcceptPlayer result;
		switch(pkt.cause) {
		case SPacketSvRejectPlayer.CAUSE_DUPLICATE_USERNAME:
			result = EnumAcceptPlayer.REJECT_DUPLICATE_USERNAME;
			break;
		case SPacketSvRejectPlayer.CAUSE_DUPLICATE_UUID:
			result = EnumAcceptPlayer.REJECT_DUPLICATE_UUID;
			break;
		default:
			result = EnumAcceptPlayer.REJECT_UNKNOWN;
			break;
		}
		service.onPlayerAccept(pkt.playerUUID, result);
	}

	@Override
	public void handleServer(SPacketSvClientBrandError pkt) {
		service.loadPlayer(pkt.playerUUID).onNodeIDError();
	}

	@Override
	public void handleServer(SPacketSvRPCExecute pkt) {
		//controller.getRPCHandler().onRPCExecute(pkt.requestUUID, pkt.sourceNodeId,
		//		new String(pkt.name, StandardCharsets.US_ASCII), pkt.dataBuffer);
	}

	@Override
	public void handleServer(SPacketSvRPCExecuteVoid pkt) {
		//controller.getRPCHandler().onRPCExecuteVoid(pkt.sourceNodeId,
		//		new String(pkt.name, StandardCharsets.US_ASCII), pkt.dataBuffer);
	}

	@Override
	public void handleServer(SPacketSvRPCResultSuccess pkt) {
		//controller.getRPCHandler().onRPCResultSuccess(pkt.requestUUID, pkt.dataBuffer);
	}

	@Override
	public void handleServer(SPacketSvRPCResultFail pkt) {
		//controller.getRPCHandler().onRPCResultFail(pkt.requestUUID, pkt.failureType);
	}

	@Override
	public void handleServer(SPacketSvRPCResultMulti pkt) {
		//controller.getRPCHandler().onRPCResultMulti(pkt.requestUUID, pkt.results);
	}

	@Override
	public void handleDisconnected() {
		service.handleDisconnected();
	}

}