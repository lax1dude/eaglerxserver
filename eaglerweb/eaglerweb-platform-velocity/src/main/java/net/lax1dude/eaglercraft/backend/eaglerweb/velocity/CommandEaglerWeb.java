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

package net.lax1dude.eaglercraft.backend.eaglerweb.velocity;

import java.io.IOException;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebPlatform;

class CommandEaglerWeb implements SimpleCommand {

	private final PlatformPluginVelocity plugin;

	CommandEaglerWeb(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean hasPermission(Invocation invocation) {
		return invocation.source().hasPermission("eaglercraft.eaglerweb.refresh");
	}

	@Override
	public void execute(Invocation invocation) {
		String[] args = invocation.arguments();
		if (args.length == 1 && "refresh".equalsIgnoreCase(args[0])) {
			IEaglerWebPlatform.IHandleRefresh handler = plugin.handleRefresh;
			if (handler == null) {
				invocation.source().sendMessage(Component.text("Plugin is not enabled!", NamedTextColor.RED));
				return;
			}
			invocation.source().sendMessage(Component.text("Indexing pages, please wait...", NamedTextColor.AQUA));
			int cnt;
			try {
				cnt = handler.refresh();
			} catch (IOException ex) {
				plugin.logger().error("Failed to index pages!", ex);
				invocation.source()
						.sendMessage(Component.text("Failed to index pages! (Check Proxy Log)", NamedTextColor.RED));
				invocation.source().sendMessage(Component.text(ex.toString(), NamedTextColor.RED));
				return;
			}
			invocation.source().sendMessage(Component.text("Indexed " + cnt + " pages total!", NamedTextColor.AQUA));
		} else {
			invocation.source().sendMessage(Component.text("Usage: /eaglerweb refresh", NamedTextColor.RED));
		}
	}

	public CommandMeta register() {
		CommandManager mgr = plugin.proxy().getCommandManager();
		CommandMeta meta = mgr.metaBuilder("eaglerweb").plugin(plugin).build();
		mgr.register(meta, this);
		return meta;
	}

}
