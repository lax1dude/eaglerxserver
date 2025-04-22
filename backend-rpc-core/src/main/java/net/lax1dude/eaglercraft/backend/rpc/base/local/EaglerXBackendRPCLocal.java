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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.Init;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.InitLocalMode;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCBase;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.Collectors3;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

public class EaglerXBackendRPCLocal<PlayerObject> extends EaglerXBackendRPCBase<PlayerObject> {

	private final ConcurrentMap<PlayerObject, PlayerInstanceLocal<PlayerObject>> playerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

	private IEaglerXServerAPI<PlayerObject> serverAPI;
	private ISkinImageLoader skinLoaderCache;
	private ISkinImageLoader skinLoaderNoCache;
	private IPacketImageLoader packetImageLoader;
	private IVoiceService<PlayerObject> voiceService;

	@Override
	protected void load0(Init<PlayerObject> platf) {
		logger().info("Detected EaglerXServer, running plugin in local mode");
		platf.setOnServerEnable(this::enableHandler);
		platf.setOnServerDisable(this::disableHandler);
		platf.setPlayerInitializer(new BackendRPCPlayerInitializer<>(this));
		InitLocalMode<PlayerObject> platfLocal = platf.localMode();
		platfLocal.setOnInitializePlayer(this::fireInitializePlayer);
		platfLocal.setOnWebViewChannel(this::fireWebViewChannel);
		platfLocal.setOnWebViewMessage(this::fireWebViewMessage);
		platfLocal.setOnVoiceChange(this::fireVoiceChange);
		serverAPI = IEaglerXServerAPI.instance(playerClass);
		skinLoaderCache = new SkinTypesHelper(serverAPI.getSkinService().getSkinLoader(true));
		skinLoaderNoCache = new SkinTypesHelper(serverAPI.getSkinService().getSkinLoader(false));
		packetImageLoader = new PacketImageDataHelper(serverAPI.getPacketImageLoader());
		if(serverAPI.getVoiceService().isVoiceEnabled()) {
			voiceService = new VoiceServiceLocal<>(this, serverAPI.getVoiceService());
		}else {
			voiceService = new VoiceServiceDisabled<>(this);
		}
	}

	private void enableHandler() {
		
	}

	private void disableHandler() {
		
	}

	private void fireInitializePlayer(IEaglercraftInitializePlayerEvent<PlayerObject> event) {
		PlayerInstanceLocal<PlayerObject> player = playerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			player.offerPlayer(event.getPlayer());
		}
	}

	private void fireWebViewChannel(IEaglercraftWebViewChannelEvent<PlayerObject> event) {
		PlayerInstanceLocal<PlayerObject> player = playerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			BasePlayerRPCLocal<PlayerObject> handle = player.handle();
			if(handle != null && handle.isEaglerPlayer()) {
				((EaglerPlayerRPCLocal<PlayerObject>)handle).fireLocalWebViewChannel(event);
			}
		}
	}

	private void fireWebViewMessage(IEaglercraftWebViewMessageEvent<PlayerObject> event) {
		PlayerInstanceLocal<PlayerObject> player = playerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			BasePlayerRPCLocal<PlayerObject> handle = player.handle();
			if(handle != null && handle.isEaglerPlayer()) {
				((EaglerPlayerRPCLocal<PlayerObject>)handle).fireLocalWebViewMessage(event);
			}
		}
	}

	private void fireVoiceChange(IEaglercraftVoiceChangeEvent<PlayerObject> event) {
		PlayerInstanceLocal<PlayerObject> player = playerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			BasePlayerRPCLocal<PlayerObject> handle = player.handle();
			if(handle != null && handle.isEaglerPlayer()) {
				((EaglerPlayerRPCLocal<PlayerObject>)handle).fireLocalVoiceChange(event);
			}
		}
	}

	void registerPlayer(PlayerInstanceLocal<PlayerObject> player) {
		PlayerObject playerObj = player.getPlayerObject();
		if(playerMap.putIfAbsent(playerObj, player) != null) {
			throw new IllegalStateException("Player is already registered!");
		}
	}

	void confirmPlayer(PlayerInstanceLocal<PlayerObject> player) {
		if(playerMap.get(player.getPlayerObject()) == player) {
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> playerImpl
					= serverAPI.getPlayer(player.getPlayerObject());
			player.offerPlayer(playerImpl);
			BasePlayerRPCLocal<PlayerObject> handle = player.handle();
			if(handle != null && handle.isVoiceCapable()) {
				net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> voiceManager = handle.delegate
						.asEaglerPlayer().getVoiceManager();
				net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel =
						voiceManager.getVoiceChannel();
				platform.eventDispatcher().dispatchVoiceCapableEvent(player, VoiceChannelHelper.wrap(channel),
						(evt) -> {
					net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel2 =
							VoiceChannelHelper.unwrap(evt.getTargetChannel());
					if(channel != channel2) {
						voiceManager.setVoiceChannel(channel2);
					}
				});
			}
		}
	}

	void unregisterPlayer(PlayerInstanceLocal<PlayerObject> player) {
		PlayerObject playerObj = player.getPlayerObject();
		if(playerMap.remove(playerObj) != null) {
			player.handleDestroyed();
		}
		
	}

	IEaglerXServerAPI<PlayerObject> serverAPI() {
		return serverAPI;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		return voiceService;
	}

	@Override
	public ISkinImageLoader getSkinImageLoader(boolean cacheEnabled) {
		return cacheEnabled ? skinLoaderCache : skinLoaderNoCache;
	}

	@Override
	public IPacketImageLoader getPacketImageLoader() {
		return packetImageLoader;
	}

	@Override
	public Set<Class<?>> getComponentTypes() {
		return serverAPI.getComponentTypes();
	}

	@Override
	public IPauseMenuBuilder createPauseMenuBuilder() {
		return new PauseMenuBuilderLocal(serverAPI.getPauseMenuService().createPauseMenuBuilder());
	}

	@Override
	public <ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(
			Class<ComponentType> componentType) {
		return new NotificationBuilderLocal<ComponentType>(
				serverAPI.getNotificationService().createNotificationBuilder(componentType));
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer(PlayerObject player) {
		if(player == null) {
			throw new NullPointerException("player");
		}
		return playerMap.get(player);
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayerByName(String playerName) {
		if(playerName == null) {
			throw new NullPointerException("playerName");
		}
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			return platformPlayer.getAttachment();
		}
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayerByUUID(UUID playerUUID) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			return platformPlayer.getAttachment();
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player) {
		if(player == null) {
			throw new NullPointerException("player");
		}
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(player);
		if(platformPlayer != null) {
			return platformPlayer.<PlayerInstanceLocal<PlayerObject>>getAttachment().asEaglerPlayer();
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName) {
		if(playerName == null) {
			throw new NullPointerException("playerName");
		}
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			return platformPlayer.<PlayerInstanceLocal<PlayerObject>>getAttachment().asEaglerPlayer();
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			return platformPlayer.<PlayerInstanceLocal<PlayerObject>>getAttachment().asEaglerPlayer();
		}
		return null;
	}

	@Override
	public Collection<IBasePlayer<PlayerObject>> getAllPlayers() {
		return ImmutableList.copyOf(playerMap.values());
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers() {
		return playerMap.values().stream().filter(IBasePlayer<PlayerObject>::isEaglerPlayer)
				.collect(Collectors3.toImmutableList());
	}

	@Override
	public UUID intern(UUID uuid) {
		return serverAPI.intern(uuid);
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}
