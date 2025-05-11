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

package net.lax1dude.eaglercraft.backend.rpc.adapter;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

public interface IPlatform<PlayerObject> {

	public interface Init<PlayerObject> {

		IPlatform<PlayerObject> getPlatform();

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setPlayerInitializer(IBackendRPCPlayerInitializer<?, PlayerObject> initializer);

		void setWorldChangeHandler(IBackendRPCWorldChangeHandler<PlayerObject> handler);

		InitLocalMode<PlayerObject> localMode();

		InitRemoteMode<PlayerObject> remoteMode();

	}

	public interface InitLocalMode<PlayerObject> {

		void setOnInitializePlayer(Consumer<IEaglercraftInitializePlayerEvent<PlayerObject>> handler);

		void setOnWebViewChannel(Consumer<IEaglercraftWebViewChannelEvent<PlayerObject>> handler);

		void setOnWebViewMessage(Consumer<IEaglercraftWebViewMessageEvent<PlayerObject>> handler);

		void setOnVoiceChange(Consumer<IEaglercraftVoiceChangeEvent<PlayerObject>> handler);

	}

	public interface InitRemoteMode<PlayerObject> {

		void setEaglerPlayerChannels(Collection<IBackendRPCMessageChannel<PlayerObject>> channels);

	}

	EnumAdapterPlatformType getType();

	Class<PlayerObject> getPlayerClass();

	String getPluginId();

	File getDataFolder();

	IPlatformLogger logger();

	IEventDispatchAdapter<PlayerObject> eventDispatcher();

	void forEachPlayer(Consumer<IPlatformPlayer<PlayerObject>> playerCallback);

	IPlatformPlayer<PlayerObject> getPlayer(PlayerObject playerObj);

	IPlatformPlayer<PlayerObject> getPlayer(String username);

	IPlatformPlayer<PlayerObject> getPlayer(UUID uuid);

	Collection<IPlatformPlayer<PlayerObject>> getAllPlayers();

	IPlatformScheduler getScheduler();

	IPlatformComponentHelper getComponentHelper();

	boolean isPost_v1_13();

}
