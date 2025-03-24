package net.lax1dude.eaglercraft.backend.rpc.api.notifications;

import java.util.UUID;

public interface INotificationBuilder<ComponentObject> {

	INotificationBuilder<ComponentObject> copyFrom(INotificationBuilder<ComponentObject> input);

	INotificationBuilder<ComponentObject> copyFrom(INotificationBadge input);

	UUID getBadgeUUID();

	INotificationBuilder<ComponentObject> setBadgeUUID(UUID uuid);

	INotificationBuilder<ComponentObject> setBadgeUUIDRandom();

	ComponentObject getBodyComponent();

	INotificationBuilder<ComponentObject> setBodyComponent(ComponentObject component);

	INotificationBuilder<ComponentObject> setBodyComponent(String text);

	ComponentObject getTitleComponent();

	INotificationBuilder<ComponentObject> setTitleComponent(ComponentObject component);

	INotificationBuilder<ComponentObject> setTitleComponent(String text);

	ComponentObject getSourceComponent();

	INotificationBuilder<ComponentObject> setSourceComponent(ComponentObject component);

	INotificationBuilder<ComponentObject> setSourceComponent(String text);

	long getOriginalTimestampSec();

	INotificationBuilder<ComponentObject> setOriginalTimestampSec(long timestamp);

	boolean getSilent();

	INotificationBuilder<ComponentObject> setSilent(boolean silent);

	EnumBadgePriority getPriority();

	INotificationBuilder<ComponentObject> setPriority(EnumBadgePriority priority);

	UUID getMainIconUUID();

	INotificationBuilder<ComponentObject> setMainIconUUID(UUID uuid);

	UUID getTitleIconUUID();

	INotificationBuilder<ComponentObject> setTitleIconUUID(UUID uuid);

	int getHideAfterSec();

	INotificationBuilder<ComponentObject> setHideAfterSec(int seconds);

	int getExpireAfterSec();

	INotificationBuilder<ComponentObject> setExpireAfterSec(int seconds);

	int getBackgroundColor();

	INotificationBuilder<ComponentObject> setBackgroundColor(int color);

	default INotificationBuilder<ComponentObject> setBackgroundColor(int red, int green, int blue) {
		return setBackgroundColor((red << 16) | (green << 8) | blue);
	}

	int getBodyTxtColorRGB();

	INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int color);

	default INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int red, int green, int blue) {
		return setBodyTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	int getTitleTxtColorRGB();

	INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int color);

	default INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int red, int green, int blue) {
		return setTitleTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	int getSourceTxtColorRGB();

	INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int color);

	default INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int red, int green, int blue) {
		return setSourceTxtColorRGB((red << 16) | (green << 8) | blue);
	}

	INotificationBadge buildPacket();

}
