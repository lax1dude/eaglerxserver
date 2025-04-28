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

package net.lax1dude.eaglercraft.backend.server.base.webserver;

import io.netty.handler.codec.http.FullHttpResponse;

public abstract class ResponseOrdering {

	public final class Slot {

		protected Slot prev;
		protected Slot next;

		protected Slot() {
			if (tail != null) {
				prev = tail;
				tail.next = this;
			}
		}

		protected FullHttpResponse data;
		protected boolean complete;

		public void complete(FullHttpResponse response) {
			if (complete) {
				response.release();
				return;
			}
			data = response;
			complete = true;
			_notify();
		}

		private void _notify() {
			if (complete) {
				if (prev == null || prev.complete) {
					try {
						if (data != null) {
							ResponseOrdering.this.send(data);
						}
					} finally {
						data = null;
						prev = null;
					}
					if (next != null) {
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
		while (s != null) {
			if (s.data != null) {
				s.data.release();
				s.data = null;
			}
			s = s.prev;
		}
		tail = null;
	}

}
