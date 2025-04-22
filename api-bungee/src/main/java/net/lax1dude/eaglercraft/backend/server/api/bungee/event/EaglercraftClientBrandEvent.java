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

package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public final class EaglercraftClientBrandEvent
		extends AsyncEvent<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>, Cancellable {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IEaglerPendingConnection pendingConnection;
	private BaseComponent message;

	public EaglercraftClientBrandEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPendingConnection pendingConnection,
			@Nonnull Callback<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.pendingConnection = pendingConnection;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
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

	@Nonnull
	@Override
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
	}

	@Nullable
	@Override
	public BaseComponent getMessage() {
		return message;
	}

	@Override
	public void setMessage(@Nullable String message) {
		this.message = message != null ? new TextComponent(message) : null;
	}

	@Override
	public void setMessage(@Nullable BaseComponent message) {
		this.message = message;
	}

}
