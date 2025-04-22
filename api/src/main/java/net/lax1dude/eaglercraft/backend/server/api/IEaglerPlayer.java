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

package net.lax1dude.eaglercraft.backend.server.api;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerEagler;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayer<PlayerObject> extends IBasePlayer<PlayerObject>, IEaglerLoginConnection {

	void sendEaglerMessage(@Nonnull GameMessagePacket packet);

	@Nonnull
	@Override 
	ISkinManagerEagler<PlayerObject> getSkinManager();

	@Nonnull
	default IEaglerPlayerSkin getEaglerSkin() {
		return getSkinManager().getEaglerSkin();
	}

	@Nonnull
	default IEaglerPlayerCape getEaglerCape() {
		return getSkinManager().getEaglerCape();
	}

	boolean isVoiceCapable();

	boolean hasVoiceManager();

	@Nullable
	IVoiceManager<PlayerObject> getVoiceManager();

	default void setCookieData(@Nullable byte[] data, long expiresAfter, @Nonnull TimeUnit timeUnit) {
		setCookieData(data, timeUnit.toSeconds(expiresAfter), false, true);
	}

	default void setCookieData(@Nullable byte[] data, long expiresAfter, @Nonnull TimeUnit timeUnit,
			boolean revokeQuerySupported) {
		setCookieData(data, timeUnit.toSeconds(expiresAfter), revokeQuerySupported, true);
	}

	default void setCookieData(@Nullable byte[] data, long expiresAfter, @Nonnull TimeUnit timeUnit,
			boolean revokeQuerySupported, boolean clientSaveCookieToDisk) {
		setCookieData(data, timeUnit.toSeconds(expiresAfter), revokeQuerySupported, clientSaveCookieToDisk);
	}

	void setCookieData(@Nullable byte[] data, long expiresAfterSec, boolean revokeQuerySupported,
			boolean clientSaveCookieToDisk);

	default void clearCookieData() {
		setCookieData(null, 0l, false, false);
	}

	boolean isRedirectPlayerSupported();

	void redirectPlayerToWebSocket(@Nonnull String webSocketURI);

	boolean isNotificationSupported();

	@Nullable
	INotificationManager<PlayerObject> getNotificationManager();

	boolean isPauseMenuSupported();

	@Nullable
	IPauseMenuManager<PlayerObject> getPauseMenuManager();

	boolean isWebViewSupported();

	@Nullable
	IWebViewManager<PlayerObject> getWebViewManager();

	boolean isUpdateSystemSupported();

	@Nullable
	IUpdateCertificate getUpdateCertificate();

	void offerUpdateCertificate(@Nonnull IUpdateCertificate certificate);

	void sendUpdateCertificate(@Nonnull IUpdateCertificate certificate);

}
