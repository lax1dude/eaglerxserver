package net.lax1dude.eaglercraft.backend.server.config.snakeyaml.legacy;

import java.util.ArrayList;
import java.util.List;

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
	private final boolean exists;
	private boolean initialized;

	public YAMLConfigList(YAMLConfigBase owner, SequenceNode yaml, boolean exists) {
		this.owner = owner;
		this.yaml = yaml;
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
	}

	@Override
	public IEaglerConfSection appendSection() {
		MappingNode obj = LegacyHelper.mappingNode(Tag.MAP, new ArrayList<>());
		yaml.getValue().add(obj);
		owner.modified = true;
		initialized = true;
		return new YAMLConfigSection(owner, obj, false);
	}

	@Override
	public IEaglerConfList appendList() {
		SequenceNode obj = LegacyHelper.sequenceNode(Tag.SEQ, new ArrayList<>());
		yaml.getValue().add(obj);
		owner.modified = true;
		initialized = true;
		return new YAMLConfigList(owner, obj, false);
	}

	@Override
	public void appendInteger(int value) {
		yaml.getValue().add(LegacyHelper.scalarNode(Tag.INT, Integer.toString(value), null));
		owner.modified = true;
		initialized = true;
	}

	@Override
	public void appendString(String string) {
		yaml.getValue().add(LegacyHelper.scalarNode(Tag.STR, string, '\''));
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
		if(t instanceof MappingNode tt) {
			return new YAMLConfigSection(owner, tt, true);
		}else {
			return null;
		}
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return null;
		Node t = lst.get(index);
		if(t instanceof SequenceNode tt) {
			return new YAMLConfigList(owner, tt, true);
		}else {
			return null;
		}
	}

	@Override
	public boolean isInteger(int index) {
		List<Node> lst = yaml.getValue();
		if(index < 0 || index >= lst.size()) return false;
		Node t = lst.get(index);
		if(t != null && (t instanceof ScalarNode tt)) {
			try {
				Double.parseDouble(tt.getValue());
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
		if(t != null && (t instanceof ScalarNode tt)) {
			try {
				return (int) Double.parseDouble(tt.getValue());
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
		if(t != null && (t instanceof ScalarNode tt)) {
			return tt.getValue();
		}
		return defaultVal;
	}

}
