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

package net.lax1dude.eaglercraft.backend.server.adapter;

import java.io.File;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;

public interface IPlatform<PlayerObject> {

	public interface Init<PlayerObject> {

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel<PlayerObject>> channels);

		void setPipelineInitializer(IEaglerXServerNettyPipelineInitializer<? extends IPipelineData> initializer);

		void setConnectionInitializer(IEaglerXServerLoginInitializer<? extends IPipelineData> initializer);

		void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, PlayerObject> initializer);

		void setServerJoinListener(IEaglerXServerJoinListener<PlayerObject> listener);

		void setCommandRegistry(Collection<IEaglerXServerCommandType<PlayerObject>> commands);

		IPlatform<PlayerObject> getPlatform();

	}

	public interface InitProxying<PlayerObject> extends Init<PlayerObject> {

		void setEaglerListeners(Collection<IEaglerXServerListener> listeners);

		void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel<PlayerObject>> channels);

	}

	public interface InitNonProxying<PlayerObject> extends Init<PlayerObject> {

		void setEaglerListener(IEaglerXServerListener listener);

		SocketAddress getListenerAddress();

	}

	EnumAdapterPlatformType getType();

	String getVersion();

	Class<PlayerObject> getPlayerClass();

	String getPluginId();

	File getDataFolder();

	IPlatformLogger logger();

	IPlatformCommandSender<PlayerObject> getConsole();

	void forEachPlayer(Consumer<IPlatformPlayer<PlayerObject>> playerCallback);

	IPlatformPlayer<PlayerObject> getPlayer(PlayerObject playerObj);

	IPlatformPlayer<PlayerObject> getPlayer(String username);

	IPlatformPlayer<PlayerObject> getPlayer(UUID uuid);

	Collection<IPlatformPlayer<PlayerObject>> getAllPlayers();

	Map<String, IPlatformServer<PlayerObject>> getRegisteredServers();

	IPlatformServer<PlayerObject> getServer(String serverName);

	IEventDispatchAdapter<PlayerObject, ?> eventDispatcher();

	IPlatformScheduler getScheduler();

	Set<EnumConfigFormat> getConfigFormats();

	IPlatformComponentHelper getComponentHelper();

	boolean isOnlineMode();

	boolean isModernPluginChannelNamesOnly();

	int getPlayerTotal();

	int getPlayerMax();

	void setPlayerCountHandler(IEaglerXServerPlayerCountHandler playerCountHandler);

	Bootstrap setChannelFactory(Bootstrap bootstrap, SocketAddress address);

	ServerBootstrap setServerChannelFactory(ServerBootstrap bootstrap, SocketAddress address);

	EventLoopGroup getBossEventLoopGroup();

	EventLoopGroup getWorkerEventLoopGroup();

}
