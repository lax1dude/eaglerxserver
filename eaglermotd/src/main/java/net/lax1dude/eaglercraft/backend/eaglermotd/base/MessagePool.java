package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessagePool {
	
	public final String poolName;
	public final List<MessagePoolEntry> messagePool = new ArrayList<>();
	
	public MessagePool(String s) {
		this.poolName = s;
	}
	
	public void sort() {
		Collections.sort(messagePool);
	}
	
	public MessagePoolEntry pickNew() {
		if(messagePool.size() <= 0) {
			return null;
		}
		float f = 0.0f;
		for(MessagePoolEntry m : messagePool) {
			f += m.weight;
		}
		f *= ThreadLocalRandom.current().nextFloat();
		float f2 = 0.0f;
		for(MessagePoolEntry m : messagePool) {
			f2 += m.weight;
			if(f2 >= f) {
				return m;
			}
		}
		return messagePool.get(0);
	}

	public MessagePoolEntry pickDefault() {
		for(MessagePoolEntry m : messagePool) {
			if("default".equalsIgnoreCase(m.name)) {
				return m;
			}
		}
		return pickNew();
	}

}
