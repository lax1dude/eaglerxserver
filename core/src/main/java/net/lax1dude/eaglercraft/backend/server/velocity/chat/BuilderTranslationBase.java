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

package net.lax1dude.eaglercraft.backend.server.velocity.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderClickEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslationArgs;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderHoverEvent;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderStyle;

abstract class BuilderTranslationBase<ParentType> implements IBuilderComponentTranslation<ParentType>, IAppendCallback {

	BuilderStyle<IBuilderComponentTranslation<ParentType>> style;
	BuilderClick<IBuilderComponentTranslation<ParentType>> click;
	BuilderHover<IBuilderComponentTranslation<ParentType>> hover;
	String translation;
	String insertion;
	List<Component> buildChildren;
	List<ComponentLike> args;

	@Override
	public IBuilderStyle<IBuilderComponentTranslation<ParentType>> beginStyle() {
		return style = new BuilderStyle<>(this);
	}

	@Override
	public IBuilderClickEvent<IBuilderComponentTranslation<ParentType>> beginClickEvent() {
		return click = new BuilderClick<>(this);
	}

	@Override
	public IBuilderHoverEvent<IBuilderComponentTranslation<ParentType>> beginHoverEvent() {
		return hover = new BuilderHover<>(this);
	}

	@Override
	public IBuilderComponentText<IBuilderComponentTranslation<ParentType>> appendTextComponent() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderComponentTranslation<ParentType>> appendTranslationComponent() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<ParentType> translation(String key) {
		this.translation = key;
		return this;
	}

	@Override
	public IBuilderComponentTranslation<ParentType> stringArgs(Object... args) {
		this.args = new ArrayList<>(args.length);
		for(int i = 0; i < args.length; ++i) {
			this.args.add(Component.text(Objects.toString(args[i])));
		}
		return this;
	}

	@Override
	public IBuilderComponentTranslation<ParentType> stringArgs(List<Object> args) {
		int size = args.size();
		this.args = new ArrayList<>(size);
		for(int i = 0; i < size; ++i) {
			this.args.add(Component.text(Objects.toString(args.get(i))));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IBuilderComponentTranslation<ParentType> componentArgs(Object... args) {
		this.args = (List<ComponentLike>) (Object) Arrays.asList(args);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IBuilderComponentTranslation<ParentType> componentArgs(List<Object> args) {
		this.args = (List<ComponentLike>) (Object) args;
		return this;
	}

	@Override
	public IBuilderComponentTranslationArgs<IBuilderComponentTranslation<ParentType>> args() {
		return new BuilderTranslationArgs<>(this);
	}

	@Override
	public IBuilderComponentTranslation<ParentType> insertion(String txt) {
		this.insertion = txt;
		return this;
	}

	@Override
	public void append(Component comp) {
		if(buildChildren == null) {
			buildChildren = new ArrayList<>(4);
		}
		buildChildren.add(comp);
	}

	protected Component build() {
		if(translation == null) {
			throw new IllegalStateException("No translation key specified");
		}
		Component ret;
		if(args != null) {
			ret = Component.translatable(translation, args);
		}else {
			ret = Component.translatable(translation);
		}
		if(insertion != null) {
			ret = ret.insertion(insertion);
		}
		if(style != null) {
			ret = style.applyTo(ret);
		}
		if(click != null) {
			ret = click.applyTo(ret);
		}
		if(hover != null) {
			ret = hover.applyTo(ret);
		}
		if(buildChildren != null) {
			ret = ret.children(buildChildren);
		}
		return ret;
	}

}
