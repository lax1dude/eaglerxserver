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

package net.lax1dude.eaglercraft.backend.server.bungee;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeConsole implements IPlatformCommandSender<ProxiedPlayer> {

	private final CommandSender console;

	BungeeConsole(CommandSender console) {
		this.console = console;
	}

	@Override
	public boolean checkPermission(String permission) {
		return console.hasPermission(permission);
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		console.sendMessage((BaseComponent) component);
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public IPlatformPlayer<ProxiedPlayer> asPlayer() {
		return null;
	}

}
