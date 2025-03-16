package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import java.util.List;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.frame.IFrameUpdater;

public class MessagePoolEntry implements Comparable<MessagePoolEntry> {

	public final String name;
	public final int interval;
	public final int timeout;
	public final float weight;
	public final String next;
	public final List<IFrameUpdater> frames;
	
	public MessagePoolEntry(int interval, int timeout, float weight, String next, List<IFrameUpdater> frames, String name) {
		this.interval = interval;
		this.timeout = timeout;
		this.weight = weight;
		this.next = next;
		this.frames = frames;
		this.name = name;
	}

	@Override
	public int compareTo(MessagePoolEntry o) {
		return Float.compare(weight, o.weight);
	}

}
