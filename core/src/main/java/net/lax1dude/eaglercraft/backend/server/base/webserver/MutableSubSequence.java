package net.lax1dude.eaglercraft.backend.server.base.webserver;

public class MutableSubSequence implements CharSequence {

	protected int hash;
	protected boolean hashIsZero;
	protected CharSequence data;
	protected int off;
	protected int len;

	public MutableSubSequence() {
	}

	public MutableSubSequence(CharSequence data, int off, int len) {
		set(data, off, len);
	}

	public MutableSubSequence set(CharSequence data, int off, int len) {
		this.hash = 0;
		this.hashIsZero = false;
		this.data = data;
		this.off = off;
		this.len = len;
		return this;
	}

	@Override
	public int length() {
		return len;
	}

	@Override
	public char charAt(int index) {
		if(index < 0 || index >= len) {
			throw new IndexOutOfBoundsException(index);
		}
		return data.charAt(index + off);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if(start < 0 || start >= len) {
			throw new IndexOutOfBoundsException(start);
		}
		if(end < start || end > len) {
			throw new IndexOutOfBoundsException(end);
		}
		return data.subSequence(start + off, end + off);
	}

	@Override
	public String toString() {
		return data.subSequence(off, off + len).toString();
	}

	@Override
	public int hashCode() {
		if(hash == 0 && !hashIsZero) {
			int h = 0;
			int l = len;
			for (int i = 0; i < l; i++) {
				h = 31 * h + data.charAt(off + i);
			}
			if(h == 0) {
				hashIsZero = true;
			}
			return hash = h;
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof CharSequence) && subEquals((CharSequence)obj));
	}

	private boolean subEquals(CharSequence obj) {
		int l = len;
		if(obj.length() != l) {
			return false;
		}
		for (int i = 0; i < l; i++) {
			if(data.charAt(off + i) != obj.charAt(i)) return false;
		}
		return true;
	}

}
