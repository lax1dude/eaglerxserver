package net.lax1dude.eaglercraft.backend.server.base.webserver;

import io.netty.handler.codec.http.FullHttpResponse;

public abstract class ResponseOrdering {

	public final class Slot {

		protected Slot prev;
		protected Slot next;

		protected Slot() {
			if(tail != null) {
				prev = tail;
				tail.next = this;
			}
		}

		protected FullHttpResponse data;
		protected boolean complete;

		public void complete(FullHttpResponse response) {
			if(complete) {
				response.release();
				return;
			}
			data = response;
			complete = true;
			_notify();
		}

		private void _notify() {
			if(complete) {
				if(prev == null || prev.complete) {
					try {
						if(data != null) {
							ResponseOrdering.this.send(data);
						}
					}finally {
						data = null;
						prev = null;
					}
					if(next != null) {
						next._notify();
					}
				}
			}
		}

	}

	protected Slot tail;

	public Slot push() {
		return tail = new Slot();
	}

	protected abstract void send(FullHttpResponse data);

	public void release() {
		Slot s = tail;
		while(s != null) {
			if(s.data != null) {
				s.data.release();
				s.data = null;
			}
			s = s.prev;
		}
		tail = null;
	}

}
