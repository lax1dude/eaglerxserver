package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IComponentSerializer;

public class ComponentHelper<ComponentType> implements IComponentSerializer<ComponentType> {

	private final IPlatformComponentHelper platformImpl;

	public ComponentHelper(IPlatformComponentHelper platformImpl) {
		this.platformImpl = platformImpl;
	}

	@Override
	public String serializeLegacySection(ComponentType component) {
		return platformImpl.serializeLegacySection(component);
	}

	@Override
	public String serializePlainText(ComponentType component) {
		return platformImpl.serializePlainText(component);
	}

	@Override
	public String serializeGenericJSON(ComponentType component) {
		return platformImpl.serializeGenericJSON(component);
	}

	@Override
	public String serializeLegacyJSON(ComponentType component) {
		return platformImpl.serializeLegacyJSON(component);
	}

	@Override
	public String serializeModernJSON(ComponentType component) {
		return platformImpl.serializeModernJSON(component);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentType parseGenericJSON(String json) throws IllegalArgumentException {
		return (ComponentType) platformImpl.parseGenericJSON(json);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentType parseLegacyJSON(String json) throws IllegalArgumentException {
		return (ComponentType) platformImpl.parseLegacyJSON(json);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentType parseModernJSON(String json) throws IllegalArgumentException {
		return (ComponentType) platformImpl.parseModernJSON(json);
	}

	@Override
	public String convertJSONToLegacySection(String json) throws IllegalArgumentException {
		return platformImpl.serializeLegacySection(platformImpl.parseGenericJSON(json));
	}

	@Override
	public String convertJSONToPlainText(String json) throws IllegalArgumentException {
		return platformImpl.serializePlainText(platformImpl.parseGenericJSON(json));
	}

}
