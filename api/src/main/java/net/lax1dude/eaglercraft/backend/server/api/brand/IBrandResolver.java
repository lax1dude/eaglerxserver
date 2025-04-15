package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public interface IBrandResolver {

	void resolvePlayerBrand(@Nonnull UUID playerUUID, @Nonnull Consumer<UUID> callback);

	void resolvePlayerRegisteredBrand(@Nonnull UUID playerUUID, @Nonnull BiConsumer<UUID, IBrandRegistration> callback);

}
