package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IBrandResolver {

	void resolvePlayerBrand(UUID playerUUID, Consumer<UUID> callback);

	void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback);

}
