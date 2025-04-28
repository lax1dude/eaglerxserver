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

package net.lax1dude.eaglercraft.backend.eaglerweb.bungee;

import java.io.IOException;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebPlatform;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

class CommandEaglerWeb extends Command {

	private final PlatformPluginBungee plugin;

	CommandEaglerWeb(PlatformPluginBungee plugin) {
		super("eaglerweb", "eaglercraft.eaglerweb.refresh");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 1 && "refresh".equalsIgnoreCase(args[0])) {
			IEaglerWebPlatform.IHandleRefresh handler = plugin.handleRefresh;
			if (handler == null) {
				BaseComponent comp = new TextComponent("Plugin is not enabled!");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return;
			}
			BaseComponent comp = new TextComponent("Indexing pages, please wait...");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
			int cnt;
			try {
				cnt = handler.refresh();
			} catch (IOException ex) {
				plugin.logger().error("Failed to index pages!", ex);
				comp = new TextComponent("Failed to index pages! (Check Proxy Log)");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				comp = new TextComponent(ex.toString());
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return;
			}
			comp = new TextComponent("Indexed " + cnt + " pages total!");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
		} else {
			BaseComponent comp = new TextComponent("Usage: /eaglerweb refresh");
			comp.setColor(ChatColor.RED);
			sender.sendMessage(comp);
		}
	}

}
