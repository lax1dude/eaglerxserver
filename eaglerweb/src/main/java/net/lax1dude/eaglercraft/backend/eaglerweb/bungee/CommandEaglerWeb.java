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
		if(args.length == 1 && "refresh".equalsIgnoreCase(args[0])) {
			IEaglerWebPlatform.IHandleRefresh handler = plugin.handleRefresh;
			if(handler == null) {
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
			}catch(IOException ex) {
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
		}else {
			BaseComponent comp = new TextComponent("Usage: /eaglerweb refresh");
			comp.setColor(ChatColor.RED);
			sender.sendMessage(comp);
		}
	}

}
