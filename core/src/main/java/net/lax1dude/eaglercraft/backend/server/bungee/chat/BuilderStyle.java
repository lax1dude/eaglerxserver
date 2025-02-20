package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentStyle;

class BuilderStyle<ParentType> implements IBuilderStyle<ParentType> {

	private final ParentType parent;

	EnumChatColor color;
	boolean bold;
	boolean italic;
	boolean strikethrough;
	boolean underline;
	boolean obfuscated;

	BuilderStyle(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		return parent;
	}

	@Override
	public IBuilderStyle<ParentType> color(EnumChatColor color) {
		this.color = color;
		return this;
	}

	@Override
	public IBuilderStyle<ParentType> bold(boolean bold) {
		this.bold = bold;
		return this;
	}

	@Override
	public IBuilderStyle<ParentType> italic(boolean italic) {
		this.italic = italic;
		return this;
	}

	@Override
	public IBuilderStyle<ParentType> strikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}

	@Override
	public IBuilderStyle<ParentType> underline(boolean underline) {
		this.underline = underline;
		return this;
	}

	@Override
	public IBuilderStyle<ParentType> obfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}

	void applyTo(BaseComponent ret) {
		ComponentStyle style = new ComponentStyle();
		if(color != null) {
			switch(color) {
			case BLACK:
				style.setColor(ChatColor.BLACK);
				break;
			case DARK_BLUE:
				style.setColor(ChatColor.DARK_BLUE);
				break;
			case DARK_GREEN:
				style.setColor(ChatColor.DARK_GREEN);
				break;
			case DARK_AQUA:
				style.setColor(ChatColor.DARK_AQUA);
				break;
			case DARK_RED:
				style.setColor(ChatColor.DARK_RED);
				break;
			case DARK_PURPLE:
				style.setColor(ChatColor.DARK_PURPLE);
				break;
			case GOLD:
				style.setColor(ChatColor.GOLD);
				break;
			case GRAY:
				style.setColor(ChatColor.GRAY);
				break;
			case DARK_GRAY:
				style.setColor(ChatColor.DARK_GRAY);
				break;
			case BLUE:
				style.setColor(ChatColor.BLUE);
				break;
			case GREEN:
				style.setColor(ChatColor.GREEN);
				break;
			case AQUA:
				style.setColor(ChatColor.AQUA);
				break;
			case RED:
				style.setColor(ChatColor.RED);
				break;
			case LIGHT_PURPLE:
				style.setColor(ChatColor.LIGHT_PURPLE);
				break;
			case YELLOW:
				style.setColor(ChatColor.YELLOW);
				break;
			case WHITE:
				style.setColor(ChatColor.WHITE);
				break;
			}
		}
		if(bold) {
			style.setBold(true);
		}
		if(italic) {
			style.setItalic(true);
		}
		if(strikethrough) {
			style.setStrikethrough(true);
		}
		if(underline) {
			style.setUnderlined(true);
		}
		if(obfuscated) {
			style.setObfuscated(true);
		}
		ret.setStyle(style);
	}

}
