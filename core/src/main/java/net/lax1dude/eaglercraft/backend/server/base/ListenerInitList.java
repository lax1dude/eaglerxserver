package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;

public class ListenerInitList {

	private final Collection<IEaglerXServerListener> listeners;

	public ListenerInitList(Collection<IEaglerXServerListener> listeners) {
		this.listeners = Lists.newArrayList(listeners);
	}

	public synchronized IEaglerXServerListener offer(SocketAddress addr) {
		Iterator<IEaglerXServerListener> itr = listeners.iterator();
		while(itr.hasNext()) {
			IEaglerXServerListener i = itr.next();
			if(i.matchListenerAddress(addr)) {
				itr.remove();
				return i;
			}
		}
		return null;
	}

}
