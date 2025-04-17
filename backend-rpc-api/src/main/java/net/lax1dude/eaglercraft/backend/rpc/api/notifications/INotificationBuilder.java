package net.lax1dude.eaglercraft.backend.rpc.api.notifications;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface INotificationBuilder<ComponentObject> {

	@Nonnull
	INotificationBuilder<ComponentObject> copyFrom(@Nonnull INotificationBuilder<ComponentObject> input);

	@Nonnull
	INotificationBuilder<ComponentObject> copyFrom(@Nonnull INotificationBadge input);

	@Nonnull
	UUID getBadgeUUID();

	@Nonnull
	INotificationBuilder<ComponentObject> setBadgeUUID(@Nonnull UUID uuid);

	@Nonnull
	INotificationBuilder<ComponentObject> setBadgeUUIDRandom();

	@Nullable
	ComponentObject getBodyComponent();

	@Nonnull
	INotificationBuilder<ComponentObject> setBodyComponent(@Nullable ComponentObject component);

	@Nonnull
	INotificationBuilder<ComponentObject> setBodyComponent(@Nullable String text);

	@Nullable
	ComponentObject getTitleComponent();

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleComponent(@Nullable ComponentObject component);

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleComponent(@Nullable String text);

	@Nullable
	ComponentObject getSourceComponent();

	@Nonnull
	INotificationBuilder<ComponentObject> setSourceComponent(@Nullable ComponentObject component);

	@Nonnull
	INotificationBuilder<ComponentObject> setSourceComponent(@Nullable String text);

	long getOriginalTimestampSec();

	@Nonnull
	INotificationBuilder<ComponentObject> setOriginalTimestampSec(long timestamp);

	boolean getSilent();

	@Nonnull
	INotificationBuilder<ComponentObject> setSilent(boolean silent);

	boolean getManaged();

	@Nonnull
	INotificationBuilder<ComponentObject> setManaged(boolean managed);

	@Nonnull
	EnumBadgePriority getPriority();

	@Nonnull
	INotificationBuilder<ComponentObject> setPriority(@Nonnull EnumBadgePriority priority);

	@Nullable
	UUID getMainIconUUID();

	@Nonnull
	INotificationBuilder<ComponentObject> setMainIconUUID(@Nullable UUID uuid);

	@Nullable
	UUID getTitleIconUUID();

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleIconUUID(@Nullable UUID uuid);

	int getHideAfterSec();

	@Nonnull
	INotificationBuilder<ComponentObject> setHideAfterSec(int seconds);

	int getExpireAfterSec();

	@Nonnull
	INotificationBuilder<ComponentObject> setExpireAfterSec(int seconds);

	int getBackgroundColor();

	@Nonnull
	INotificationBuilder<ComponentObject> setBackgroundColor(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setBackgroundColor(int red, int green, int blue) {
		return setBackgroundColor((red << 16) | (green << 8) | blue);
	}

	int getBodyTxtColorRGB();

	@Nonnull
	INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int red, int green, int blue) {
		return setBodyTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	int getTitleTxtColorRGB();

	@Nonnull
	INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int red, int green, int blue) {
		return setTitleTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	int getSourceTxtColorRGB();

	@Nonnull
	INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int color);

	@Nonnull
	default INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int red, int green, int blue) {
		return setSourceTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	@Nonnull
	INotificationBadge buildPacket();

}
