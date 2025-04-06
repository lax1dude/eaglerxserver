package net.lax1dude.eaglercraft.backend.plan.bukkit;

import net.lax1dude.eaglercraft.backend.plan.PlanHook;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class PlatformPluginBukkit extends JavaPlugin {
	@Override
	public void onEnable() {
		PlanHook.hookIntoPlan(EaglerXServerAPI.instance());
	}
}
