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
import java.util.Optional;
import java.util.UUID;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.util.GameProfile;

import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class VelocityPlayer implements IPlatformPlayer<Player> {

	static final Component DEFAULT_KICK_MESSAGE = Component.translatable("disconnect.closed");

	private final Player player;
	Object attachment;
	IPlatformServer<Player> server;

	VelocityPlayer(Player player) {
		this.player = player;
	}

	@Override
	public Player getPlayerObject() {
		return player;
	}

	@Override
	public Channel getChannel() {
		return VelocityUnsafe.getInboundChannel(player);
	}

	@Override
	public IPlatformServer<Player> getServer() {
		return server;
	}

	@Override
	public String getUsername() {
		return player.getUsername();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return player.getRemoteAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return player.getProtocolVersion().getProtocol();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public boolean isConnected() {
		return player.isActive();
	}

	@Override
	public boolean isOnlineMode() {
		return player.isOnlineMode();
	}

	@Override
	public String getMinecraftBrand() {
		return player.getClientBrand();
	}

	@Override
	public void sendDataClient(String channel, byte[] message) {
		VelocityUnsafe.sendDataClient(player, channel, message);
	}

	@Override
	public void sendDataBackend(String channel, byte[] message) {
		Optional<ServerConnection> serverCon = player.getCurrentServer();
		if (serverCon.isPresent()) {
			VelocityUnsafe.sendDataBackend(serverCon.get(), channel, message);
		}
	}

	@Override
	public boolean isSetViewDistanceSupportedPaper() {
		return false;
	}

	@Override
	public void setViewDistancePaper(int distance) {
	}

	@Override
	public String getTexturesProperty() {
		GameProfile profile = player.getGameProfile();
		if (profile != null) {
			for (GameProfile.Property prop : profile.getProperties()) {
				if ("textures".equals(prop.getName())) {
					return prop.getValue();
				}
			}
		}
		return null;
	}

	@Override
	public void sendMessage(String message) {
		player.sendMessage(Component.text(message));
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject message) {
		player.sendMessage((Component) message);
	}

	@Override
	public void disconnect() {
		player.disconnect(DEFAULT_KICK_MESSAGE);
	}

	@Override
	public void disconnect(String kickMessage) {
		player.disconnect(Component.text(kickMessage));
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		player.disconnect((Component) kickMessage);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPlayerAttachment() {
		return (T) attachment;
	}

	@Override
	public boolean checkPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public IPlatformPlayer<Player> asPlayer() {
		return this;
	}

}
