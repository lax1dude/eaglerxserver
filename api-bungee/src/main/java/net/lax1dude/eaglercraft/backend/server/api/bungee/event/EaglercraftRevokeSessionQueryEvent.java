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

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public final class EaglercraftRevokeSessionQueryEvent
		extends AsyncEvent<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>>
		implements IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IQueryConnection query;
	private final byte[] cookieData;
	private EnumSessionRevokeStatus result;
	private boolean shouldDelete;

	public EaglercraftRevokeSessionQueryEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IQueryConnection query, @Nonnull byte[] cookieData,
			@Nonnull Callback<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>> cb) {
		super(cb);
		this.api = api;
		this.query = query;
		this.cookieData = cookieData;
		this.result = EnumSessionRevokeStatus.FAILED_NOT_SUPPORTED;
		this.shouldDelete = false;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IQueryConnection getSocket() {
		return query;
	}

	@Nonnull
	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Nonnull
	@Override
	public EnumSessionRevokeStatus getResultStatus() {
		return result;
	}

	@Override
	public void setResultStatus(@Nonnull EnumSessionRevokeStatus result) {
		if (result == null) {
			throw new NullPointerException("result");
		}
		this.result = result;
	}

	@Override
	public boolean getShouldDeleteCookie() {
		return shouldDelete;
	}

	@Override
	public void setShouldDeleteCookie(boolean flag) {
		shouldDelete = flag;
	}

}
