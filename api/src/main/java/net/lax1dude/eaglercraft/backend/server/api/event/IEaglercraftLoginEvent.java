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

package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.UnsupportedOn;

public interface IEaglercraftLoginEvent<PlayerObject, ComponentObject>
		extends IBaseLoginEvent<PlayerObject>, ICancellableEvent {

	@Nullable
	ComponentObject getMessage();

	void setMessage(@Nullable ComponentObject kickMessage);

	void setMessage(@Nullable String kickMessage);

	boolean isLoginStateRedirectSupported();

	@Nullable
	String getRedirectAddress();

	void setRedirectAddress(@Nullable String addr);

	@Nonnull
	String getProfileUsername();

	void setProfileUsername(@Nonnull String username);

	@Nonnull
	UUID getProfileUUID();

	@UnsupportedOn({ EnumPlatformType.BUKKIT })
	void setProfileUUID(@Nonnull UUID uuid);

	@Nonnull
	String getRequestedServer();

	void setRequestedServer(@Nonnull String server);

	default void setKickMessage(@Nullable ComponentObject kickMessage) {
		setCancelled(true);
		setMessage(kickMessage);
		setRedirectAddress(null);
	}

	default void setKickMessage(@Nullable String kickMessage) {
		setCancelled(true);
		setMessage(kickMessage);
		setRedirectAddress(null);
	}

	default void setKickRedirect(@Nonnull String addr) {
		if(!isLoginStateRedirectSupported()) {
			throw new UnsupportedOperationException("Login state redirect is not supported by this client");
		}
		if(addr == null) {
			throw new NullPointerException("addr");
		}
		setCancelled(true);
		setMessage((ComponentObject) null);
		setRedirectAddress(addr);
	}

}
