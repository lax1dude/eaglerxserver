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

package net.lax1dude.eaglercraft.backend.eaglerweb.bukkit;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebPlatform;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

class CommandEaglerWeb extends Command {

	private final PlatformPluginBukkit plugin;

	protected CommandEaglerWeb(PlatformPluginBukkit plugin) {
		super("eaglerweb", "Can be used to refresh the page index", "/eaglerweb refresh", new ArrayList<>());
		this.plugin = plugin;
		setPermission("eaglercraft.eaglerweb.refresh");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 1 && "refresh".equalsIgnoreCase(args[0])) {
			IEaglerWebPlatform.IHandleRefresh handler = plugin.handleRefresh;
			if (handler == null) {
				BaseComponent comp = new TextComponent("Plugin is not enabled!");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return true;
			}
			BaseComponent comp = new TextComponent("Indexing pages, please wait...");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
			int cnt;
			try {
				cnt = handler.refresh();
			} catch (IOException ex) {
				plugin.logger().error("Failed to index pages!", ex);
				comp = new TextComponent("Failed to index pages! (Check Server Log)");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				comp = new TextComponent(ex.toString());
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return true;
			}
			comp = new TextComponent("Indexed " + cnt + " pages total!");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
			return true;
		} else {
			return false;
		}
	}

}
