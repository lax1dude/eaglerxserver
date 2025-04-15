package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IProfileResolver {

	void resolveVanillaUUIDFromUsername(@Nonnull String username, @Nonnull Consumer<UUID> callback);

	void resolveVanillaTexturesFromUUID(@Nonnull UUID uuid, @Nonnull Consumer<TexturesProperty> callback);

	default void resolveVanillaTexturesFromUsername(@Nonnull String username, @Nonnull Consumer<TexturesProperty> callback) {
		resolveVanillaUUIDFromUsername(username, (uuid) -> {
			if(uuid != null) {
				resolveVanillaTexturesFromUUID(uuid, callback);
			}else {
				callback.accept(null);
			}
		});
	}

	@Nullable
	TexturesResult decodeVanillaTextures(@Nonnull String propertyValue);

	@Nullable
	default TexturesResult decodeVanillaTextures(@Nonnull TexturesProperty property) {
		return decodeVanillaTextures(property.getValue());
	}

}
