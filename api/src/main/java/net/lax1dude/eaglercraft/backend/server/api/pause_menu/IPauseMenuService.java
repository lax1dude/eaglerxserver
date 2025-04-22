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

package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IPauseMenuService<PlayerObject> extends IPacketImageLoader {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nullable
	default IPauseMenuManager<PlayerObject> getPauseMenuManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getPauseMenuManager() : null;
	}

	@Nonnull
	IWebViewService<PlayerObject> getWebViewService();

	@Nonnull
	ICustomPauseMenu getVanillaPauseMenu();

	@Nonnull
	ICustomPauseMenu getDefaultPauseMenu();

	void setDefaultPauseMenu(@Nonnull ICustomPauseMenu pauseMenu);

	void updateAllPlayersPauseMenu(@Nonnull ICustomPauseMenu pauseMenu);

	@Nonnull
	IPauseMenuBuilder createPauseMenuBuilder();

}
