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

package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftLoginEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

class BukkitLoginEventImpl extends EaglercraftLoginEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IEaglerLoginConnection loginConnection;
	private final boolean redirectSupport;
	private BaseComponent message;
	private String redirect;
	private String username;
	private String requestedServer;

	BukkitLoginEventImpl(IEaglerXServerAPI<Player> api, IEaglerLoginConnection loginConnection,
			boolean redirectSupport, String requestedServer) {
		this.api = api;
		this.loginConnection = loginConnection;
		this.redirectSupport = redirectSupport;
		this.username = loginConnection.getUsername();
		this.requestedServer = requestedServer;
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
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
	}

	@Override
	public BaseComponent getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message != null ? new TextComponent(message) : null;
	}

	@Override
	public void setMessage(BaseComponent message) {
		this.message = message;
	}

	@Override
	public boolean isLoginStateRedirectSupported() {
		return redirectSupport;
	}

	@Override
	public String getRedirectAddress() {
		return redirect;
	}

	@Override
	public void setRedirectAddress(String addr) {
		this.redirect = addr;
	}

	@Override
	public String getProfileUsername() {
		return username;
	}

	@Override
	public void setProfileUsername(String username) {
		this.username = username;
	}

	@Override
	public UUID getProfileUUID() {
		return loginConnection.getUniqueId();
	}

	@Override
	public void setProfileUUID(UUID uuid) {
		throw new UnsupportedOperationException("Cannot change player UUID on Bukkit platforms!");
	}

	@Override
	public String getRequestedServer() {
		return requestedServer;
	}

	@Override
	public void setRequestedServer(String server) {
		if(server == null) {
			throw new NullPointerException("server");
		}
		this.requestedServer = server;
	}

}
