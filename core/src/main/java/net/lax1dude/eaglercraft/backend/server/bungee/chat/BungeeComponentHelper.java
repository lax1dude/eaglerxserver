package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import java.util.List;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BungeeComponentHelper implements IPlatformComponentHelper {

	private final BungeeComponentBuilder builder = new BungeeComponentBuilder();

	@Override
	public IPlatformComponentBuilder builder() {
		return builder;
	}

	@Override
	public String serializeLegacySection(Object component) {
		return ((BaseComponent) component).toLegacyText();
	}

	@Override
	public String serializeGenericJSON(Object component) {
		return ComponentSerializer.toString((BaseComponent) component);
	}

	@Override
	public String serializeLegacyJSON(Object component) {
		BaseComponent bc = (BaseComponent) component;
		setLegacyHover(bc, true);
		return ComponentSerializer.toString(bc);
	}

	@Override
	public String serializeModernJSON(Object component) {
		BaseComponent bc = (BaseComponent) component;
		setLegacyHover(bc, false);
		return ComponentSerializer.toString(bc);
	}

	public static void setLegacyHover(BaseComponent component, boolean legacy) {
		HoverEvent evt = component.getHoverEvent();
		if(evt != null) {
			evt.setLegacy(legacy);
		}
		List<BaseComponent> extra = component.getExtra();
		if(extra != null) {
			for(int i = 0, l = extra.size(); i < l; ++i) {
				setLegacyHover(extra.get(0), legacy);
			}
		}
		if(component instanceof TranslatableComponent) {
			List<BaseComponent> with = ((TranslatableComponent) component).getWith();
			if(with != null) {
				for(int i = 0, l = with.size(); i < l; ++i) {
					setLegacyHover(with.get(0), legacy);
				}
			}
		}
	}

}
