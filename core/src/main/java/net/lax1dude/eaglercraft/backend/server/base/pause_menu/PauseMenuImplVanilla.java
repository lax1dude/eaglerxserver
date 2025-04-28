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

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

class PauseMenuImplVanilla implements IPauseMenuImpl {

	static final IPauseMenuImpl INSTANCE = new PauseMenuImplVanilla();
	static final IPauseMenuImpl INSTANCE_RPC = new PauseMenuImplVanilla();
	static final IPauseMenuImpl INSTANCE_RPC_2 = new PauseMenuImplVanilla() {
		@Override
		public boolean isPermitWebViewRequest() {
			return true;
		}
	};
	static final IPauseMenuImpl INSTANCE_RPC_3 = new PauseMenuImplVanilla() {
		@Override
		public boolean isPermitWebViewChannel() {
			return true;
		}
	};
	static final IPauseMenuImpl INSTANCE_RPC_4 = new PauseMenuImplVanilla() {
		@Override
		public boolean isPermitWebViewRequest() {
			return true;
		}

		@Override
		public boolean isPermitWebViewChannel() {
			return true;
		}
	};

	static IPauseMenuImpl getRPCMenu(SPacketCustomizePauseMenuV4EAG packet) {
		boolean request = packet.serverInfoMode == SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
		boolean channel = packet.serverInfoMode != SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_NONE
				&& packet.serverInfoMode != SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_EXTERNAL_URL
				&& (packet.serverInfoEmbedPerms
						& SPacketCustomizePauseMenuV4EAG.SERVER_INFO_EMBED_PERMS_MESSAGE_API) != 0;
		if (request) {
			return channel ? INSTANCE_RPC_4 : INSTANCE_RPC_2;
		} else {
			return channel ? INSTANCE_RPC_3 : INSTANCE_RPC;
		}
	}

	static final SPacketCustomizePauseMenuV4EAG PACKET = new SPacketCustomizePauseMenuV4EAG(0, null, null, null, 0,
			null, 0, null, null, null, null);

	@Override
	public SPacketCustomizePauseMenuV4EAG getPacket() {
		return PACKET;
	}

	@Override
	public IWebViewBlob getBlob() {
		return null;
	}

	@Override
	public boolean isPermitWebViewChannel() {
		return false;
	}

	@Override
	public boolean isPermitWebViewRequest() {
		return false;
	}

	@Override
	public ICustomPauseMenu extern() {
		return INSTANCE;
	}

	@Override
	public boolean isRemote() {
		return this != INSTANCE;
	}

}
