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

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumHoverAction;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderHoverEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

class BuilderHover<ParentType> implements IBuilderHoverEvent<ParentType>, IAppendCallback {

	private final ParentType parent;

	EnumHoverAction action;
	List<BaseComponent> buildChildren;

	BuilderHover(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		return parent;
	}

	@Override
	public IBuilderHoverEvent<ParentType> hoverAction(EnumHoverAction action) {
		this.action = action;
		return this;
	}

	@Override
	public IBuilderComponentText<IBuilderHoverEvent<ParentType>> appendTextContent() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderHoverEvent<ParentType>> appendTranslationContent() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public void append(BaseComponent comp) {
		if(buildChildren == null) {
			buildChildren = new ArrayList<>(4);
		}
		buildChildren.add(comp);
	}

	void applyTo(BaseComponent ret) {
		if(action == EnumHoverAction.SHOW_TEXT && buildChildren != null) {
			if(BungeeComponentHelper.LEGACY_FLAG_SUPPORT) {
				applyModern(ret, buildChildren);
			}else {
				applyLegacy(ret, buildChildren);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static void applyLegacy(BaseComponent ret, List<BaseComponent> lst) {
		ret.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, lst.toArray(new BaseComponent[lst.size()])));
	}

	private static void applyModern(BaseComponent ret, List<BaseComponent> lst) {
		int l = lst.size();
		Content[] clist = new Content[l];
		for(int i = 0; i < l; ++i) {
			clist[i] = new Text(lst.get(i));
		}
		ret.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, clist));
	}

}
