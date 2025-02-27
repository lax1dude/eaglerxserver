package net.lax1dude.eaglercraft.backend.rewind_v1_5.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.eaglercraft.backend.rewind_v1_5.IRewindPlatform;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.RewindPluginProtocol;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;

public class PlatformPluginBukkit extends JavaPlugin implements IRewindPlatform<Player> {

	private RewindPluginProtocol<Player> protocol;

	@Override
	public void onLoad() {
		protocol = new RewindPluginProtocol<Player>(this);
	}

	@Override
	public void onEnable() {
		EaglerXServerAPI.instance().getEaglerXRewindService().registerLegacyProtocol(protocol);
	}

	@Override
	public void onDisable() {
		EaglerXServerAPI.instance().getEaglerXRewindService().unregisterLegacyProtocol(protocol);
	}

}
