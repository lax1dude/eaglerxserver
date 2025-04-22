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

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumClickAction;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderClickEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

class BuilderClick<ParentType> implements IBuilderClickEvent<ParentType> {

	private final ParentType parent;

	EnumClickAction action;
	String value;

	BuilderClick(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		return parent;
	}

	@Override
	public IBuilderClickEvent<ParentType> clickAction(EnumClickAction action) {
		this.action = action;
		return this;
	}

	@Override
	public IBuilderClickEvent<ParentType> clickValue(String value) {
		this.value = value;
		return this;
	}

	void applyTo(BaseComponent ret) {
		if(action != null) {
			switch(action) {
			case OPEN_URL:
				ret.setClickEvent(new ClickEvent(Action.OPEN_URL, value));
				break;
			case OPEN_FILE:
				ret.setClickEvent(new ClickEvent(Action.OPEN_FILE, value));
				break;
			case RUN_COMMAND:
				ret.setClickEvent(new ClickEvent(Action.RUN_COMMAND, value));
				break;
			case SUGGEST_COMMAND:
				ret.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, value));
				break;
			case CHANGE_PAGE:
				ret.setClickEvent(new ClickEvent(Action.CHANGE_PAGE, value));
				break;
			case COPY_TO_CLIPBOARD:
				if(BungeeComponentHelper.CLICK_ACTION_COPY_TO_CLIPBOARD != null) {
					ret.setClickEvent(new ClickEvent(BungeeComponentHelper.CLICK_ACTION_COPY_TO_CLIPBOARD, value));
				}
				break;
			}
		}
	}

}
