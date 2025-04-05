package net.lax1dude.eaglercraft.backend.plan.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import net.lax1dude.eaglercraft.backend.plan.PlanHook;

import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;

import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;

@Plugin(
	id = PlatformPluginVelocity.PLUGIN_ID,
	name = PlatformPluginVelocity.PLUGIN_NAME,
	authors = {
		"lax1dude",
		"ayunami2000"
	},
	version = PlatformPluginVelocity.PLUGIN_VERSION,
	description = PlatformPluginVelocity.PLUGIN_DESC,
	dependencies = {
		@Dependency(id = EaglerXServerAPI.PLUGIN_ID, optional = false),
		@Dependency(id = "plan", optional = false)
	}
)
public class PlatformPluginVelocity {

	public static final String PLUGIN_ID = "eaglerxplan";
	public static final String PLUGIN_NAME = "EaglercraftXPlan";
	public static final String PLUGIN_AUTHOR = "ayunami2000";
	public static final String PLUGIN_VERSION = "1.0.0";
	public static final String PLUGIN_DESC = "Official Plan player analytics integration plugin for EaglercraftXServer";

	@Subscribe
	public void onProxyInitialize(ProxyInitializeEvent event) {
		PlanHook.hookIntoPlan(EaglerXServerAPI.instance());
	}

}
