package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.comments.CommentLine;
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
		if(commentSetter != null) {
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
		if(index < 0 || index >= lst.size()) return null;
		Node t = lst.get(index);
		if(t instanceof MappingNode) {
			return new YAMLConfigSection(owner, (MappingNode) t, null, true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return null;
		Node t = lst.get(index);
		if(t instanceof SequenceNode) {
			return new YAMLConfigList(owner, (SequenceNode) t, null, true);
		}else {
			return null;
		}
	}

	@Override
	public boolean isInteger(int index) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return false;
		Node t = lst.get(index);
		if(t != null && (t instanceof ScalarNode)) {
			try {
				Double.parseDouble(((ScalarNode) t).getValue());
			}catch(NumberFormatException ex) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int getIfInteger(int index, int defaultVal) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return defaultVal;
		Node t = lst.get(index);
		if(t != null && (t instanceof ScalarNode)) {
			try {
				return (int) Double.parseDouble(((ScalarNode) t).getValue());
			}catch(NumberFormatException ex) {
			}
		}
		return defaultVal;
	}

	@Override
	public boolean isString(int index) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return false;
		Node t = lst.get(index);
		if(t != null && (t instanceof ScalarNode)) {
			return true;
		}
		return false;
	}

	@Override
	public String getIfString(int index, String defaultVal) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return defaultVal;
		Node t = lst.get(index);
		if(t != null && (t instanceof ScalarNode)) {
			return ((ScalarNode) t).getValue();
		}
		return defaultVal;
	}

}
