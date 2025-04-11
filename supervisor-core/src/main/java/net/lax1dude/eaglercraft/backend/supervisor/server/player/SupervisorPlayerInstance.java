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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.IntSet;

import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvDropPlayerPartial;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvGetOtherCape;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvGetOtherSkin;
import net.lax1dude.eaglercraft.backend.supervisor.server.SupervisorClientInstance;
import net.lax1dude.eaglercraft.backend.supervisor.util.CachedTextureData;
import net.lax1dude.eaglercraft.backend.util.ConcurrentLazyLoader;

public class SupervisorPlayerInstance {

	private static final Logger logger = LoggerFactory.getLogger("SupervisorPlayerInstance");

	private final SupervisorClientInstance owner;
	private final UUID playerUUID;
	private final UUID brandUUID;
	private final int gameProtocol;
	private final int eaglerProtocol;
	private final String username;

	private final ReadWriteLock clientsKnownLock = new ReentrantReadWriteLock();
	private final IntSet clientsKnown = new IntHashSet();

	private final ConcurrentLazyLoader<PlayerSkinData> skinData;
	private final ConcurrentLazyLoader<PlayerCapeData> capeData;

	private Consumer<PlayerSkinData> skinDataWaiting = null;
	private Consumer<PlayerCapeData> capeDataWaiting = null;

	public SupervisorPlayerInstance(SupervisorClientInstance owner, UUID playerUUID, UUID brandUUID, int gameProtocol,
			int eaglerProtocol, String username) {
		this.owner = owner;
		this.playerUUID = playerUUID;
		this.brandUUID = brandUUID;
		this.gameProtocol = gameProtocol;
		this.eaglerProtocol = eaglerProtocol;
		this.username = username;
		this.skinData = new ConcurrentLazyLoader<PlayerSkinData>() {
			@Override
			protected void loadImpl(Consumer<PlayerSkinData> cb) {
				SupervisorPlayerInstance.this.owner.getHandler().channelWrite(new SPacketSvGetOtherSkin(playerUUID));
				skinDataWaiting = cb;
			}
		};
		this.capeData = new ConcurrentLazyLoader<PlayerCapeData>() {
			@Override
			protected void loadImpl(Consumer<PlayerCapeData> cb) {
				SupervisorPlayerInstance.this.owner.getHandler().channelWrite(new SPacketSvGetOtherCape(playerUUID));
				capeDataWaiting = cb;
			}
		};
	}

	public boolean clientKnown(SupervisorClientInstance client) {
		if(client == owner) {
			return true;
		}
		clientsKnownLock.readLock().lock();
		try {
			return clientsKnown.contains(client.getNodeId());
		}finally {
			clientsKnownLock.readLock().unlock();
		}
	}

	public boolean setClientKnown(SupervisorClientInstance client) {
		if(client == owner) {
			return false;
		}
		clientsKnownLock.writeLock().lock();
		try {
			return clientsKnown.add(client.getNodeId());
		}finally {
			clientsKnownLock.writeLock().unlock();
		}
	}

	public IntContainer allKnownClients() {
		clientsKnownLock.readLock().lock();
		try {
			IntArrayList lst = new IntArrayList(clientsKnown.size() + 1);
			lst.add(owner.getNodeId());
			lst.addAll(clientsKnown);
			return lst;
		}finally {
			clientsKnownLock.readLock().unlock();
		}
	}

	public void addKnownClientsNotOwner(IntSet output) {
		clientsKnownLock.readLock().lock();
		try {
			output.addAll(clientsKnown);
		}finally {
			clientsKnownLock.readLock().unlock();
		}
	}

	public void forgetClient(int nodeId) {
		clientsKnownLock.writeLock().lock();
		try {
			clientsKnown.removeAll(nodeId);
		}finally {
			clientsKnownLock.writeLock().unlock();
		}
	}

	public SupervisorClientInstance getOwner() {
		return owner;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public UUID getBrandUUID() {
		return brandUUID;
	}

	public int getGameProtocol() {
		return gameProtocol;
	}

	public int getEaglerProtocol() {
		return eaglerProtocol;
	}

	public boolean isEaglerPlayer() {
		return eaglerProtocol > 0;
	}

	public String getUsername() {
		return username;
	}

	public void onSkinDataReceivedPreset(int presetId) {
		Consumer<PlayerSkinData> consumer = skinDataWaiting;
		if(consumer != null) {
			skinDataWaiting = null;
			consumer.accept(PlayerSkinData.create(presetId));
		}else {
			logger.warn("Received unsolicited skin data for player {}", playerUUID);
		}
	}

	public void onSkinDataReceivedCustom(int modelId, byte[] customSkin) {
		Consumer<PlayerSkinData> consumer = skinDataWaiting;
		if(consumer != null) {
			skinDataWaiting = null;
			consumer.accept(PlayerSkinData.create(modelId, customSkin));
		}else {
			logger.warn("Received unsolicited skin data for player {}", playerUUID);
		}
	}

	public void onSkinDataReceivedCached(int modelId, byte[] textureData) {
		Consumer<PlayerSkinData> consumer = skinDataWaiting;
		if(consumer != null) {
			skinDataWaiting = null;
			consumer.accept(CachedTextureData.toSkinData(textureData, modelId));
		}else {
			logger.warn("Received unsolicited skin data for player {}", playerUUID);
		}
	}

	public void onSkinDataReceivedError() {
		Consumer<PlayerSkinData> consumer = skinDataWaiting;
		if(consumer != null) {
			skinDataWaiting = null;
			logger.warn("Received error response for eagler skin lookup of player {}");
			consumer.accept(PlayerSkinData.ERROR);
		}else {
			logger.warn("Received unsolicited skin data for player {}", playerUUID);
		}
	}

	public void onCapeDataReceivedPreset(int presetId) {
		Consumer<PlayerCapeData> consumer = capeDataWaiting;
		if(consumer != null) {
			capeDataWaiting = null;
			consumer.accept(PlayerCapeData.create(presetId));
		}else {
			logger.warn("Received unsolicited cape data for player {}", playerUUID);
		}
	}

	public void onCapeDataReceivedCustom(byte[] customCape) {
		Consumer<PlayerCapeData> consumer = capeDataWaiting;
		if(consumer != null) {
			capeDataWaiting = null;
			consumer.accept(PlayerCapeData.create(customCape));
		}else {
			logger.warn("Received unsolicited cape data for player {}", playerUUID);
		}
	}

	public void onCapeDataReceivedCached(byte[] textureData) {
		Consumer<PlayerCapeData> consumer = capeDataWaiting;
		if(consumer != null) {
			capeDataWaiting = null;
			consumer.accept(CachedTextureData.toCapeData(textureData));
		}else {
			logger.warn("Received unsolicited cape data for player {}", playerUUID);
		}
	}

	public void onCapeDataReceivedError() {
		Consumer<PlayerCapeData> consumer = capeDataWaiting;
		if(consumer != null) {
			capeDataWaiting = null;
			logger.warn("Received error response for eagler cape lookup of player {}");
			consumer.accept(PlayerCapeData.ERROR);
		}else {
			logger.warn("Received unsolicited cape data for player {}", playerUUID);
		}
	}

	public void onDropProxyPlayerData(String serverToNotify, boolean skin, boolean cape) {
		if(skin) {
			skinData.clear();
		}
		if(cape) {
			capeData.clear();
		}
		EaglerXSupervisorServer server = owner.getServer();
		IntContainer clientsKnownCopy = null;
		clientsKnownLock.readLock().lock();
		try {
			if(clientsKnown.size() > 0) {
				clientsKnownCopy = new IntArrayList(clientsKnown);
			}
		}finally {
			clientsKnownLock.readLock().unlock();
		}
		if(clientsKnownCopy != null) {
			int mask = 0;
			if(skin) mask |= SPacketSvDropPlayerPartial.DROP_PLAYER_SKIN;
			if(cape) mask |= SPacketSvDropPlayerPartial.DROP_PLAYER_CAPE;
			SPacketSvDropPlayerPartial pkt = new SPacketSvDropPlayerPartial(playerUUID, serverToNotify, mask);
			for(SupervisorClientInstance client : server.getClients(clientsKnownCopy)) {
				client.getHandler().channelWrite(pkt);
			}
		}
	}

	public void loadSkinData(Consumer<PlayerSkinData> cb) {
		skinData.load(cb);
	}

	public PlayerSkinData getSkinDataIfLoaded() {
		return skinData.getIfLoaded();
	}

	public void loadCapeData(Consumer<PlayerCapeData> cb) {
		capeData.load(cb);
	}

	public PlayerCapeData getCapeDataIfLoaded() {
		return capeData.getIfLoaded();
	}

}