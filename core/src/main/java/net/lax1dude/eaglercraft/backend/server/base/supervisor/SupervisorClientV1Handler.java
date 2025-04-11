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

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomCapeGeneric;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomCapePlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomSkinGeneric;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.CustomSkinPlayer;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc.SupervisorRPCHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.*;

public class SupervisorClientV1Handler implements EaglerSupervisorHandler {

	private final SupervisorService<?> service;
	private final SupervisorConnection connection;
	private final SupervisorRPCHandler rpcHandler;

	public SupervisorClientV1Handler(SupervisorConnection connection) {
		this.service = connection.service;
		this.connection = connection;
		this.rpcHandler = service.getRPCHandler();
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
		connection.onDropPlayer(pkt.uuid);
	}

	@Override
	public void handleServer(SPacketSvDropPlayerPartial pkt) {
		if(pkt.bitmask != 0) {
			connection.loadPlayer(pkt.uuid).onDropPartial(pkt.serverNotify,
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
		SupervisorPlayer player = connection.loadPlayerIfPresent(pkt.uuid);
		IEaglerPlayerSkin skin = InternUtils.getPresetSkin(pkt.presetSkin);
		if (player != null) {
			player.onSkinReceived(skin);
		}else {
			if(!service.resolver.onForeignSkinReceived(pkt.uuid, skin)) {
				service.logger().warn("Received skin response from supervisor for unknown skin " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherSkinCustom pkt) {
		SupervisorPlayer player = connection.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onSkinReceived(CustomSkinPlayer.createV4(pkt.uuid.getMostSignificantBits(),
					pkt.uuid.getLeastSignificantBits(), pkt.model, pkt.customSkin));
		}else {
			if(!service.resolver.onForeignSkinReceived(pkt.uuid, CustomSkinGeneric.createV4(pkt.model, pkt.customSkin))) {
				service.logger().warn("Received skin response from supervisor for unknown skin " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherSkinError pkt) {
		SupervisorPlayer player = connection.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onSkinError();
		}else {
			if(!service.resolver.onForeignSkinReceived(pkt.uuid, MissingSkin.MISSING_SKIN)) {
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
		SupervisorPlayer player = connection.loadPlayerIfPresent(pkt.uuid);
		IEaglerPlayerCape cape = InternUtils.getPresetCape(pkt.presetCape);
		if (player != null) {
			player.onCapeReceived(cape);
		}else {
			if(!service.resolver.onForeignCapeReceived(pkt.uuid, cape)) {
				service.logger().warn("Received cape response from supervisor for unknown cape " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherCapeCustom pkt) {
		SupervisorPlayer player = connection.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onCapeReceived(new CustomCapePlayer(pkt.uuid.getMostSignificantBits(),
					pkt.uuid.getLeastSignificantBits(), pkt.customCape));
		}else {
			if(!service.resolver.onForeignCapeReceived(pkt.uuid, new CustomCapeGeneric(pkt.customCape))) {
				service.logger().warn("Received cape response from supervisor for unknown cape " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvOtherCapeError pkt) {
		SupervisorPlayer player = connection.loadPlayerIfPresent(pkt.uuid);
		if (player != null) {
			player.onCapeError();
		}else {
			if(!service.resolver.onForeignCapeReceived(pkt.uuid, MissingCape.MISSING_CAPE)) {
				service.logger().warn("Received cape error from supervisor for unknown cape " + pkt.uuid);
			}
		}
	}

	@Override
	public void handleServer(SPacketSvPlayerNodeID pkt) {
		connection.loadPlayer(pkt.playerUUID).onNodeIDReceived(pkt.nodeId, pkt.brandUUID);
	}

	@Override
	public void handleServer(SPacketSvDropAllPlayers pkt) {
		connection.onDropAllPlayers(pkt.nodeId);
	}

	@Override
	public void handleServer(SPacketSvAcceptPlayer pkt) {
		connection.onPlayerAccept(pkt.playerUUID, EnumAcceptPlayer.ACCEPT);
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
		connection.onPlayerAccept(pkt.playerUUID, result);
	}

	@Override
	public void handleServer(SPacketSvClientBrandError pkt) {
		connection.loadPlayer(pkt.playerUUID).onNodeIDError();
	}

	@Override
	public void handleServer(SPacketSvRPCExecute pkt) {
		rpcHandler.onRPCExecute(connection, pkt.requestUUID, pkt.sourceNodeId,
				pkt.payload.readCharSequence(pkt.nameLength, StandardCharsets.US_ASCII).toString(), pkt.payload);
	}

	@Override
	public void handleServer(SPacketSvRPCExecuteVoid pkt) {
		rpcHandler.onRPCExecuteVoid(pkt.sourceNodeId,
				pkt.payload.readCharSequence(pkt.nameLength, StandardCharsets.US_ASCII).toString(), pkt.payload);
	}

	@Override
	public void handleServer(SPacketSvRPCResultSuccess pkt) {
		rpcHandler.onRPCResultSuccess(pkt.requestUUID, pkt.dataBuffer);
	}

	@Override
	public void handleServer(SPacketSvRPCResultFail pkt) {
		rpcHandler.onRPCResultFail(pkt.requestUUID, pkt.failureType);
	}

	@Override
	public void handleServer(SPacketSvRPCResultMulti pkt) {
		rpcHandler.onRPCResultMulti(pkt.requestUUID, pkt.results);
	}

	@Override
	public void handleDisconnected() {
		service.handleDisconnected();
	}

}