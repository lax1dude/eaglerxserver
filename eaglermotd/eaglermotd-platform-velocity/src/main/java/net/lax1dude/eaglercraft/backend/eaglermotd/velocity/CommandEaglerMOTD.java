/*
 * Copyright (c) 2026 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.eaglermotd.velocity;

import java.io.IOException;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDPlatform;

class CommandEaglerMOTD implements SimpleCommand {

	private final PlatformPluginVelocity plugin;

	CommandEaglerMOTD(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean hasPermission(Invocation invocation) {
		return invocation.source().hasPermission("eaglercraft.eaglermotd.reload");
	}

	@Override
	public void execute(Invocation invocation) {
		String[] args = invocation.arguments();
		if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
			IEaglerMOTDPlatform.IHandleReload handler = plugin.handleReload;
			if (handler == null) {
				invocation.source().sendMessage(Component.text("Plugin is not enabled!", NamedTextColor.RED));
				return;
			}
			try {
				handler.reload();
			} catch (IOException ex) {
				plugin.logger().error("Failed to reload config files!", ex);
				invocation.source()
						.sendMessage(Component.text("Failed to reload config files! (Check Proxy Log)", NamedTextColor.RED));
				invocation.source().sendMessage(Component.text(ex.toString(), NamedTextColor.RED));
				return;
			}
			invocation.source().sendMessage(Component.text("Configuration reloaded.", NamedTextColor.AQUA));
		} else {
			invocation.source().sendMessage(Component.text("Usage: /eaglermotd reload", NamedTextColor.RED));
		}
	}

	public CommandMeta register() {
		CommandManager mgr = plugin.proxy().getCommandManager();
		CommandMeta meta = mgr.metaBuilder("eaglermotd").plugin(plugin).build();
		mgr.register(meta, this);
		return meta;
	}

}
