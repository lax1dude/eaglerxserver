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

package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

public class PauseMenuManager<PlayerObject> implements IPauseMenuManager<PlayerObject> {

	private final EaglerPlayerInstance<PlayerObject> player;
	private final PauseMenuService<PlayerObject> service;
	private IPauseMenuImpl activePauseMenu = PauseMenuImplVanilla.INSTANCE;

	PauseMenuManager(EaglerPlayerInstance<PlayerObject> player, PauseMenuService<PlayerObject> service) {
		this.player = player;
		this.service = service;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IPauseMenuService<PlayerObject> getPauseMenuService() {
		return service;
	}

	@Override
	public ICustomPauseMenu getActivePauseMenu() {
		return activePauseMenu.extern();
	}

	@Override
	public boolean isActivePauseMenuRemote() {
		return activePauseMenu.isRemote();
	}

	@Override
	public void updatePauseMenu(ICustomPauseMenu pauseMenu) {
		if (pauseMenu == null) {
			throw new NullPointerException("pauseMenu");
		}
		IPauseMenuImpl impl = (IPauseMenuImpl) pauseMenu;
		if (activePauseMenu == impl)
			return;
		activePauseMenu = impl;
		player.sendEaglerMessage(impl.getPacket());
	}

	public boolean isWebViewChannelAllowedDefault() {
		return activePauseMenu.isPermitWebViewChannel();
	}

	public boolean isWebViewRequestAllowedDefault() {
		return activePauseMenu.isPermitWebViewRequest();
	}

	public IWebViewBlob getWebViewBlobDefault() {
		return activePauseMenu.getBlob();
	}

	public void updatePauseMenuRPC(SPacketCustomizePauseMenuV4EAG packet) {
		activePauseMenu = PauseMenuImplVanilla.getRPCMenu(packet);
		player.sendEaglerMessage(packet);
	}

}
