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

package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;
import net.lax1dude.eaglercraft.backend.server.base.rpc.BasePlayerRPCManager;

public class BasePlayerInstance<PlayerObject> extends IIdentifiedConnection.Base
		implements IBasePlayer<PlayerObject>, INettyChannel.NettyUnsafe {

	protected final IPlatformPlayer<PlayerObject> player;
	protected final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;
	protected final EaglerXServer<PlayerObject> server;
	ISkinManagerBase<PlayerObject> skinManager;
	BasePlayerRPCManager<PlayerObject> backendRPCManager;
	DataSerializationContext serializationContext;

	public BasePlayerInstance(IPlatformPlayer<PlayerObject> player,
			EaglerAttributeManager.EaglerAttributeHolder attributeHolder, EaglerXServer<PlayerObject> server) {
		this.player = player;
		this.attributeHolder = attributeHolder;
		this.server = server;
	}

	public IPlatformPlayer<PlayerObject> getPlatformPlayer() {
		return player;
	}

	@Override
	public String getUsername() {
		return player.getUsername();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return player.getChannel().remoteAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return player.getMinecraftProtocol();
	}

	@Override
	public SocketAddress getPlayerAddress() {
		return player.getSocketAddress();
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> asEaglerPlayer() {
		return null;
	}

	@Override
	public Object getIdentityToken() {
		return attributeHolder;
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return attributeHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		attributeHolder.set(key, value);
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public PlayerObject getPlayerObject() {
		return player.getPlayerObject();
	}

	@Override
	public String getMinecraftBrand() {
		return player.getMinecraftBrand();
	}

	@Override
	public UUID getEaglerBrandUUID() {
		return IBrandRegistry.BRAND_VANILLA;
	}

	@Override
	public boolean isConnected() {
		return player.isConnected();
	}

	@Override
	public boolean isOnlineMode() {
		return player.isOnlineMode();
	}

	@Override
	public ISkinManagerBase<PlayerObject> getSkinManager() {
		return skinManager;
	}

	@Override
	public void sendChatMessage(String message) {
		if (message == null) {
			throw new NullPointerException("message");
		}
		player.sendMessage(message);
	}

	@Override
	public <ComponentObject> void sendChatMessage(ComponentObject message) {
		if (message == null) {
			throw new NullPointerException("message");
		}
		player.sendMessage(message);
	}

	@Override
	public void disconnect() {
		player.disconnect();
	}

	@Override
	public void disconnect(String kickMessage) {
		if (kickMessage == null) {
			throw new NullPointerException("kickMessage");
		}
		player.disconnect(kickMessage);
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		if (kickMessage == null) {
			throw new NullPointerException("kickMessage");
		}
		player.disconnect(kickMessage);
	}

	public EaglerXServer<PlayerObject> getEaglerXServer() {
		return server;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return player.getChannel();
	}

	public BasePlayerRPCManager<PlayerObject> getPlayerRPCManager() {
		return backendRPCManager;
	}

	public DataSerializationContext getSerializationContext() {
		if (serializationContext == null) {
			serializationContext = new DataSerializationContext();
		}
		return serializationContext;
	}

}
