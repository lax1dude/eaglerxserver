/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import java.util.List;

import com.google.gson.JsonParseException;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BungeeComponentHelper implements IPlatformComponentHelper {

	public static final boolean LEGACY_FLAG_SUPPORT;
	public static final ClickEvent.Action CLICK_ACTION_COPY_TO_CLIPBOARD;

	static {
		boolean b;
		try {
			BaseComponent.class.getMethod("setLegacy", boolean.class);
			b = true;
		}catch(NoSuchMethodException | SecurityException ex) {
			b = false;
		}
		LEGACY_FLAG_SUPPORT = b;
		ClickEvent.Action action;
		try {
			action = ClickEvent.Action.valueOf("COPY_TO_CLIPBOARD");
		}catch(IllegalArgumentException ex) {
			action = null;
		}
		CLICK_ACTION_COPY_TO_CLIPBOARD = action;
	}

	private final BungeeComponentBuilder builder = new BungeeComponentBuilder();
	private final Object kickAlreadyPlayer;

	public BungeeComponentHelper(Object kickAlreadyPlayer) {
		this.kickAlreadyPlayer = kickAlreadyPlayer;
	}

	@Override
	public IPlatformComponentBuilder builder() {
		return builder;
	}

	@Override
	public Class<?> getComponentType() {
		return BaseComponent.class;
	}

	@Override
	public Object getStandardKickAlreadyPlaying() {
		return kickAlreadyPlayer;
	}

	@Override
	public String serializeLegacySection(Object component) {
		String lt = ((BaseComponent) component).toLegacyText();
		if (((BaseComponent) component).getColorRaw() == null && (lt.startsWith("\u00A7f") || lt.startsWith("\u00A7F"))) {
			lt = lt.substring(2);
		}
		return lt;
	}

	@Override
	public String serializePlainText(Object component) {
		return ((BaseComponent) component).toPlainText();
	}

	@Override
	public String serializeGenericJSON(Object component) {
		return ComponentSerializer.toString((BaseComponent) component);
	}

	@Override
	public String serializeLegacyJSON(Object component) {
		BaseComponent bc = (BaseComponent) component;
		if(LEGACY_FLAG_SUPPORT) {
			setLegacyHover(bc, true);
		}
		return ComponentSerializer.toString(bc);
	}

	@Override
	public String serializeModernJSON(Object component) {
		BaseComponent bc = (BaseComponent) component;
		if(LEGACY_FLAG_SUPPORT) {
			setLegacyHover(bc, false);
		}
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
		if(component instanceof TranslatableComponent cmp) {
			List<BaseComponent> with = cmp.getWith();
			if(with != null) {
				for(int i = 0, l = with.size(); i < l; ++i) {
					setLegacyHover(with.get(0), legacy);
				}
			}
		}
	}

	@Override
	public Object parseGenericJSON(String json) throws IllegalArgumentException {
		BaseComponent[] components;
		try {
			components = ComponentSerializer.parse(json);
		}catch(IllegalArgumentException ex) {
			throw ex;
		}catch(JsonParseException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex.getCause());
		}catch(Exception ex) {
			throw new IllegalArgumentException("Could not parse JSON chat component", ex);
		}
		BaseComponent ret;
		if(components.length == 1) {
			ret = components[0];
		}else if(components.length == 0) {
			ret = new TextComponent();
		}else {
			ret = components[0];
			for(int i = 1; i < components.length; ++i) {
				ret.addExtra(components[i]);
			}
		}

		if (ret instanceof TextComponent textRet && ret.getColor() == ChatColor.WHITE && textRet.getText().equals(json)) {
			throw new IllegalArgumentException("Could not parse JSON chat component", new Exception("Not a valid JSON component"));
		}

		return ret;
	}

	@Override
	public Object parseLegacyJSON(String json) throws IllegalArgumentException {
		return parseGenericJSON(json);
	}

	@Override
	public Object parseModernJSON(String json) throws IllegalArgumentException {
		return parseGenericJSON(json);
	}

	@Override
	public Object parseLegacyText(String text) throws IllegalArgumentException {
		BaseComponent[] components = TextComponent.fromLegacyText(text);
		BaseComponent ret;
		if(components.length == 1) {
			ret = components[0];
		}else if(components.length == 0) {
			ret = new TextComponent();
		}else {
			ret = components[0];
			for(int i = 1; i < components.length; ++i) {
				ret.addExtra(components[i]);
			}
		}
		return ret;
	}

}
