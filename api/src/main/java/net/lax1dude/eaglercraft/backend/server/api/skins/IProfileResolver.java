package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;
import java.util.function.Consumer;

public interface IProfileResolver {

	void resolveVanillaUUIDFromUsername(String username, Consumer<UUID> callback);

	void resolveVanillaTexturesFromUUID(UUID uuid, Consumer<TexturesProperty> callback);

	default void resolveVanillaTexturesFromUsername(String username, Consumer<TexturesProperty> callback) {
		resolveVanillaUUIDFromUsername(username, (uuid) -> {
			if(uuid != null) {
				resolveVanillaTexturesFromUUID(uuid, callback);
			}else {
				callback.accept(null);
			}
		});
	}

	TexturesResult decodeVanillaTextures(String propertyValue);

	default TexturesResult decodeVanillaTextures(TexturesProperty property) {
		return decodeVanillaTextures(property.getValue());
	}

}
