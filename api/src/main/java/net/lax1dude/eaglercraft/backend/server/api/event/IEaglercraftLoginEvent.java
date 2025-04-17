package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.UnsupportedOn;

public interface IEaglercraftLoginEvent<PlayerObject, ComponentObject>
		extends IBaseLoginEvent<PlayerObject>, ICancellableEvent {

	@Nullable
	ComponentObject getMessage();

	void setMessage(@Nullable ComponentObject kickMessage);

	void setMessage(@Nullable String kickMessage);

	boolean isLoginStateRedirectSupported();

	@Nullable
	String getRedirectAddress();

	void setRedirectAddress(@Nullable String addr);

	@Nonnull
	String getProfileUsername();

	void setProfileUsername(@Nonnull String username);

	@Nonnull
	UUID getProfileUUID();

	@UnsupportedOn({ EnumPlatformType.BUKKIT })
	void setProfileUUID(@Nonnull UUID uuid);

	@Nonnull
	String getRequestedServer();

	void setRequestedServer(@Nonnull String server);

	default void setKickMessage(@Nullable ComponentObject kickMessage) {
		setCancelled(true);
		setMessage(kickMessage);
		setRedirectAddress(null);
	}

	default void setKickMessage(@Nullable String kickMessage) {
		setCancelled(true);
		setMessage(kickMessage);
		setRedirectAddress(null);
	}

	default void setKickRedirect(@Nonnull String addr) {
		if(!isLoginStateRedirectSupported()) {
			throw new UnsupportedOperationException("Login state redirect is not supported by this client");
		}
		if(addr == null) {
			throw new NullPointerException("addr");
		}
		setCancelled(true);
		setMessage((ComponentObject) null);
		setRedirectAddress(addr);
	}

}
