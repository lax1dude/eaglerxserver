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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IEaglercraftAuthCheckRequiredEvent<PlayerObject, ComponentObject> extends IBaseHandshakeEvent<PlayerObject> {

	public static enum EnumAuthResponse {
		SKIP, REQUIRE, DENY
	}

	public static enum EnumAuthType {
		PLAINTEXT, EAGLER_SHA256, AUTHME_SHA256
	}

	boolean isClientSolicitingPassword();

	@Nonnull
	byte[] getAuthUsername();

	boolean isNicknameSelectionEnabled();

	void setNicknameSelectionEnabled(boolean enable);

	@Nullable
	byte[] getSaltingData();

	void setSaltingData(@Nullable byte[] saltingData);

	@Nullable
	EnumAuthType getUseAuthType();

	void setUseAuthType(@Nullable EnumAuthType authType);

	@Nullable
	EnumAuthResponse getAuthRequired();

	void setAuthRequired(@Nullable EnumAuthResponse required);

	@Nonnull
	String getAuthMessage();

	void setAuthMessage(@Nonnull String authMessage);

	boolean getEnableCookieAuth();

	void setEnableCookieAuth(boolean enable);

	@Nullable
	ComponentObject getKickMessage();

	void setKickMessage(@Nullable ComponentObject kickMessage);

	void setKickMessage(@Nullable String kickMessage);

	default void kickUser(@Nullable ComponentObject kickMessage) {
		setKickMessage(kickMessage);
		setAuthRequired(EnumAuthResponse.DENY);
	}

	default void kickUser(@Nullable String kickMessage) {
		setKickMessage(kickMessage);
		setAuthRequired(EnumAuthResponse.DENY);
	}

}
