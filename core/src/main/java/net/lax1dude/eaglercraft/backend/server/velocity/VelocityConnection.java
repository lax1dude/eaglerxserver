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

package net.lax1dude.eaglercraft.backend.server.velocity;

import java.net.SocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;

import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;

class VelocityConnection implements IPlatformConnection {

	static final Component DEFAULT_KICK_MESSAGE = Component.translatable("disconnect.closed");

	private final PlatformPluginVelocity plugin;
	private final InboundConnection connection;
	private final String username;
	UUID uuid;
	String texturesPropertyValue;
	String texturesPropertySignature;
	Player playerInstance;
	Object attachment;
	Consumer<Runnable> awaitPlayState;
	byte eaglerPlayerProperty;
	boolean compressionDisable;

	VelocityConnection(PlatformPluginVelocity plugin, InboundConnection connection,
			String username, UUID uuid, Consumer<Runnable> awaitPlayState) {
		this.plugin = plugin;
		this.connection = connection;
		this.username = username;
		this.uuid = uuid;
		this.awaitPlayState = awaitPlayState;
	}

	@Override
	public Channel getChannel() {
		return VelocityUnsafe.getInboundChannel(connection);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttachment() {
		return (T) attachment;
	}

	@Override
	public String getUsername() {
		Player player = playerInstance;
		if(player != null) {
			return player.getUsername();
		}else {
			return username;
		}
	}

	@Override
	public UUID getUniqueId() {
		Player player = playerInstance;
		if(player != null) {
			return player.getUniqueId();
		}else {
			return uuid;
		}
	}

	@Override
	public SocketAddress getSocketAddress() {
		return connection.getRemoteAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return connection.getProtocolVersion().getProtocol();
	}

	@Override
	public boolean isOnlineMode() {
		Player player = playerInstance;
		if(player != null) {
			return player.isOnlineMode();
		}else {
			//TODO: online mode?
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		Player player = playerInstance;
		if(player != null) {
			return player.isActive();
		}else {
			return connection.isActive();
		}
	}

	@Override
	public void disconnect() {
		Player player = playerInstance;
		if(player != null) {
			player.disconnect(DEFAULT_KICK_MESSAGE);
		}else {
			VelocityUnsafe.disconnectInbound(connection);
		}
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		Player player = playerInstance;
		if(player != null) {
			player.disconnect((Component)kickMessage);
		}else {
			VelocityUnsafe.disconnectInbound(connection, (Component)kickMessage);
		}
	}

	public void awaitPlayState(Runnable runnable) {
		if(awaitPlayState != null) {
			awaitPlayState.accept(runnable);
			awaitPlayState = null;
		}else {
			runnable.run();
		}
	}

}
