package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.UnsupportedOn;

public interface IEaglercraftLoginEvent<PlayerObject, ComponentObject>
		extends IBaseLoginEvent<PlayerObject>, ICancellableEvent {

	ComponentObject getMessage();

	void setMessage(ComponentObject kickMessage);

	void setMessage(String kickMessage);

	boolean isLoginStateRedirectSupported();

	String getRedirectAddress();

	void setRedirectAddress(String addr);

	String getProfileUsername();

	void setProfileUsername(String username);

	UUID getProfileUUID();

	@UnsupportedOn({ EnumPlatformType.BUKKIT })
	void setProfileUUID(UUID uuid);

	String getRequestedServer();

	void setRequestedServer(String server);

	default void setKickMessage(ComponentObject kickMessage) {
		setCancelled(true);
		setMessage(kickMessage);
		setRedirectAddress(null);
	}

	default void setKickMessage(String kickMessage) {
		setCancelled(true);
		setMessage(kickMessage);
		setRedirectAddress(null);
	}

	default void setKickRedirect(String addr) {
		if(!isLoginStateRedirectSupported()) {
			throw new UnsupportedOperationException("Login state redirect is not supported by this client");
		}
		setCancelled(true);
		setMessage((ComponentObject) null);
		setRedirectAddress(addr);
	}

}
