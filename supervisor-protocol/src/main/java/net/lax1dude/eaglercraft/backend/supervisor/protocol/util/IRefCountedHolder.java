package net.lax1dude.eaglercraft.backend.supervisor.protocol.util;

import io.netty.util.ReferenceCounted;

public interface IRefCountedHolder extends ReferenceCounted {

	ReferenceCounted delegate();

	default int refCnt() {
		ReferenceCounted d = delegate();
		return d != null ? d.refCnt() : 0;
	}

	default ReferenceCounted retain() {
		ReferenceCounted d = delegate();
		if(d != null) {
			d.retain();
		}
		return this;
	}

	default ReferenceCounted retain(int increment) {
		ReferenceCounted d = delegate();
		if(d != null) {
			d.retain(increment);
		}
		return this;
	}

	default ReferenceCounted touch() {
		ReferenceCounted d = delegate();
		if(d != null) {
			d.touch();
		}
		return this;
	}

	default ReferenceCounted touch(Object hint) {
		ReferenceCounted d = delegate();
		if(d != null) {
			d.touch(hint);
		}
		return this;
	}

	default boolean release() {
		ReferenceCounted d = delegate();
		return d != null && d.release();
	}

	default boolean release(int decrement) {
		ReferenceCounted d = delegate();
		return d != null && d.release(decrement);
	}

}
