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

package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.util.ArrayList;
import java.util.List;

import com.electronwill.nightconfig.core.CommentedConfig;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class NightConfigList implements IEaglerConfList {

	private final NightConfigBase owner;
	private final List<Object> data;
	private final IContext commentSetter;
	private final boolean exists;
	private boolean initialized;

	public interface IContext {
		void setComment(String comment);

		CommentedConfig genSection();
	}

	public NightConfigList(NightConfigBase owner, List<Object> list, IContext commentSetter, boolean exists) {
		this.owner = owner;
		this.data = list;
		this.commentSetter = commentSetter;
		this.exists = this.initialized = exists;
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	@Override
	public void setComment(String comment) {
		commentSetter.setComment(NightConfigLoader.createComment(comment));
		owner.modified = true;
		initialized = true;
	}

	@Override
	public IEaglerConfSection appendSection() {
		CommentedConfig conf = commentSetter.genSection();
		data.add(conf);
		owner.modified = true;
		initialized = true;
		return new NightConfigSection(owner, conf, null, false);
	}

	@Override
	public IEaglerConfList appendList() {
		List<Object> list = new ArrayList<>();
		data.add(list);
		owner.modified = true;
		initialized = true;
		return new NightConfigList(owner, list, new IContext() {
			@Override
			public void setComment(String comment) {
			}

			@Override
			public CommentedConfig genSection() {
				return commentSetter.genSection();
			}
		}, false);
	}

	@Override
	public void appendInteger(int value) {
		data.add(value);
		owner.modified = true;
		initialized = true;
	}

	@Override
	public void appendString(String string) {
		data.add(string);
		owner.modified = true;
		initialized = true;
	}

	@Override
	public int getLength() {
		return data.size();
	}

	@Override
	public IEaglerConfSection getIfSection(int index) {
		if (index < 0 || index >= data.size())
			return null;
		Object val = data.get(index);
		return (val instanceof CommentedConfig vall) ? new NightConfigSection(owner, vall, null, true) : null;
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		if (index < 0 || index >= data.size())
			return null;
		Object val = data.get(index);
		return (val instanceof List) ? new NightConfigList(owner, (List<Object>) val, new IContext() {
			@Override
			public void setComment(String comment) {
			}

			@Override
			public CommentedConfig genSection() {
				return commentSetter.genSection();
			}
		}, true) : null;
	}

	@Override
	public boolean isInteger(int index) {
		if (index < 0 || index >= data.size())
			return false;
		return (data.get(index) instanceof Number);
	}

	@Override
	public int getIfInteger(int index, int defaultVal) {
		if (index < 0 || index >= data.size())
			return defaultVal;
		Object val = data.get(index);
		return (val instanceof Number num) ? num.intValue() : defaultVal;
	}

	@Override
	public boolean isString(int index) {
		if (index < 0 || index >= data.size())
			return false;
		return (data.get(index) instanceof String);
	}

	@Override
	public String getIfString(int index, String defaultVal) {
		if (index < 0 || index >= data.size())
			return defaultVal;
		Object val = data.get(index);
		return (val instanceof String str) ? str : defaultVal;
	}

}
