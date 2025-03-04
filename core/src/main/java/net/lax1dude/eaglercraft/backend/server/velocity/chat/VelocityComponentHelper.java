package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import com.google.gson.JsonParseException;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;

public class VelocityComponentHelper implements IPlatformComponentHelper {

	private final LegacyComponentSerializer legacySection;
	private final PlainTextComponentSerializer plainText;
	private final JSONComponentSerializer legacyJSON;
	private final JSONComponentSerializer modernJSON;
	private final VelocityComponentBuilder builder;

	public VelocityComponentHelper() {
		JSONComponentSerializer.Builder builder = JSONComponentSerializer.builder();
		try {
			builder.legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get());
		}catch(Throwable t) {
			try {
				Class<?> c = Class.forName("com.velocitypowered.proxy.protocol.util.VelocityLegacyHoverEventSerializer");
				builder.legacyHoverEventSerializer((LegacyHoverEventSerializer)c.getDeclaredField("INSTANCE").get(null));
			}catch(Throwable tt) {
				throw new RuntimeException("Legacy hover event serializer is unavailable! (downgrade velocity)");
			}
		}
		this.legacySection = LegacyComponentSerializer.legacySection();
		this.plainText = PlainTextComponentSerializer.plainText();
		this.legacyJSON = builder.build();
		this.modernJSON = JSONComponentSerializer.json();
		this.builder = new VelocityComponentBuilder();
	}

	@Override
	public IPlatformComponentBuilder builder() {
		return builder;
	}

	@Override
	public Class<?> getComponentType() {
		return Component.class;
	}

	@Override
	public String serializeLegacySection(Object component) {
		return legacySection.serialize((Component) component);
	}

	@Override
	public String serializePlainText(Object component) {
		return plainText.serialize((Component) component);
	}

	@Override
	public String serializeGenericJSON(Object component) {
		return modernJSON.serialize((Component) component);
	}

	@Override
	public String serializeLegacyJSON(Object component) {
		return legacyJSON.serialize((Component) component);
	}

	@Override
	public String serializeModernJSON(Object component) {
		return modernJSON.serialize((Component) component);
	}

	@Override
	public Object parseGenericJSON(String json) throws IllegalArgumentException {
		return parseJSON(modernJSON, json);
	}

	@Override
	public Object parseLegacyJSON(String json) throws IllegalArgumentException {
		return parseJSON(legacyJSON, json);
	}

	@Override
	public Object parseModernJSON(String json) throws IllegalArgumentException {
		return parseJSON(modernJSON, json);
	}

	private Object parseJSON(JSONComponentSerializer serializer, String json) throws IllegalArgumentException {
		try {
			Object ret = serializer.deserialize(json);
			if(ret == null) {
				throw new NullPointerException("Deserialization result is null");
			}
			return ret;
		}catch(IllegalArgumentException ex) {
			throw ex;
		}catch(JsonParseException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex.getCause());
		}catch(Exception ex) {
			throw new IllegalArgumentException("Could not parse JSON chat component", ex);
		}
	}

}
