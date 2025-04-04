package net.lax1dude.eaglercraft.backend.rpc.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public class BukkitEventDispatchAdapter implements IEventDispatchAdapter<Player> {

	private IEaglerXBackendRPC<Player> api;
	private final Plugin platformPlugin;
	private final PluginManager eventMgr;

	public BukkitEventDispatchAdapter(Plugin platformPlugin, PluginManager eventMgr) {
		this.platformPlugin = platformPlugin;
		this.eventMgr = eventMgr;
	}

	@Override
	public void setAPI(IEaglerXBackendRPC<Player> api) {
		this.api = api;
	}

	@Override
	public void dispatchPlayerReadyEvent(IEaglerPlayer<Player> player) {
		eventMgr.callEvent(new BukkitPlayerReadyEventImpl(api, player));
	}

	@Override
	public void dispatchVoiceCapableEvent(IEaglerPlayer<Player> player) {
		eventMgr.callEvent(new BukkitVoiceCapableEventImpl(api, player));
	}

	@Override
	public void dispatchVoiceChangeEvent(IEaglerPlayer<Player> player, EnumVoiceState stateOld, EnumVoiceState stateNew) {
		eventMgr.callEvent(new BukkitVoiceChangeEventImpl(api, player, stateOld, stateNew));
	}

}
