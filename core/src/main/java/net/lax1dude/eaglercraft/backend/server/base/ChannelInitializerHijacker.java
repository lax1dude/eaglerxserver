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

import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class ChannelInitializerHijacker extends ChannelInitializer<Channel> {

	private class ImplInitial implements Consumer<Channel> {

		@Override
		public void accept(Channel channel) {
			Consumer<Channel> run;
			eagler: {
				synchronized (ChannelInitializerHijacker.this) {
					run = impl;
					if (run != this) {
						break eagler;
					}
					if (reInject()) {
						impl = ChannelInitializerHijacker.this::callParent;
						run = (c) -> c.close(channel.voidPromise());
						break eagler;
					}
					run = impl = ChannelInitializerHijacker.this::callParentAndInit;
				}
				run.accept(channel);
				return;
			}
			if (run != null) {
				run.accept(channel);
			}
		}

	}

	protected final Consumer<Channel> initServerChild;

	public ChannelInitializerHijacker(Consumer<Channel> initServerChild) {
		this.initServerChild = initServerChild;
	}

	protected Consumer<Channel> impl = new ImplInitial();

	protected abstract void callParent(Channel channel);

	protected void callParentAndInit(Channel channel) {
		callParent(channel);
		initServerChild.accept(channel);
	}

	protected abstract boolean reInject();

	@Override
	protected void initChannel(Channel var1) throws Exception {
		impl.accept(var1);
	}

	public void deactivate() {
		impl = ChannelInitializerHijacker.this::callParent;
	}

}
