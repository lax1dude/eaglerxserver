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
