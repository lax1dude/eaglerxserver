package net.lax1dude.eaglercraft.backend.eaglerweb.bukkit;

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
		if(args.length == 1 && "refresh".equalsIgnoreCase(args[0])) {
			IEaglerWebPlatform.IHandleRefresh handler = plugin.handleRefresh;
			if(handler == null) {
				BaseComponent comp = new TextComponent("Plugin is not enabled!");
				comp.setColor(ChatColor.RED);
				sender.sendMessage(comp);
				return true;
			}
			BaseComponent comp = new TextComponent("Indexing pages, please wait...");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
			int cnt = handler.refresh();
			comp = new TextComponent("Indexed " + cnt + " pages total!");
			comp.setColor(ChatColor.AQUA);
			sender.sendMessage(comp);
			return true;
		}else {
			return false;
		}
	}

}
