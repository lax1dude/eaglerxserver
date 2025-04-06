package net.lax1dude.eaglercraft.backend.plan.bungee;

import net.lax1dude.eaglercraft.backend.plan.PlanHook;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin {
	@Override
	public void onEnable() {
		PlanHook.hookIntoPlan(EaglerXServerAPI.instance());
	}
}
