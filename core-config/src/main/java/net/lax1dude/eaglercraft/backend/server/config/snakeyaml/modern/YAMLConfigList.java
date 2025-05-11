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

package net.lax1dude.eaglercraft.backend.server.config.snakeyaml.modern;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class YAMLConfigList implements IEaglerConfList {

	private final YAMLConfigBase owner;
	final SequenceNode yaml;
	private final Consumer<String> commentSetter;
	private final boolean exists;
	private boolean initialized;

	public YAMLConfigList(YAMLConfigBase owner, SequenceNode yaml, Consumer<String> commentSetter, boolean exists) {
		this.owner = owner;
		this.yaml = yaml;
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
		if (commentSetter != null) {
			commentSetter.accept(comment);
		}
	}

	@Override
	public IEaglerConfSection appendSection() {
		MappingNode obj = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.BLOCK);
		yaml.getValue().add(obj);
		owner.modified = true;
		initialized = true;
		return new YAMLConfigSection(owner, obj, null, false);
	}

	@Override
	public IEaglerConfList appendList() {
		SequenceNode obj = new SequenceNode(Tag.SEQ, new ArrayList<>(), FlowStyle.BLOCK);
		yaml.getValue().add(obj);
		owner.modified = true;
		initialized = true;
		return new YAMLConfigList(owner, obj, null, false);
	}

	@Override
	public void appendInteger(int value) {
		yaml.getValue().add(new ScalarNode(Tag.INT, Integer.toString(value), null, null, ScalarStyle.PLAIN));
		owner.modified = true;
		initialized = true;
	}

	@Override
	public void appendString(String string) {
		yaml.getValue().add(new ScalarNode(Tag.STR, string, null, null, ScalarStyle.SINGLE_QUOTED));
		owner.modified = true;
		initialized = true;
	}

	@Override
	public int getLength() {
		return yaml.getValue().size();
	}

	@Override
	public IEaglerConfSection getIfSection(int index) {
		List<Node> lst = yaml.getValue();
		if (index < 0 || index >= lst.size())
			return null;
		Node t = lst.get(index);
		if (t instanceof MappingNode tt) {
			return new YAMLConfigSection(owner, tt, null, true);
		} else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		List<Node> lst = yaml.getValue();
		if (index < 0 || index >= lst.size())
			return null;
		Node t = lst.get(index);
		if (t instanceof SequenceNode tt) {
			return new YAMLConfigList(owner, tt, null, true);
		} else {
			return null;
		}
	}

	@Override
	public boolean isInteger(int index) {
		List<Node> lst = yaml.getValue();
		if (index < 0 || index >= lst.size())
			return false;
		Node t = lst.get(index);
		if (t != null && (t instanceof ScalarNode tt)) {
			try {
				Double.parseDouble(tt.getValue());
			} catch (NumberFormatException ex) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int getIfInteger(int index, int defaultVal) {
		List<Node> lst = yaml.getValue();
		if (index < 0 || index >= lst.size())
			return defaultVal;
		Node t = lst.get(index);
		if (t != null && (t instanceof ScalarNode tt)) {
			try {
				return (int) Double.parseDouble(tt.getValue());
			} catch (NumberFormatException ex) {
			}
		}
		return defaultVal;
	}

	@Override
	public boolean isString(int index) {
		List<Node> lst = yaml.getValue();
		if (index < 0 || index >= lst.size())
			return false;
		Node t = lst.get(index);
		if (t != null && (t instanceof ScalarNode)) {
			return true;
		}
		return false;
	}

	@Override
	public String getIfString(int index, String defaultVal) {
		List<Node> lst = yaml.getValue();
		if (index < 0 || index >= lst.size())
			return defaultVal;
		Node t = lst.get(index);
		if (t != null && (t instanceof ScalarNode tt)) {
			return tt.getValue();
		}
		return defaultVal;
	}

}
