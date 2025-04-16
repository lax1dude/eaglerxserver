package net.lax1dude.eaglercraft.backend.server.api.webview;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface ITranslationProvider {

	@Nonnull
	String format(@Nonnull String key, @Nonnull String... args);

}
