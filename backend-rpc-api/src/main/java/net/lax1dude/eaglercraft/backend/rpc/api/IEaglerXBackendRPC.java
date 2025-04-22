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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.EaglerXBackendRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.internal.factory.IEaglerRPCFactory;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public interface IEaglerXBackendRPC<PlayerObject> extends IRPCAttributeHolder {

	@Nonnull
	public static <PlayerObject> IEaglerXBackendRPC<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return EaglerXBackendRPCFactory.INSTANCE.getAPI(playerObj);
	}

	@Nonnull
	public static IEaglerXBackendRPC<?> instance() {
		return EaglerXBackendRPCFactory.INSTANCE.getDefaultAPI();
	}

	@Nonnull
	public static Set<Class<?>> getPlayerTypes() {
		return EaglerXBackendRPCFactory.INSTANCE.getPlayerTypes();
	}

	@Nonnull
	public static IEaglerRPCFactory getFactoryInstance() {
		return EaglerXBackendRPCFactory.INSTANCE;
	}

	@Nonnull
	IEaglerRPCFactory getFactory();

	@Nonnull
	EnumPlatformType getPlatformType();

	@Nonnull
	Class<PlayerObject> getPlayerClass();

	@Nonnull
	IVoiceService<PlayerObject> getVoiceService();

	@Nonnull
	ISkinImageLoader getSkinImageLoader(boolean cacheEnabled);

	@Nonnull
	IPacketImageLoader getPacketImageLoader();

	@Nonnull
	Set<Class<?>> getComponentTypes();

	@Nonnull
	IPauseMenuBuilder createPauseMenuBuilder();

	@Nonnull
	<ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(
			@Nonnull Class<ComponentType> componentType);

	@Nullable
	IBasePlayer<PlayerObject> getBasePlayer(@Nonnull PlayerObject player);

	@Nullable
	IBasePlayer<PlayerObject> getBasePlayerByName(@Nonnull String playerName);

	@Nullable
	IBasePlayer<PlayerObject> getBasePlayerByUUID(@Nonnull UUID playerUUID);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayer(@Nonnull PlayerObject player);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayerByName(@Nonnull String playerName);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(@Nonnull UUID playerUUID);

	boolean isEaglerPlayer(@Nonnull PlayerObject player);

	boolean isEaglerPlayerByName(@Nonnull String playerName);

	boolean isEaglerPlayerByUUID(@Nonnull UUID playerUUID);

	@Nonnull
	Collection<IBasePlayer<PlayerObject>> getAllPlayers();

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers();

	@Nonnull
	IScheduler getScheduler();

	void setBaseRequestTimeout(int seconds);

	int getBaseRequestTimeout();

	@Nonnull
	UUID intern(@Nonnull UUID uuid);

	boolean isLocal();

}
