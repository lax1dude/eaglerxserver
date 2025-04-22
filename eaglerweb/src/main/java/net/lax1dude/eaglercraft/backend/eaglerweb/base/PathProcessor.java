/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class PathProcessor implements Iterator<CharSequence> {

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

	private boolean adjustBounds() {
		while(index < end && url.charAt(index) == SEPARATOR) {
			++index;
		}
		boolean dir = false;
		while(end > 0 && url.charAt(end - 1) == SEPARATOR) {
			dir = true;
			--end;
		}
		return dir;
	}

	static class RedirectDirException extends Exception {
		final IndexNodeFolder autoIndex;
		RedirectDirException() {
			this.autoIndex = null;
		}
		RedirectDirException(IndexNodeFolder autoIndex) {
			this.autoIndex = autoIndex;
		}
	}

	ResponseCacheKey find(CharSequence url, List<String> index, boolean autoIndex, IndexNode base) throws RedirectDirException {
		int len = url.length();
		if(len == 0) {
			return get(NOP_ITERATOR, false, index, autoIndex, base);
		}else if(len == 1 && url.charAt(0) == SEPARATOR) {
			return get(NOP_ITERATOR, true, index, autoIndex, base);
		}else {
			try {
				this.url = url;
				this.index = 0;
				this.nextIndex = -2;
				this.end = len;
				return get(this, adjustBounds(), index, autoIndex, base);
			}finally {
				this.url = null;
			}
		}
	}

	private static ResponseCacheKey get(Iterator<CharSequence> itr, boolean dir, List<String> index, boolean autoIndex,
			IndexNode base) throws RedirectDirException {
		IndexNode ret = base;
		while(itr.hasNext()) {
			ret = ret.find(itr.next());
			if(ret == null) {
				break;
			}
		}
		if(ret == null) {
			return null;
		}
		if(ret.isDirectory() != dir) {
			throw new RedirectDirException();
		}
		ResponseCacheKey r = ret.getResponse(index);
		if(r == null && autoIndex && dir) {
			throw new RedirectDirException((IndexNodeFolder)ret);
		}
		return r;
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
