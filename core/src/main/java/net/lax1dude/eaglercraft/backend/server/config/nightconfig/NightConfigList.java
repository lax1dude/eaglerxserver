package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.util.List;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public class NightConfigList implements IEaglerConfList {

	private final List<Object> data;
	private final Consumer<String> commentSetter;
	private final boolean exists;
	private boolean initialized;

	public NightConfigList(List<Object> list, Consumer<String> commentSetter, boolean exists) {
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
		if(commentSetter != null) {
			commentSetter.accept(comment);
		}
	}

	@Override
	public IEaglerConfSection appendSection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerConfList appendList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendInteger(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendString(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IEaglerConfSection getIfSection(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInteger(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getIfInteger(int index, int defaultVal) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isString(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getIfString(int index, String defaultVal) {
		// TODO Auto-generated method stub
		return null;
	}

}
