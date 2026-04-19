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

package net.lax1dude.eaglercraft.backend.eaglermotd.bungee;

import java.io.IOException;

import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDPlatform;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

class CommandEaglerMOTD extends Command {

	private final PlatformPluginBungee plugin;

	CommandEaglerMOTD(PlatformPluginBungee plugin) {
		super("eaglermotd", "eaglercraft.eaglermotd.reload");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
			IEaglerMOTDPlatform.IHandleReload handler = plugin.handleReload;
			if (handler == null) {
				BaseComponent comp = new TextComponent("Plugin is not enabled!");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return;
			}
			TextComponent comp;
			try {
				handler.reload();
			} catch (IOException | JsonParseException ex) {
				plugin.logger().error("Failed to reload config files!", ex);
				comp = new TextComponent("Failed to reload config files! (Check Server Log)");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				comp = new TextComponent(ex.toString());
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return;
			}
			comp = new TextComponent("Configuration reloaded.");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
		} else {
			BaseComponent comp = new TextComponent("Usage: /eaglermotd reload");
			comp.setColor(ChatColor.RED);
			sender.sendMessage(comp);
		}
	}

}
