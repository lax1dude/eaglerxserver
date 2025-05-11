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

package net.lax1dude.eaglercraft.backend.server.config.snakeyaml.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class YAMLConfigSection implements IEaglerConfSection {

	private final YAMLConfigBase owner;
	final MappingNode yaml;
	final Map<String, NodeTuple> accelerator = new HashMap<>();
	private final boolean exists;
	private boolean initialized;

	protected YAMLConfigSection(YAMLConfigBase owner, MappingNode yaml, boolean exists) {
		this.owner = owner;
		this.yaml = yaml;
		this.exists = this.initialized = exists;
		rehash();
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	public void rehash() {
		accelerator.clear();
		for (NodeTuple t : yaml.getValue()) {
			Node key = t.getKeyNode();
			if (key instanceof ScalarNode key2) {
				accelerator.put(key2.getValue(), t);
			}
		}
	}

	@Override
	public void setComment(String comment) {
	}

	@Override
	public IEaglerConfSection getIfSection(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof MappingNode value2)) {
			return new YAMLConfigSection(owner, value2, true);
		} else {
			return null;
		}
	}

	@Override
	public IEaglerConfSection getSection(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof MappingNode value2)) {
			return new YAMLConfigSection(owner, value2, true);
		} else {
			ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
			MappingNode obj = LegacyHelper.mappingNode(Tag.MAP, new ArrayList<>());
			NodeTuple tt = new NodeTuple(key, obj);
			accelerator.put(name, tt);
			yaml.getValue().add(tt);
			owner.modified = true;
			initialized = true;
			return new YAMLConfigSection(owner, obj, false);
		}
	}

	@Override
	public IEaglerConfList getIfList(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof SequenceNode value2)) {
			return new YAMLConfigList(owner, value2, true);
		} else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getList(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof SequenceNode value2)) {
			return new YAMLConfigList(owner, value2, true);
		} else {
			ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
			SequenceNode obj = LegacyHelper.sequenceNode(Tag.SEQ, new ArrayList<>());
			NodeTuple tt = new NodeTuple(key, obj);
			accelerator.put(name, tt);
			yaml.getValue().add(tt);
			owner.modified = true;
			initialized = true;
			return new YAMLConfigList(owner, obj, false);
		}
	}

	@Override
	public List<String> getKeys() {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		for (NodeTuple t : yaml.getValue()) {
			Node key = t.getKeyNode();
			if (key instanceof ScalarNode key2) {
				builder.add(key2.getValue());
			}
		}
		return builder.build();
	}

	@Override
	public boolean isBoolean(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			String str = value2.getValue().toLowerCase();
			return "false".equals(str) || "true".equals(str);
		} else {
			return false;
		}
	}

	@Override
	public boolean getBoolean(String name) {
		NodeTuple t = accelerator.get(name);
		return t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)
				&& Boolean.valueOf(value2.getValue());
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			String str = value2.getValue().toLowerCase();
			boolean b = false;
			if ("false".equals(str) || (b = "true".equals(str))) {
				return b;
			}
		}
		ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
		ScalarNode node = LegacyHelper.scalarNode(Tag.BOOL, Boolean.toString(defaultValue), null);
		t = new NodeTuple(key, node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return defaultValue;
	}

	@Override
	public boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			String str = value2.getValue().toLowerCase();
			boolean b = false;
			if ("false".equals(str) || (b = "true".equals(str))) {
				return b;
			}
		}
		boolean b = defaultValue.get();
		ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
		ScalarNode node = LegacyHelper.scalarNode(Tag.BOOL, Boolean.toString(b), null);
		t = new NodeTuple(key, node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return b;
	}

	@Override
	public boolean isInteger(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			try {
				Double.parseDouble(value2.getValue());
			} catch (NumberFormatException ex) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int getInteger(String name, int defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			try {
				return (int) Double.parseDouble(value2.getValue());
			} catch (NumberFormatException ex) {
			}
		}
		ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
		ScalarNode node = LegacyHelper.scalarNode(Tag.INT, Integer.toString(defaultValue), null);
		t = new NodeTuple(key, node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return defaultValue;
	}

	@Override
	public int getInteger(String name, Supplier<Integer> defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			try {
				return (int) Double.parseDouble(value2.getValue());
			} catch (NumberFormatException ex) {
			}
		}
		int i = defaultValue.get();
		ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
		ScalarNode node = LegacyHelper.scalarNode(Tag.INT, Integer.toString(i), null);
		t = new NodeTuple(key, node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return i;
	}

	@Override
	public boolean isString(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			return true;
		}
		return false;
	}

	@Override
	public String getIfString(String name) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			return value2.getValue();
		}
		return null;
	}

	@Override
	public String getString(String name, String defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			return value2.getValue();
		}
		ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
		ScalarNode node = LegacyHelper.scalarNode(Tag.STR, defaultValue, '\'');
		t = new NodeTuple(key, node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return defaultValue;
	}

	@Override
	public String getString(String name, Supplier<String> defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if (t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode value2)) {
			return value2.getValue();
		}
		String str = defaultValue.get();
		ScalarNode key = LegacyHelper.scalarNode(Tag.STR, name, null);
		ScalarNode node = LegacyHelper.scalarNode(Tag.STR, str, '\'');
		t = new NodeTuple(key, node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return str;
	}

}
