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

package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.util.List;

import com.velocitypowered.api.proxy.Player;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftWebSocketOpenEvent;

class VelocityWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent
		implements IEaglercraftWebSocketOpenEvent.NettyUnsafe {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IEaglerConnection connection;
	private final FullHttpRequest request;

	VelocityWebSocketOpenEventImpl(IEaglerXServerAPI<Player> api, IEaglerConnection connection,
			FullHttpRequest request) {
		this.api = api;
		this.connection = connection;
		this.request = request;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public IEaglerConnection getConnection() {
		return connection;
	}

	@Override
	public String getRawHeader(String name) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		return request.headers().get(name);
	}

	@Override
	public List<String> getRawHeaders(String name) {
		if(name == null) {
			throw new NullPointerException("name");
		}
		return request.headers().getAll(name);
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public FullHttpRequest getHttpRequest() {
		return request;
	}

}
