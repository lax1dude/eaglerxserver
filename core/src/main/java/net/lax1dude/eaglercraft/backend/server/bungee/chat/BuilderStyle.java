package net.lax1dude.eaglercraft.backend.server.bungee.chat;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderStyle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

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
		if(color != null) {
			switch(color) {
			case BLACK:
				ret.setColor(ChatColor.BLACK);
				break;
			case DARK_BLUE:
				ret.setColor(ChatColor.DARK_BLUE);
				break;
			case DARK_GREEN:
				ret.setColor(ChatColor.DARK_GREEN);
				break;
			case DARK_AQUA:
				ret.setColor(ChatColor.DARK_AQUA);
				break;
			case DARK_RED:
				ret.setColor(ChatColor.DARK_RED);
				break;
			case DARK_PURPLE:
				ret.setColor(ChatColor.DARK_PURPLE);
				break;
			case GOLD:
				ret.setColor(ChatColor.GOLD);
				break;
			case GRAY:
				ret.setColor(ChatColor.GRAY);
				break;
			case DARK_GRAY:
				ret.setColor(ChatColor.DARK_GRAY);
				break;
			case BLUE:
				ret.setColor(ChatColor.BLUE);
				break;
			case GREEN:
				ret.setColor(ChatColor.GREEN);
				break;
			case AQUA:
				ret.setColor(ChatColor.AQUA);
				break;
			case RED:
				ret.setColor(ChatColor.RED);
				break;
			case LIGHT_PURPLE:
				ret.setColor(ChatColor.LIGHT_PURPLE);
				break;
			case YELLOW:
				ret.setColor(ChatColor.YELLOW);
				break;
			case WHITE:
				ret.setColor(ChatColor.WHITE);
				break;
			}
		}
		if(bold) {
			ret.setBold(true);
		}
		if(italic) {
			ret.setItalic(true);
		}
		if(strikethrough) {
			ret.setStrikethrough(true);
		}
		if(underline) {
			ret.setUnderlined(true);
		}
		if(obfuscated) {
			ret.setObfuscated(true);
		}
	}

}
