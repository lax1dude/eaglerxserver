package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.notifications.EnumBadgePriority;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;

public class NotificationBuilderLocal<ComponentObject> implements INotificationBuilder<ComponentObject> {

	final net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationBuilder<ComponentObject> delegate;
	boolean managed;

	NotificationBuilderLocal(net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationBuilder<ComponentObject> delegate) {
		this.delegate = delegate;
	}

	@Override
	public INotificationBuilder<ComponentObject> copyFrom(INotificationBuilder<ComponentObject> input) {
		delegate.copyFrom(((NotificationBuilderLocal<ComponentObject>) input).delegate);
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> copyFrom(INotificationBadge input) {
		NotificationBadgeHelper.NotificationBadgeLocal badgeLocal = NotificationBadgeHelper.unwrap(input);
		delegate.copyFrom(badgeLocal.packet);
		managed = badgeLocal.managed;
		return this;
	}

	@Override
	public UUID getBadgeUUID() {
		return delegate.getBadgeUUID();
	}

	@Override
	public INotificationBuilder<ComponentObject> setBadgeUUID(UUID uuid) {
		delegate.setBadgeUUID(uuid);
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBadgeUUIDRandom() {
		delegate.setBadgeUUIDRandom();
		return this;
	}

	@Override
	public ComponentObject getBodyComponent() {
		return delegate.getBodyComponent();
	}

	@Override
	public INotificationBuilder<ComponentObject> setBodyComponent(ComponentObject component) {
		delegate.setBodyComponent(component);
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBodyComponent(String text) {
		delegate.setBodyComponent(text);
		return this;
	}

	@Override
	public ComponentObject getTitleComponent() {
		return delegate.getTitleComponent();
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleComponent(ComponentObject component) {
		delegate.setTitleComponent(component);
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleComponent(String text) {
		delegate.setTitleComponent(text);
		return this;
	}

	@Override
	public ComponentObject getSourceComponent() {
		return delegate.getSourceComponent();
	}

	@Override
	public INotificationBuilder<ComponentObject> setSourceComponent(ComponentObject component) {
		delegate.setSourceComponent(component);
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setSourceComponent(String text) {
		delegate.setSourceComponent(text);
		return this;
	}

	@Override
	public long getOriginalTimestampSec() {
		return delegate.getOriginalTimestampSec();
	}

	@Override
	public INotificationBuilder<ComponentObject> setOriginalTimestampSec(long timestamp) {
		delegate.setOriginalTimestampSec(timestamp);
		return this;
	}

	@Override
	public boolean getSilent() {
		return delegate.getSilent();
	}

	@Override
	public INotificationBuilder<ComponentObject> setSilent(boolean silent) {
		delegate.setSilent(silent);
		return this;
	}

	@Override
	public boolean getManaged() {
		return managed;
	}

	@Override
	public INotificationBuilder<ComponentObject> setManaged(boolean managed) {
		this.managed = managed;
		return this;
	}

	@Override
	public EnumBadgePriority getPriority() {
		return NotificationBadgeHelper.wrap(delegate.getPriority());
	}

	@Override
	public INotificationBuilder<ComponentObject> setPriority(EnumBadgePriority priority) {
		delegate.setPriority(NotificationBadgeHelper.unwrap(priority));
		return this;
	}

	@Override
	public UUID getMainIconUUID() {
		return delegate.getMainIconUUID();
	}

	@Override
	public INotificationBuilder<ComponentObject> setMainIconUUID(UUID uuid) {
		delegate.setMainIconUUID(uuid);
		return this;
	}

	@Override
	public UUID getTitleIconUUID() {
		return delegate.getTitleIconUUID();
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleIconUUID(UUID uuid) {
		delegate.setTitleIconUUID(uuid);
		return this;
	}

	@Override
	public int getHideAfterSec() {
		return delegate.getHideAfterSec();
	}

	@Override
	public INotificationBuilder<ComponentObject> setHideAfterSec(int seconds) {
		delegate.setHideAfterSec(seconds);
		return this;
	}

	@Override
	public int getExpireAfterSec() {
		return delegate.getExpireAfterSec();
	}

	@Override
	public INotificationBuilder<ComponentObject> setExpireAfterSec(int seconds) {
		delegate.setExpireAfterSec(seconds);
		return this;
	}

	@Override
	public int getBackgroundColor() {
		return delegate.getBackgroundColor();
	}

	@Override
	public INotificationBuilder<ComponentObject> setBackgroundColor(int color) {
		delegate.setBackgroundColor(color);
		return this;
	}

	@Override
	public int getBodyTxtColorRGB() {
		return delegate.getBodyTxtColorRGB();
	}

	@Override
	public INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int color) {
		delegate.setBodyTxtColorRGB(color);
		return this;
	}

	@Override
	public int getTitleTxtColorRGB() {
		return delegate.getTitleTxtColorRGB();
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int color) {
		delegate.setTitleTxtColorRGB(color);
		return this;
	}

	@Override
	public int getSourceTxtColorRGB() {
		return delegate.getSourceTxtColorRGB();
	}

	@Override
	public INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int color) {
		delegate.setSourceTxtColorRGB(color);
		return this;
	}

	@Override
	public INotificationBadge buildPacket() {
		return NotificationBadgeHelper.wrap(delegate.buildPacket(), managed);
	}

}
