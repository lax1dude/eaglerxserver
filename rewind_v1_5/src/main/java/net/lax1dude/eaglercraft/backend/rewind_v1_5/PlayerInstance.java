/*
 * Copyright (c) 2025 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.*;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.IComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.collect.HPPC;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntSet;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectObjectMap;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalGlobalEAG;

public class PlayerInstance<PlayerObject> {

	private final RewindPluginProtocol<PlayerObject> rewind;
	private final IMessageController messageController;
	private final IOutboundInjector outboundInjector;
	private final Channel channel;
	private final IRewindLogger logger;
	private IEaglerPlayer<PlayerObject> eaglerPlayer;

	private INBTContext nbtContext;
	private IComponentHelper componentHelper;
	private TabListTracker tabList;
	private ObjectObjectMap<UUID, SkinRequest> skinRequests;
	private ObjectObjectMap<UUID, String> voiceGlobalMap;
	private ObjectObjectMap<String, UUID> voiceGlobalMapInv;

	private final IntSet enchWindows;

	private double x = 0;
	private double y = 0;
	private double z = 0;
	private float yaw = 0;
	private float pitch = 0;
	private boolean isSneaking = false;

	private byte[] temp;

	private long lastReqFlush;

	private static class SkinRequest {
		protected final long createdAt;
		protected final int cookie;
		protected SkinRequest(long createdAt, int cookie) {
			this.createdAt = createdAt;
			this.cookie = cookie;
		}
	}

	public PlayerInstance(RewindPluginProtocol<PlayerObject> rewind, IMessageController messageController,
			IOutboundInjector outboundInjector, Channel channel, String logName) {
		this.rewind = rewind;
		this.messageController = messageController;
		this.outboundInjector = outboundInjector;
		this.channel = channel;
		this.logger = rewind.logger().createSubLogger(logName);
		this.enchWindows = rewind.getServerAPI().getHPPC().createIntHashSet();
	}

	public RewindPluginProtocol<PlayerObject> getRewind() {
		return rewind;
	}

	public IRewindLogger logger() {
		return logger;
	}

	public IEaglerPlayer<PlayerObject> getPlayer() {
		return eaglerPlayer;
	}

	public IMessageController getMessageController() {
		return messageController;
	}

	public IOutboundInjector getOutboundInjector() {
		return outboundInjector;
	}

	public Channel getChannel() {
		return channel;
	}

	public INBTContext getNBTContext() {
		if(this.nbtContext == null) {
			this.nbtContext = rewind.getServerAPI().getNBTHelper().createThreadContext(512);
		}
		return this.nbtContext;
	}

	public IComponentHelper getComponentHelper() {
		if(this.componentHelper == null) {
			this.componentHelper = rewind.getServerAPI().getComponentHelper();
		}
		return this.componentHelper;
	}

	public TabListTracker getTabList() {
		if(this.tabList == null) {
			this.tabList = new TabListTracker(rewind.getServerAPI().getHPPC());
		}
		return this.tabList;
	}

	public IntSet getEnchWindows() {
		return this.enchWindows;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setLook(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public boolean isSneaking() {
		return isSneaking;
	}

	public void setSneaking(boolean sneaking) {
		this.isSneaking = sneaking;
	}

	public byte[] getTempBuffer() {
		if(this.temp == null) {
			this.temp = new byte[1024];
		}
		return this.temp;
	}

	public void addSkinRequest(UUID uuid, int cookie) {
		long nanoTime = System.nanoTime();
		if(nanoTime - lastReqFlush > (15l * 1000l * 1000l * 1000l)) {
			lastReqFlush = nanoTime;
			flushRequests(nanoTime);
		}
		if(skinRequests == null) {
			skinRequests = rewind.getServerAPI().getHPPC().createObjectObjectHashMap(64);
		}
		skinRequests.put(uuid, new SkinRequest(nanoTime, cookie));
	}

	private void flushRequests(long nanoTime) {
		if(skinRequests == null) return;
		skinRequests.removeAll((k, v) -> nanoTime - v.createdAt > (30l * 1000l * 1000l * 1000l));
	}

	public int removeSkinRequest(UUID uuid) {
		if(skinRequests == null) return -1;
		SkinRequest req = skinRequests.remove(uuid);
		return req != null ? req.cookie : -1;
	}

	public void releaseVoiceGlobalMap() {
		voiceGlobalMap = null;
		voiceGlobalMapInv = null;
	}

	public void handleVoiceGlobal(Collection<SPacketVoiceSignalGlobalEAG.UserData> userDatas) {
		IEaglerXServerAPI<?> api = rewind.getServerAPI();
		HPPC hppc = api.getHPPC();
		voiceGlobalMap = hppc.createObjectObjectHashMap(userDatas.size());
		voiceGlobalMapInv = hppc.createObjectObjectHashMap(userDatas.size());
		for(SPacketVoiceSignalGlobalEAG.UserData userData : userDatas) {
			UUID uuid = api.intern(new UUID(userData.uuidMost, userData.uuidLeast));
			String name = userData.username.intern();
			voiceGlobalMap.put(uuid, name);
			voiceGlobalMapInv.put(name, uuid);
		}
	}

	public String getVoicePlayerByUUID(UUID uuid) {
		return voiceGlobalMap != null ? voiceGlobalMap.get(uuid) : null;
	}

	public UUID getVoicePlayerByName(String name) {
		return voiceGlobalMapInv != null ? voiceGlobalMapInv.get(name) : null;
	}

	public void handlePlayerCreate(IEaglerPlayer<PlayerObject> eaglerPlayer) {
		this.eaglerPlayer = eaglerPlayer;
	}

	public void handlePlayerDestroy() {

	}

	public void releaseNatives() {
		
	}

}
