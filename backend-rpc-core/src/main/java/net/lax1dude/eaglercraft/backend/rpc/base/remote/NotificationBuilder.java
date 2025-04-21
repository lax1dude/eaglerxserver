package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.EnumBadgePriority;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBadge;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCNotifBadgeShow;

public class NotificationBuilder<ComponentObject> implements INotificationBuilder<ComponentObject> {

	private final IPlatformComponentHelper componentHelper;

	private UUID badgeUUID = null;
	private ComponentObject bodyComponent = null;
	private ComponentObject titleComponent = null;
	private ComponentObject sourceComponent = null;
	private long originalTimestampSec = 0l;
	private boolean silent = false;
	private boolean managed = false;
	private EnumBadgePriority priority = EnumBadgePriority.NORMAL;
	private UUID mainIconUUID = null;
	private UUID titleIconUUID = null;
	private int hideAfterSec = 10;
	private int expireAfterSec = 3600;
	private int backgroundColor = 0xFFFFFF;
	private int bodyTxtColor = 0xFFFFFF;
	private int titleTxtColor = 0xFFFFFF;
	private int sourceTxtColor = 0xFFFFFF;

	private CPacketRPCNotifBadgeShow packetCache = null;
	private boolean packetDirty = true;

	public NotificationBuilder(IPlatformComponentHelper componentHelper) {
		this.componentHelper = componentHelper;
		this.originalTimestampSec = System.currentTimeMillis() / 1000l;
	}

	@Override
	public INotificationBuilder<ComponentObject> copyFrom(INotificationBuilder<ComponentObject> input) {
		NotificationBuilder<ComponentObject> inputCasted = (NotificationBuilder<ComponentObject>) input;
		badgeUUID = inputCasted.badgeUUID;
		bodyComponent = inputCasted.bodyComponent;
		titleComponent = inputCasted.titleComponent;
		sourceComponent = inputCasted.sourceComponent;
		originalTimestampSec = inputCasted.originalTimestampSec;
		silent = inputCasted.silent;
		priority = inputCasted.priority;
		mainIconUUID = inputCasted.mainIconUUID;
		titleIconUUID = inputCasted.titleIconUUID;
		hideAfterSec = inputCasted.hideAfterSec;
		backgroundColor = inputCasted.backgroundColor;
		bodyTxtColor = inputCasted.bodyTxtColor;
		titleTxtColor = inputCasted.titleTxtColor;
		sourceTxtColor = inputCasted.sourceTxtColor;
		packetCache = !inputCasted.packetDirty ? inputCasted.packetCache : null;
		packetDirty = inputCasted.packetDirty;
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> copyFrom(INotificationBadge input) {
		CPacketRPCNotifBadgeShow packet = NotificationBadgeWrapper.unwrap(input);
		badgeUUID = packet.badgeUUID;
		try {
			bodyComponent = (ComponentObject) componentHelper.parseLegacyJSON(packet.bodyComponent);
		}catch(Exception t) {
			bodyComponent = (ComponentObject) componentHelper.createTextComponent(packet.bodyComponent);
		}
		try {
			titleComponent = (ComponentObject) componentHelper.parseLegacyJSON(packet.titleComponent);
		}catch(Exception t) {
			titleComponent = (ComponentObject) componentHelper.createTextComponent(packet.titleComponent);
		}
		try {
			sourceComponent = (ComponentObject) componentHelper.parseLegacyJSON(packet.sourceComponent);
		}catch(Exception t) {
			sourceComponent = (ComponentObject) componentHelper.createTextComponent(packet.sourceComponent);
		}
		originalTimestampSec = packet.originalTimestampSec;
		silent = packet.silent;
		managed = packet.managed;
		priority = switch(packet.priority) {
		case LOW -> EnumBadgePriority.LOW;
		default -> EnumBadgePriority.NORMAL;
		case HIGHER -> EnumBadgePriority.HIGHER;
		case HIGHEST -> EnumBadgePriority.HIGHEST;
		};
		mainIconUUID = packet.mainIconUUID;
		titleIconUUID = packet.titleIconUUID;
		hideAfterSec = packet.hideAfterSec;
		backgroundColor = packet.backgroundColor;
		bodyTxtColor = packet.bodyTxtColor;
		titleTxtColor = packet.titleTxtColor;
		sourceTxtColor = packet.sourceTxtColor;
		packetCache = packet;
		packetDirty = false;
		return this;
	}

	@Override
	public UUID getBadgeUUID() {
		return badgeUUID;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBadgeUUID(UUID uuid) {
		if(uuid == null) {
			throw new NullPointerException("uuid");
		}
		this.badgeUUID = uuid;
		this.packetDirty = true;
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBadgeUUIDRandom() {
		this.badgeUUID = UUID.randomUUID();
		this.packetDirty = true;
		return this;
	}

	@Override
	public ComponentObject getBodyComponent() {
		return this.bodyComponent;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBodyComponent(ComponentObject component) {
		this.bodyComponent = component;
		this.packetDirty = true;
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBodyComponent(String text) {
		this.bodyComponent = text != null ? (ComponentObject) componentHelper.createTextComponent(text) : null;
		this.packetDirty = true;
		return this;
	}

	@Override
	public ComponentObject getTitleComponent() {
		return this.titleComponent;
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleComponent(ComponentObject component) {
		this.titleComponent = component;
		this.packetDirty = true;
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleComponent(String text) {
		this.titleComponent = text != null ? (ComponentObject) componentHelper.createTextComponent(text) : null;
		this.packetDirty = true;
		return this;
	}

	@Override
	public ComponentObject getSourceComponent() {
		return this.sourceComponent;
	}

	@Override
	public INotificationBuilder<ComponentObject> setSourceComponent(ComponentObject component) {
		this.sourceComponent = component;
		this.packetDirty = true;
		return this;
	}

	@Override
	public INotificationBuilder<ComponentObject> setSourceComponent(String text) {
		this.sourceComponent = text != null ? (ComponentObject) componentHelper.createTextComponent(text) : null;
		this.packetDirty = true;
		return this;
	}

	@Override
	public long getOriginalTimestampSec() {
		return this.originalTimestampSec;
	}

	@Override
	public INotificationBuilder<ComponentObject> setOriginalTimestampSec(long timestamp) {
		this.originalTimestampSec = timestamp;
		this.packetDirty = true;
		return this;
	}

	@Override
	public boolean getSilent() {
		return this.silent;
	}

	@Override
	public INotificationBuilder<ComponentObject> setSilent(boolean silent) {
		this.silent = silent;
		this.packetDirty = true;
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
		return this.priority;
	}

	@Override
	public INotificationBuilder<ComponentObject> setPriority(EnumBadgePriority priority) {
		if(priority == null) {
			throw new NullPointerException("priority");
		}
		this.priority = priority;
		this.packetDirty = true;
		return this;
	}

	@Override
	public UUID getMainIconUUID() {
		return this.mainIconUUID;
	}

	@Override
	public INotificationBuilder<ComponentObject> setMainIconUUID(UUID uuid) {
		this.mainIconUUID = uuid;
		this.packetDirty = true;
		return this;
	}

	@Override
	public UUID getTitleIconUUID() {
		return this.titleIconUUID;
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleIconUUID(UUID uuid) {
		this.titleIconUUID = uuid;
		this.packetDirty = true;
		return this;
	}

	@Override
	public int getHideAfterSec() {
		return this.hideAfterSec;
	}

	@Override
	public INotificationBuilder<ComponentObject> setHideAfterSec(int seconds) {
		this.hideAfterSec = seconds;
		this.packetDirty = true;
		return this;
	}

	@Override
	public int getExpireAfterSec() {
		return this.expireAfterSec;
	}

	@Override
	public INotificationBuilder<ComponentObject> setExpireAfterSec(int seconds) {
		this.expireAfterSec = seconds;
		this.packetDirty = true;
		return this;
	}

	@Override
	public int getBackgroundColor() {
		return this.backgroundColor;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBackgroundColor(int color) {
		this.backgroundColor = color;
		this.packetDirty = true;
		return this;
	}

	@Override
	public int getBodyTxtColorRGB() {
		return this.bodyTxtColor;
	}

	@Override
	public INotificationBuilder<ComponentObject> setBodyTxtColorRGB(int color) {
		this.bodyTxtColor = color;
		this.packetDirty = true;
		return this;
	}

	@Override
	public int getTitleTxtColorRGB() {
		return this.titleTxtColor;
	}

	@Override
	public INotificationBuilder<ComponentObject> setTitleTxtColorRGB(int color) {
		this.titleTxtColor = color;
		this.packetDirty = true;
		return this;
	}

	@Override
	public int getSourceTxtColorRGB() {
		return this.sourceTxtColor;
	}

	@Override
	public INotificationBuilder<ComponentObject> setSourceTxtColorRGB(int color) {
		this.sourceTxtColor = color;
		this.packetDirty = true;
		return this;
	}

	@Override
	public INotificationBadge buildPacket() {
		if(packetDirty || packetCache == null) {
			if(badgeUUID == null) {
				badgeUUID = UUID.randomUUID();
			}else if(badgeUUID.getMostSignificantBits() == 0l && badgeUUID.getLeastSignificantBits() == 0l) {
				throw new IllegalStateException("Badge UUID cannot be 0!");
			}
			CPacketRPCNotifBadgeShow.EnumBadgePriority internalPriority = switch(priority) {
			case LOW -> CPacketRPCNotifBadgeShow.EnumBadgePriority.LOW;
			default -> CPacketRPCNotifBadgeShow.EnumBadgePriority.NORMAL;
			case HIGHER -> CPacketRPCNotifBadgeShow.EnumBadgePriority.HIGHER;
			case HIGHEST -> CPacketRPCNotifBadgeShow.EnumBadgePriority.HIGHEST;
			};
			String bodyComp = bodyComponent != null ? componentHelper.serializeLegacyJSON(bodyComponent) : "";
			if(bodyComp.length() > 32767) {
				throw new IllegalStateException("Body component is longer than 32767 chars serialized!");
			}
			String titleComp = titleComponent != null ? componentHelper.serializeLegacyJSON(titleComponent) : "";
			if(titleComp.length() > 255) {
				throw new IllegalStateException("Title component is longer than 255 chars serialized!");
			}
			String sourceComp = sourceComponent != null ? componentHelper.serializeLegacyJSON(sourceComponent) : "";
			if(sourceComp.length() > 255) {
				throw new IllegalStateException("Body component is longer than 255 chars serialized!");
			}
			packetCache = new CPacketRPCNotifBadgeShow(badgeUUID, bodyComp, titleComp, sourceComp, originalTimestampSec,
					mainIconUUID, titleIconUUID, silent, managed, internalPriority, hideAfterSec, expireAfterSec,
					backgroundColor, bodyTxtColor, titleTxtColor, sourceTxtColor);
			packetDirty = false;
		}
		return NotificationBadgeWrapper.wrap(packetCache);
	}

}
