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
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.api.IBaseLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;

public class BaseConnectionInstance extends IIdentifiedConnection.Base
		implements IBaseLoginConnection, INettyChannel.NettyUnsafe {

	protected final IPlatformConnection connection;
	protected final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;

	public BaseConnectionInstance(IPlatformConnection connection,
			EaglerAttributeManager.EaglerAttributeHolder attributeHolder) {
		this.connection = connection;
		this.attributeHolder = attributeHolder;
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
	public SocketAddress getSocketAddress() {
		return connection.getChannel().remoteAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return connection.getMinecraftProtocol();
	}

	@Override
	public SocketAddress getPlayerAddress() {
		return connection.getSocketAddress();
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public EaglerConnectionInstance asEaglerPlayer() {
		return null;
	}

	@Override
	public UUID getUniqueId() {
		return connection.getUniqueId();
	}

	@Override
	public String getUsername() {
		return connection.getUsername();
	}

	@Override
	public boolean isOnlineMode() {
		return connection.isOnlineMode();
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	@Override
	public void disconnect() {
		connection.disconnect();
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return connection.getChannel();
	}

}
