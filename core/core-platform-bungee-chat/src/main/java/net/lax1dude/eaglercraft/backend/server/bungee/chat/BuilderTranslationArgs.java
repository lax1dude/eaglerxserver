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

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentText;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslation;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.IBuilderComponentTranslationArgs;
import net.md_5.bungee.api.chat.BaseComponent;

class BuilderTranslationArgs<ParentType> implements IBuilderComponentTranslationArgs<ParentType>, IAppendCallback {

	private final ParentType parent;
	private List<Object> args;

	BuilderTranslationArgs(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public IBuilderComponentText<IBuilderComponentTranslationArgs<ParentType>> textArg() {
		return new BuilderTextChild<>(this);
	}

	@Override
	public IBuilderComponentTranslation<IBuilderComponentTranslationArgs<ParentType>> translateArg() {
		return new BuilderTranslationChild<>(this);
	}

	@Override
	public IBuilderComponentTranslationArgs<ParentType> rawArg(Object component) {
		append((BaseComponent) component);
		return this;
	}

	@Override
	public void append(BaseComponent comp) {
		if (args == null) {
			args = new ArrayList<>(4);
		}
		args.add(comp);
	}

	@Override
	public ParentType end() {
		if (args != null) {
			((BuilderTranslationBase<?>) parent).componentArgs(args);
		}
		return parent;
	}

}
