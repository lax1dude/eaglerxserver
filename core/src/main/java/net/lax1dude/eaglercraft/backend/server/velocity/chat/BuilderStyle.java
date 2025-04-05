package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderStyle;

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

	Component applyTo(Component ret) {
		if(color != null) {
			switch(color) {
			case BLACK:
				ret = ret.color(NamedTextColor.BLACK);
				break;
			case DARK_BLUE:
				ret = ret.color(NamedTextColor.DARK_BLUE);
				break;
			case DARK_GREEN:
				ret = ret.color(NamedTextColor.DARK_GREEN);
				break;
			case DARK_AQUA:
				ret = ret.color(NamedTextColor.DARK_AQUA);
				break;
			case DARK_RED:
				ret = ret.color(NamedTextColor.DARK_RED);
				break;
			case DARK_PURPLE:
				ret = ret.color(NamedTextColor.DARK_PURPLE);
				break;
			case GOLD:
				ret = ret.color(NamedTextColor.GOLD);
				break;
			case GRAY:
				ret = ret.color(NamedTextColor.GRAY);
				break;
			case DARK_GRAY:
				ret = ret.color(NamedTextColor.DARK_GRAY);
				break;
			case BLUE:
				ret = ret.color(NamedTextColor.BLUE);
				break;
			case GREEN:
				ret = ret.color(NamedTextColor.GREEN);
				break;
			case AQUA:
				ret = ret.color(NamedTextColor.AQUA);
				break;
			case RED:
				ret = ret.color(NamedTextColor.RED);
				break;
			case LIGHT_PURPLE:
				ret = ret.color(NamedTextColor.LIGHT_PURPLE);
				break;
			case YELLOW:
				ret = ret.color(NamedTextColor.YELLOW);
				break;
			case WHITE:
				ret = ret.color(NamedTextColor.WHITE);
				break;
			}
		}
		if(bold) {
			ret = ret.decorate(TextDecoration.BOLD);
		}
		if(italic) {
			ret = ret.decorate(TextDecoration.ITALIC);
		}
		if(strikethrough) {
			ret = ret.decorate(TextDecoration.STRIKETHROUGH);
		}
		if(underline) {
			ret = ret.decorate(TextDecoration.UNDERLINED);
		}
		if(obfuscated) {
			ret = ret.decorate(TextDecoration.OBFUSCATED);
		}
		return ret;
	}

}
