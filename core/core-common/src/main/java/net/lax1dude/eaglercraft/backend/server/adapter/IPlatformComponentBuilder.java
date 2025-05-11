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

package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.List;

public interface IPlatformComponentBuilder {

	public static enum EnumChatColor {
		BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED,
		LIGHT_PURPLE, YELLOW, WHITE
	}

	public static enum EnumClickAction {
		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD
	}

	public static enum EnumHoverAction {
		SHOW_TEXT
	}

	public interface IBuilderBase<ParentType> {

		ParentType end();

	}

	public interface IBuilderComponent<ParentType, ThisType> extends IBuilderBase<ParentType> {

		IBuilderStyle<ThisType> beginStyle();

		IBuilderClickEvent<ThisType> beginClickEvent();

		IBuilderHoverEvent<ThisType> beginHoverEvent();

		IBuilderComponentText<ThisType> appendTextComponent();

		IBuilderComponentTranslation<ThisType> appendTranslationComponent();

		ThisType insertion(String txt);

	}

	public interface IBuilderComponentText<ParentType>
			extends IBuilderComponent<ParentType, IBuilderComponentText<ParentType>> {

		IBuilderComponentText<ParentType> text(String txt);

	}

	public interface IBuilderComponentTranslation<ParentType>
			extends IBuilderComponent<ParentType, IBuilderComponentTranslation<ParentType>> {

		IBuilderComponentTranslation<ParentType> translation(String key);

		IBuilderComponentTranslation<ParentType> stringArgs(Object... args);

		IBuilderComponentTranslation<ParentType> stringArgs(List<Object> args);

		IBuilderComponentTranslation<ParentType> componentArgs(Object... args);

		IBuilderComponentTranslation<ParentType> componentArgs(List<Object> args);

		IBuilderComponentTranslationArgs<IBuilderComponentTranslation<ParentType>> args();

	}

	public interface IBuilderComponentTranslationArgs<ParentType> extends IBuilderBase<ParentType> {

		IBuilderComponentText<IBuilderComponentTranslationArgs<ParentType>> textArg();

		IBuilderComponentTranslation<IBuilderComponentTranslationArgs<ParentType>> translateArg();

		IBuilderComponentTranslationArgs<ParentType> rawArg(Object component);

	}

	public interface IBuilderStyle<ParentType> extends IBuilderBase<ParentType> {

		IBuilderStyle<ParentType> color(EnumChatColor color);

		IBuilderStyle<ParentType> bold(boolean bold);

		IBuilderStyle<ParentType> italic(boolean italic);

		IBuilderStyle<ParentType> strikethrough(boolean strikethrough);

		IBuilderStyle<ParentType> underline(boolean underline);

		IBuilderStyle<ParentType> obfuscated(boolean obfuscated);

	}

	public interface IBuilderClickEvent<ParentType> extends IBuilderBase<ParentType> {

		IBuilderClickEvent<ParentType> clickAction(EnumClickAction action);

		IBuilderClickEvent<ParentType> clickValue(String value);

	}

	public interface IBuilderHoverEvent<ParentType> extends IBuilderBase<ParentType> {

		IBuilderHoverEvent<ParentType> hoverAction(EnumHoverAction action);

		IBuilderComponentText<IBuilderHoverEvent<ParentType>> appendTextContent();

		IBuilderComponentTranslation<IBuilderHoverEvent<ParentType>> appendTranslationContent();

	}

	IBuilderComponentText<Object> buildTextComponent();

	IBuilderComponentTranslation<Object> buildTranslationComponent();

}
