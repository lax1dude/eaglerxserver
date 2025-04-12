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

package net.lax1dude.eaglercraft.backend.supervisor.server;

import java.util.ArrayList;
import java.util.Set;

import io.netty.channel.EventLoop;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCResultMulti;

abstract class RPCMultiResultAggregator extends ArrayList<SPacketSvRPCResultMulti.ResultEntry> {

	Set<RPCMultiResultAggregator> set;
	EventLoop eventLoop;
	private int cntDown;

	public RPCMultiResultAggregator(int cntDown) {
		super(cntDown);
	}

	public void push(SPacketSvRPCResultMulti.ResultEntry etr) {
		synchronized(this) {
			if(cntDown > 0) {
				add(etr);
				if(--cntDown > 0) {
					return;
				}
			}else {
				return;
			}
		}
		eventLoop.execute(() -> {
			if(set.remove(this)) {
				try {
					onComplete();
				}finally {
					destroy0();
				}
			}
		});
	}

	public void pushEmpty() {
		synchronized(this) {
			if(cntDown <= 0 || --cntDown != 0) {
				return;
			}
		}
		eventLoop.execute(() -> {
			if(set.remove(this)) {
				try {
					onComplete();
				}finally {
					destroy0();
				}
			}
		});
	}

	protected abstract void onComplete();

	public void destroy() {
		synchronized(this) {
			if(cntDown > 0) {
				cntDown = 0;
			}else {
				return;
			}
		}
		if(set.remove(this)) {
			destroy0();
		}
	}

	private void destroy0() {
		for(int i = 0, l = size(); i < l; ++i) {
			get(i).release();
		}
	}

}