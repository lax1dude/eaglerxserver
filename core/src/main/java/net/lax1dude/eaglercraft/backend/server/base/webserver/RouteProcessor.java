package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RouteProcessor extends RouteMap.Result<Object> implements Iterator<CharSequence> {

	private static final char SEPARATOR = '/';

	private final MutableSubSequence subsequence = new MutableSubSequence();

	private CharSequence url;
	private int index;
	private int nextIndex;
	private int nextIndexEnd;
	private int end;

	private static final Iterator<CharSequence> NOP_ITERATOR = new Iterator<CharSequence>() {
		@Override
		public boolean hasNext() {
			return false;
		}
		@Override
		public CharSequence next() {
			throw new NoSuchElementException();
		}
	};

	public <L, T> boolean register(CharSequence url, L listener, int method, RouteMap<L, T> routeMap, T value) {
		int len = url.length();
		if(len == 0) {
			return routeMap.register(NOP_ITERATOR, false, listener, method, value);
		}else if(len == 1 && url.charAt(0) == SEPARATOR) {
			return routeMap.register(NOP_ITERATOR, true, listener, method, value);
		}else {
			try {
				this.url = url;
				this.index = 0;
				this.nextIndex = -2;
				this.end = len;
				while(index < len && url.charAt(index) == SEPARATOR) {
					++index;
				}
				boolean dir = false;
				while(end > 0 && url.charAt(end - 1) == SEPARATOR) {
					dir = true;
					--end;
				}
				return routeMap.register(this, dir, listener, method, value);
			}finally {
				this.url = null;
			}
		}
	}

	public <L, T> boolean remove(CharSequence url, L listener, int method, RouteMap<L, T> routeMap, T value) {
		int len = url.length();
		if(len == 0) {
			return routeMap.remove(NOP_ITERATOR, false, listener, method, value);
		}else if(len == 1 && url.charAt(0) == SEPARATOR) {
			return routeMap.remove(NOP_ITERATOR, true, listener, method, value);
		}else {
			try {
				this.url = url;
				this.index = 0;
				this.nextIndex = -2;
				this.end = len;
				while(index < len && url.charAt(index) == SEPARATOR) {
					++index;
				}
				boolean dir = false;
				while(end > 0 && url.charAt(end - 1) == SEPARATOR) {
					dir = true;
					--end;
				}
				return routeMap.remove(this, dir, listener, method, value);
			}finally {
				this.url = null;
			}
		}
	}

	public <L, T> RouteMap.Result<T> find(CharSequence url, L listener, int method, RouteMap<L, T> routeMap) {
		this.result = null;
		RouteMap.Result<T> ret = (RouteMap.Result<T>) this;
		int len = url.length();
		if(len == 0) {
			routeMap.get(NOP_ITERATOR, false, listener, method, ret);
		}else if(len == 1 && url.charAt(0) == SEPARATOR) {
			routeMap.get(NOP_ITERATOR, true, listener, method, ret);
		}else {
			try {
				this.url = url;
				this.index = 0;
				this.nextIndex = -2;
				this.end = len;
				while(index < len && url.charAt(index) == SEPARATOR) {
					++index;
				}
				boolean dir = false;
				while(end > 0 && url.charAt(end - 1) == SEPARATOR) {
					dir = true;
					--end;
				}
				routeMap.get(this, dir, listener, method, ret);
			}finally {
				this.url = null;
			}
		}
		return ret;
	}

	private void findNext() {
		int i = indexOf(url, SEPARATOR, index, end);
		if(i == -1 && index < end) {
			i = end;
		}
		nextIndex = nextIndexEnd = i;
		if(nextIndex < 0) {
			return;
		}
		while((i = nextIndexEnd + 1) < end && url.charAt(i) == SEPARATOR) {
			nextIndexEnd = i;
		}
	}

	@Override
	public boolean hasNext() {
		if(url == null) {
			throw new UnsupportedOperationException();
		}
		if(nextIndex == -2) {
			findNext();
		}
		return nextIndex >= 0;
	}

	@Override
	public CharSequence next() {
		if(url == null) {
			throw new UnsupportedOperationException();
		}
		if(nextIndex == -2) {
			findNext();
		}
		if(nextIndex < 0) {
			throw new NoSuchElementException();
		}
		CharSequence ret = subsequence.set(url, index, nextIndex - index);
		nextIndex = -2;
		index = nextIndexEnd + 1;
		System.out.println(": " + ret);
		return ret;
	}

	private static int indexOf(CharSequence cs, char searchChar, int start, int end) {
		if (start < 0) {
			start = 0;
		}
		for (int i = start; i < end; i++) {
			if (cs.charAt(i) == searchChar) {
				return i;
			}
		}
		return -1;
	}

}
