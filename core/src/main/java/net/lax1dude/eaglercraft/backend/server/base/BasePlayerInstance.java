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

public class BasePlayerInstance<PlayerObject> implements IBasePlayer<PlayerObject>, INettyChannel.NettyUnsafe {

	protected final IPlatformPlayer<PlayerObject> player;
	protected final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;
	protected final EaglerXServer<PlayerObject> server;
	ISkinManagerBase<PlayerObject> skinManager;

	public BasePlayerInstance(IPlatformPlayer<PlayerObject> player,
			EaglerXServer<PlayerObject> server) {
		this.player = player;
		this.attributeHolder = player.<BaseConnectionInstance>getConnectionAttachment().attributeHolder;
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
		return player.getConnection().getSocketAddress();
	}

	@Override
	public String getRealAddress() {
		return player.<BaseConnectionInstance>getConnectionAttachment().getRealAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return player.<BaseConnectionInstance>getConnectionAttachment().getMinecraftProtocol();
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
	public void disconnect() {
		player.getConnection().disconnect();
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		player.getConnection().disconnect(kickMessage);
	}

	public EaglerXServer<PlayerObject> getEaglerXServer() {
		return server;
	}

	@Override
	public NettyUnsafe getNettyUnsafe() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return player.getConnection().getChannel();
	}

}
