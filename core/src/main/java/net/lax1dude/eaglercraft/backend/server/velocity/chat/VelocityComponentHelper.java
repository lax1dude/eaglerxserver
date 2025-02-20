package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.velocity.PlatformPluginVelocity;

public class VelocityComponentHelper implements IPlatformComponentHelper {

	private final PlatformPluginVelocity plugin;
	private final VelocityComponentBuilder builder;

	public VelocityComponentHelper(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
		this.builder = new VelocityComponentBuilder();
	}

	@Override
	public IPlatformComponentBuilder builder() {
		return builder;
	}

	@Override
	public Object parseLegacySection(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseLegacyJSON(String str) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseModernJSON(String str) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serializeLegacySection(Object component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serializeLegacyJSON(Object component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serializeModernJSON(Object component) {
		// TODO Auto-generated method stub
		return null;
	}

}
