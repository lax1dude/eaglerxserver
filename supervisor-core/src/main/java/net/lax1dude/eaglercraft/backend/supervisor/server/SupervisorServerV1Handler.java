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

package net.lax1dude.eaglercraft.backend.supervisor.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;
import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.*;
import net.lax1dude.eaglercraft.backend.supervisor.server.SupervisorClientInstance.PendingRPC;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.SupervisorPlayerInstance;
import net.lax1dude.eaglercraft.backend.supervisor.util.AlreadyRegisteredException;
import net.lax1dude.eaglercraft.backend.supervisor.util.CachedTextureData;
import net.lax1dude.eaglercraft.backend.supervisor.util.EnumPluginType;
import net.lax1dude.eaglercraft.backend.supervisor.util.EnumProxyType;
import net.lax1dude.eaglercraft.backend.util.SteadyTime;

public class SupervisorServerV1Handler implements EaglerSupervisorHandler {

	private static final Logger logger = LoggerFactory.getLogger("SupervisorServerV1Handler");

	private final EaglerXSupervisorServer server;
	private final SupervisorPacketHandler handler;
	private final SupervisorClientInstance client;

	public SupervisorServerV1Handler(EaglerXSupervisorServer server, SupervisorPacketHandler handler,
			SupervisorClientInstance client) {
		this.server = server;
		this.handler = handler;
		this.client = client;
	}

	@Override
	public void handleClient(CPacketSvPing pkt) {
		handler.channelWrite(new SPacketSvPong());
	}

	@Override
	public void handleClient(CPacketSvPong pkt) {
		client.onPongPacket();
	}

	@Override
	public void handleClient(CPacketSvProxyBrand pkt) {
		EnumProxyType proxyType = EnumProxyType.UNKNOWN;
		switch(pkt.proxyType) {
		case CPacketSvProxyBrand.PROXY_TYPE_BUNGEE:
			proxyType = EnumProxyType.BUNGEECORD;
			break;
		case CPacketSvProxyBrand.PROXY_TYPE_VELOCITY:
			proxyType = EnumProxyType.VELOCITY;
			break;
		case CPacketSvProxyBrand.PROXY_TYPE_EAGLER_STANDALONE:
			proxyType = EnumProxyType.EAGLER_STANDALONE;
			break;
		}
		EnumPluginType pluginType = EnumPluginType.UNKNOWN;
		switch(pkt.pluginType) {
		case CPacketSvProxyBrand.PLUGIN_TYPE_EAGLERXBUNGEE:
			pluginType = EnumPluginType.EAGLERXBUNGEE;
			break;
		case CPacketSvProxyBrand.PLUGIN_TYPE_EAGLERXVELOCITY:
			pluginType = EnumPluginType.EAGLERXVELOCITY;
			break;
		case CPacketSvProxyBrand.PLUGIN_TYPE_EAGLERXSERVER:
			pluginType = EnumPluginType.EAGLERXSERVER;
			break;
		}
		client.onProxyBrandPacket(proxyType, pkt.proxyVersion, pluginType, pkt.pluginBrand, pkt.pluginVersion);
	}

	@Override
	public void handleClient(CPacketSvProxyStatus pkt) {
		client.onProxyStatusPacket(pkt.systemTime, pkt.playerMax);
	}

	@Override
	public void handleClient(CPacketSvRegisterPlayer pkt) {
		try {
			client.registerProxyPlayer(pkt.playerUUID, pkt.brandUUID, pkt.gameProtocol, pkt.eaglerProtocol, pkt.username);
			handler.channelWrite(new SPacketSvAcceptPlayer(pkt.playerUUID));
		}catch(AlreadyRegisteredException ex) {
			handler.channelWrite(new SPacketSvRejectPlayer(pkt.playerUUID, ex.cause));
		}
	}

	@Override
	public void handleClient(CPacketSvDropPlayer pkt) {
		client.dropProxyPlayer(pkt.playerUUID);
	}

	@Override
	public void handleClient(CPacketSvDropPlayerPartial pkt) {
		if(pkt.bitmask != 0) {
			SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.playerUUID);
			if(player != null) {
				player.onDropProxyPlayerData(pkt.serverNotify,
						(pkt.bitmask & CPacketSvDropPlayerPartial.DROP_PLAYER_SKIN) != 0,
						(pkt.bitmask & CPacketSvDropPlayerPartial.DROP_PLAYER_CAPE) != 0);
			}
		}
	}

	@Override
	public void handleClient(CPacketSvGetOtherSkin pkt) {
		SupervisorPlayerInstance player = server.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			if(player.setClientKnown(client)) {
				handler.channelWrite(new SPacketSvPlayerNodeID(pkt.uuid, player.getBrandUUID(), player.getOwner().getNodeId()));
			}
			player.loadSkinData((skin) -> {
				handler.channelWrite(skin.makeResponse(pkt.uuid));
			});
		}else {
			handler.channelWrite(new SPacketSvOtherSkinError(pkt.uuid));
		}
	}

	@Override
	public void handleClient(CPacketSvGetSkinByURL pkt) {
		ISkinCacheService skinCacheService = server.getSkinCache();
		if(skinCacheService != null) {
			skinCacheService.resolveSkinByURL(pkt.url, (skin) -> {
				handler.channelWrite(CachedTextureData.makeSkinResponse(skin, pkt.uuid, pkt.modelId));
			});
		}else {
			handler.channelWrite(new SPacketSvOtherSkinError(pkt.uuid));
		}
	}

	@Override
	public void handleClient(CPacketSvOtherSkinPreset pkt) {
		SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			player.onSkinDataReceivedPreset(pkt.presetSkin);
		}else {
			logger.warn("Received skin data for unknown player {}", pkt.uuid);
		}
	}

	@Override
	public void handleClient(CPacketSvOtherSkinCustom pkt) {
		SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			player.onSkinDataReceivedCustom(pkt.model, pkt.customSkin);
		}else {
			logger.warn("Received skin data for unknown player {}", pkt.uuid);
		}
	}

	@Override
	public void handleClient(CPacketSvOtherSkinURL pkt) {
		SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			ISkinCacheService skinCacheService = server.getSkinCache();
			if(skinCacheService != null) {
				skinCacheService.resolveSkinByURL(pkt.url, (skin) -> {
					player.onSkinDataReceivedCached(pkt.modelId, skin);
				});
			}else {
				player.onSkinDataReceivedPreset(0);
			}
		}else {
			logger.warn("Received skin data for unknown player {}", pkt.uuid);
		}
	}

	@Override
	public void handleClient(CPacketSvGetOtherCape pkt) {
		SupervisorPlayerInstance player = server.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			if(player.setClientKnown(client)) {
				handler.channelWrite(new SPacketSvPlayerNodeID(pkt.uuid, player.getBrandUUID(), player.getOwner().getNodeId()));
			}
			player.loadCapeData((cape) -> {
				handler.channelWrite(cape.makeResponse(pkt.uuid));
			});
		}else {
			handler.channelWrite(new SPacketSvOtherCapeError(pkt.uuid));
		}
	}

	@Override
	public void handleClient(CPacketSvGetCapeByURL pkt) {
		ISkinCacheService skinCacheService = server.getSkinCache();
		if(skinCacheService != null) {
			skinCacheService.resolveCapeByURL(pkt.url, (skin) -> {
				handler.channelWrite(CachedTextureData.makeCapeResponse(skin, pkt.uuid));
			});
		}else {
			handler.channelWrite(new SPacketSvOtherCapeError(pkt.uuid));
		}
	}

	@Override
	public void handleClient(CPacketSvOtherCapePreset pkt) {
		SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			player.onCapeDataReceivedPreset(pkt.presetCape);
		}else {
			logger.warn("Received cape data for unknown player {}", pkt.uuid);
		}
	}

	@Override
	public void handleClient(CPacketSvOtherCapeCustom pkt) {
		SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			player.onCapeDataReceivedCustom(pkt.customCape);
		}else {
			logger.warn("Received cape data for unknown player {}", pkt.uuid);
		}
	}

	@Override
	public void handleClient(CPacketSvOtherCapeURL pkt) {
		SupervisorPlayerInstance player = client.getPlayerByUUID(pkt.uuid);
		if(player != null) {
			ISkinCacheService skinCacheService = server.getSkinCache();
			if(skinCacheService != null) {
				skinCacheService.resolveCapeByURL(pkt.url, (skin) -> {
					player.onCapeDataReceivedCached(skin);
				});
			}else {
				player.onCapeDataReceivedPreset(0);
			}
		}else {
			logger.warn("Received cape data for unknown player {}", pkt.uuid);
		}
	}

	@Override
	public void handleClient(CPacketSvGetClientBrandUUID pkt) {
		SupervisorPlayerInstance player = server.getPlayerByUUID(pkt.playerUUID);
		if(player != null && player.setClientKnown(client)) {
			handler.channelWrite(new SPacketSvPlayerNodeID(pkt.playerUUID, player.getBrandUUID(), player.getOwner().getNodeId()));
		}else {
			handler.channelWrite(new SPacketSvClientBrandError(pkt.playerUUID));
		}
	}

	protected static int decodeFailure(int type) {
		switch(type) {
		case PendingRPC.FAILURE_PROCEDURE:
			return SPacketSvRPCResultFail.FAILURE_PROCEDURE;
		case PendingRPC.FAILURE_TIMEOUT:
		case PendingRPC.FAILURE_HANGUP:
			return SPacketSvRPCResultFail.FAILURE_TIMEOUT;
		default:
			return SPacketSvRPCResultFail.FAILURE_NOT_FOUND;
		}
	}

	@Override
	public void handleClient(CPacketSvRPCExecuteAll pkt) {
		List<SupervisorClientInstance> clientList = server.getClientList();
		if(pkt.timeout > 0) {
			List<SPacketSvRPCResultMulti.ResultEntry> ret = new ArrayList<>(Math.max(clientList.size() - 1, 1));
			AtomicInteger countDown = new AtomicInteger(clientList.size());
			for(SupervisorClientInstance otherClient : clientList) {
				if(otherClient != client) {
					otherClient.invokeRPC(pkt.requestUUID, client.getNodeId(), pkt.name, pkt.dataBuffer, new PendingRPC(SteadyTime.millis() + pkt.timeout) {
						@Override
						protected void onSuccess(byte[] dataBuffer) {
							synchronized(ret) {
								SPacketSvRPCResultMulti.ResultEntry type2 = SPacketSvRPCResultMulti.ResultEntry
										.success(otherClient.getNodeId(), dataBuffer);
								synchronized(ret) {
									ret.add(type2);
								}
							}
							if(countDown.decrementAndGet() == 0) {
								handler.channelWrite(new SPacketSvRPCResultMulti(pkt.requestUUID, ret));
							}
						}
						@Override
						protected void onFailure(int type) {
							if(type != PendingRPC.FAILURE_HANGUP) {
								SPacketSvRPCResultMulti.ResultEntry type2 = SPacketSvRPCResultMulti.ResultEntry
										.failure(otherClient.getNodeId(), decodeFailure(type));
								synchronized(ret) {
									ret.add(type2);
								}
							}
							if(countDown.decrementAndGet() == 0) {
								handler.channelWrite(new SPacketSvRPCResultMulti(pkt.requestUUID, ret));
							}
						}
					});
				}else {
					if(countDown.decrementAndGet() == 0) {
						handler.channelWrite(new SPacketSvRPCResultMulti(pkt.requestUUID, ret));
						return;
					}
				}
			}
		}else {
			for(SupervisorClientInstance otherClient : clientList) {
				otherClient.invokeRPCVoid(client.getNodeId(), pkt.name, pkt.dataBuffer);
			}
		}
	}

	@Override
	public void handleClient(CPacketSvRPCExecuteNode pkt) {
		SupervisorClientInstance otherClient = server.getClient(pkt.nodeId);
		if(pkt.timeout > 0) {
			if(otherClient != null) {
				otherClient.invokeRPC(pkt.requestUUID, client.getNodeId(), pkt.name, pkt.dataBuffer, new PendingRPC(SteadyTime.millis() + pkt.timeout) {
					@Override
					protected void onSuccess(byte[] dataBuffer) {
						handler.channelWrite(new SPacketSvRPCResultSuccess(pkt.requestUUID, dataBuffer));
					}
					@Override
					protected void onFailure(int type) {
						handler.channelWrite(new SPacketSvRPCResultFail(pkt.requestUUID, decodeFailure(type)));
					}
				});
			}else {
				handler.channelWrite(new SPacketSvRPCResultFail(pkt.requestUUID, SPacketSvRPCResultFail.FAILURE_NOT_FOUND));
			}
		}else {
			if(otherClient != null) {
				otherClient.invokeRPCVoid(client.getNodeId(), pkt.name, pkt.dataBuffer);
			}
		}
	}

	@Override
	public void handleClient(CPacketSvRPCExecutePlayerName pkt) {
		SupervisorPlayerInstance player = server.getPlayerByUsername(pkt.playerName);
		if(pkt.timeout > 0) {
			if(player != null) {
				player.getOwner().invokeRPC(pkt.requestUUID, client.getNodeId(), pkt.name, pkt.dataBuffer, new PendingRPC(SteadyTime.millis() + pkt.timeout) {
					@Override
					protected void onSuccess(byte[] dataBuffer) {
						handler.channelWrite(new SPacketSvRPCResultSuccess(pkt.requestUUID, dataBuffer));
					}
					@Override
					protected void onFailure(int type) {
						handler.channelWrite(new SPacketSvRPCResultFail(pkt.requestUUID, decodeFailure(type)));
					}
				});
			}else {
				handler.channelWrite(new SPacketSvRPCResultFail(pkt.requestUUID, SPacketSvRPCResultFail.FAILURE_NOT_FOUND));
			}
		}else {
			if(player != null) {
				player.getOwner().invokeRPCVoid(client.getNodeId(), pkt.name, pkt.dataBuffer);
			}
		}
	}

	@Override
	public void handleClient(CPacketSvRPCExecutePlayerUUID pkt) {
		SupervisorPlayerInstance player = server.getPlayerByUUID(pkt.playerUUID);
		if(pkt.timeout > 0) {
			if(player != null) {
				player.getOwner().invokeRPC(pkt.requestUUID, client.getNodeId(), pkt.name, pkt.dataBuffer, new PendingRPC(SteadyTime.millis() + pkt.timeout) {
					@Override
					protected void onSuccess(byte[] dataBuffer) {
						handler.channelWrite(new SPacketSvRPCResultSuccess(pkt.requestUUID, dataBuffer));
					}
					@Override
					protected void onFailure(int type) {
						handler.channelWrite(new SPacketSvRPCResultFail(pkt.requestUUID, decodeFailure(type)));
					}
				});
			}else {
				handler.channelWrite(new SPacketSvRPCResultFail(pkt.requestUUID, SPacketSvRPCResultFail.FAILURE_NOT_FOUND));
			}
		}else {
			if(player != null) {
				player.getOwner().invokeRPCVoid(client.getNodeId(), pkt.name, pkt.dataBuffer);
			}
		}
	}

	@Override
	public void handleClient(CPacketSvRPCResultSuccess pkt) {
		client.onRPCResultSuccess(pkt.requestUUID, pkt.dataBuffer);
	}

	@Override
	public void handleClient(CPacketSvRPCResultFail pkt) {
		client.onRPCResultFail(pkt.requestUUID);
	}

	@Override
	public void handleDisconnected() {
		client.handleDisconnected();
	}

}