package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

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
		for(NodeTuple t : yaml.getValue()) {
			Node key = t.getKeyNode();
			if(key instanceof ScalarNode) {
				accelerator.put(((ScalarNode)t.getKeyNode()).getValue(), t);
			}
		}
	}

	@Override
	public void setComment(String comment) {
		List<CommentLine> lst = yaml.getBlockComments();
		if(lst == null) {
			lst = new ArrayList<>();
			yaml.setBlockComments(lst);
		}else {
			lst.clear();
		}
		YAMLConfigLoader.createComment(comment, lst);
		owner.modified = true;
	}

	@Override
	public IEaglerConfSection getIfSection(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof MappingNode)) {
			return new YAMLConfigSection(owner, (MappingNode) t.getValueNode(), true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfSection getSection(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof MappingNode)) {
			return new YAMLConfigSection(owner, (MappingNode) t.getValueNode(), true);
		}else {
			MappingNode obj = new MappingNode(null, new ArrayList<>(), FlowStyle.BLOCK);
			t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), obj);
			accelerator.put(name, t);
			yaml.getValue().add(t);
			owner.modified = true;
			initialized = true;
			return new YAMLConfigSection(owner, obj, false);
		}
	}

	@Override
	public IEaglerConfList getIfList(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof SequenceNode)) {
			return new YAMLConfigList(owner, (SequenceNode) t.getValueNode(), true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getList(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof SequenceNode)) {
			return new YAMLConfigList(owner, (SequenceNode) t.getValueNode(), true);
		}else {
			SequenceNode obj = new SequenceNode(null, new ArrayList<>(), FlowStyle.BLOCK);
			t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), obj);
			accelerator.put(name, t);
			yaml.getValue().add(t);
			owner.modified = true;
			initialized = true;
			return new YAMLConfigList(owner, obj, false);
		}
	}

	@Override
	public List<String> getKeys() {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		for(NodeTuple t : yaml.getValue()) {
			Node key = t.getKeyNode();
			if(key instanceof ScalarNode) {
				builder.add(((ScalarNode)t.getKeyNode()).getValue());
			}
		}
		return builder.build();
	}

	@Override
	public boolean isBoolean(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			String str = ((ScalarNode)t.getValueNode()).getValue().toLowerCase();
			return "false".equals(str) || "true".equals(str);
		}else {
			return false;
		}
	}

	@Override
	public boolean getBoolean(String name) {
		NodeTuple t = accelerator.get(name);
		return t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)
				&& Boolean.valueOf(((ScalarNode) t.getValueNode()).getValue());
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			String str = ((ScalarNode) t.getValueNode()).getValue().toLowerCase();
			boolean b = false;
			if("false".equals(str) || (b = "true".equals(str))) {
				return b;
			}
		}
		ScalarNode node = new ScalarNode(null, Boolean.toString(defaultValue), null, null, ScalarStyle.PLAIN);
		if(comment != null) {
			List<CommentLine> lst = new ArrayList<>();
			YAMLConfigLoader.createComment(comment, lst);
			node.setBlockComments(lst);
		}
		t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return defaultValue;
	}

	@Override
	public boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			String str = ((ScalarNode) t.getValueNode()).getValue().toLowerCase();
			boolean b = false;
			if("false".equals(str) || (b = "true".equals(str))) {
				return b;
			}
		}
		boolean b = defaultValue.get();
		ScalarNode node = new ScalarNode(null, Boolean.toString(b), null, null, ScalarStyle.PLAIN);
		if(comment != null) {
			List<CommentLine> lst = new ArrayList<>();
			YAMLConfigLoader.createComment(comment, lst);
			node.setBlockComments(lst);
		}
		t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return b;
	}

	@Override
	public boolean isInteger(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			try {
				Double.parseDouble(((ScalarNode) t.getValueNode()).getValue());
			}catch(NumberFormatException ex) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int getInteger(String name, int defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			try {
				return (int) Double.parseDouble(((ScalarNode) t.getValueNode()).getValue());
			}catch(NumberFormatException ex) {
			}
		}
		ScalarNode node = new ScalarNode(null, Integer.toString(defaultValue), null, null, ScalarStyle.PLAIN);
		if(comment != null) {
			List<CommentLine> lst = new ArrayList<>();
			YAMLConfigLoader.createComment(comment, lst);
			node.setBlockComments(lst);
		}
		t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return defaultValue;
	}

	@Override
	public int getInteger(String name, Supplier<Integer> defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			try {
				return (int) Double.parseDouble(((ScalarNode) t.getValueNode()).getValue());
			}catch(NumberFormatException ex) {
			}
		}
		int i = defaultValue.get();
		ScalarNode node = new ScalarNode(null, Integer.toString(i), null, null, ScalarStyle.PLAIN);
		if(comment != null) {
			List<CommentLine> lst = new ArrayList<>();
			YAMLConfigLoader.createComment(comment, lst);
			node.setBlockComments(lst);
		}
		t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return i;
	}

	@Override
	public boolean isString(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			return true;
		}
		return false;
	}

	@Override
	public String getIfString(String name) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			return ((ScalarNode) t.getValueNode()).getValue();
		}
		return null;
	}

	@Override
	public String getString(String name, String defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			return ((ScalarNode) t.getValueNode()).getValue();
		}
		ScalarNode node = new ScalarNode(null, defaultValue, null, null, ScalarStyle.PLAIN);
		if(comment != null) {
			List<CommentLine> lst = new ArrayList<>();
			YAMLConfigLoader.createComment(comment, lst);
			node.setBlockComments(lst);
		}
		t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return defaultValue;
	}

	@Override
	public String getString(String name, Supplier<String> defaultValue, String comment) {
		NodeTuple t = accelerator.get(name);
		if(t != null && t.getValueNode() != null && (t.getValueNode() instanceof ScalarNode)) {
			return ((ScalarNode) t.getValueNode()).getValue();
		}
		String str = defaultValue.get();
		ScalarNode node = new ScalarNode(null, str, null, null, ScalarStyle.PLAIN);
		if(comment != null) {
			List<CommentLine> lst = new ArrayList<>();
			YAMLConfigLoader.createComment(comment, lst);
			node.setBlockComments(lst);
		}
		t = new NodeTuple(new ScalarNode(null, name, null, null, ScalarStyle.PLAIN), node);
		accelerator.put(name, t);
		yaml.getValue().add(t);
		owner.modified = true;
		initialized = true;
		return str;
	}

}
