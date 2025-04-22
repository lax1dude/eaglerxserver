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

package net.lax1dude.eaglercraft.backend.server.bungee;

import java.net.SocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeConnection implements IPlatformConnection {

	static final BaseComponent DEFAULT_KICK_MESSAGE = new TranslatableComponent("disconnect.closed");

	private final PlatformPluginBungee platformPlugin;
	private final PendingConnection pendingConnection;
	String texturesPropertyValue;
	String texturesPropertySignature;
	ProxiedPlayer playerInstance;
	Object attachment;
	Consumer<Runnable> awaitPlayState;
	byte eaglerPlayerProperty;

	BungeeConnection(PlatformPluginBungee platformPlugin, PendingConnection pendingConnection,
			Consumer<Runnable> awaitPlayState) {
		this.platformPlugin = platformPlugin;
		this.pendingConnection = pendingConnection;
		this.awaitPlayState = awaitPlayState;
	}

	@Override
	public Channel getChannel() {
		return BungeeUnsafe.getInitialHandlerChannel(pendingConnection);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttachment() {
		return (T) attachment;
	}

	@Override
	public boolean isOnlineMode() {
		return pendingConnection.isOnlineMode();
	}

	@Override
	public boolean isConnected() {
		return pendingConnection.isConnected();
	}

	@Override
	public String getUsername() {
		return pendingConnection.getName();
	}

	@Override
	public UUID getUniqueId() {
		return pendingConnection.getUniqueId();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return pendingConnection.getSocketAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return pendingConnection.getVersion();
	}

	@Override
	public void disconnect() {
		ProxiedPlayer player = playerInstance;
		if(player != null) {
			player.disconnect(DEFAULT_KICK_MESSAGE);
		}else {
			pendingConnection.disconnect(DEFAULT_KICK_MESSAGE);
		}
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		ProxiedPlayer player = playerInstance;
		if(player != null) {
			player.disconnect((BaseComponent)kickMessage);
		}else {
			pendingConnection.disconnect((BaseComponent)kickMessage);
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
