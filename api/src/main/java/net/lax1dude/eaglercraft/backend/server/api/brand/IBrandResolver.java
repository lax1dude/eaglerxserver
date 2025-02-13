package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;
import java.util.function.Consumer;

public interface IBrandResolver {

	void resolveEaglerPlayerBrand(UUID playerUUID, Consumer<UUID> callback);

}
