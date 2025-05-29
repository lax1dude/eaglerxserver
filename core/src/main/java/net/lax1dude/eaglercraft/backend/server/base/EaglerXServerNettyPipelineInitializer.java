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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerNettyPipelineInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformNettyPipelineInitializer;

class EaglerXServerNettyPipelineInitializer<PlayerObject>
		implements IEaglerXServerNettyPipelineInitializer<NettyPipelineData> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerNettyPipelineInitializer(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void initialize(IPlatformNettyPipelineInitializer<NettyPipelineData> initializer) {
		EaglerListener eagListener = (EaglerListener) initializer.getListener();
		if (server.getConfig().getSettings().isDebugLogNewChannels()) {
			server.logger().info("[" + eagListener.getName() + "]: New channel opened: " + initializer.getChannel());
		}
		Consumer<SocketAddress> realAddressHandle = null;
		CompoundRateLimiterMap.ICompoundRatelimits rateLimits = null;
		if (eagListener.isForwardIP()) {
			if (eagListener.getConfigData().isSpoofPlayerAddressForwarded()) {
				realAddressHandle = initializer.realAddressHandle();
			}
		} else {
			CompoundRateLimiterMap map = eagListener.getRateLimiter();
			if (map != null) {
				SocketAddress addr = initializer.getChannel().remoteAddress();
				if (addr instanceof InetSocketAddress inetAddr) {
					rateLimits = map.rateLimit(inetAddr.getAddress());
					if (rateLimits == null) {
						initializer.getChannel().close();
						return;
					}
				} else {
					server.logger().warn("Unable to ratelimit unknown address type: " + addr.getClass().getName()
							+ " - \"" + addr + "\"");
				}
			}
		}
		NettyPipelineData attachment = new NettyPipelineData(initializer.getChannel(), server, eagListener,
				server.getEaglerAttribManager().createEaglerHolder(), realAddressHandle, rateLimits);
		initializer.setAttachment(attachment);
		if (eagListener.isDualStack()) {
			server.getPipelineTransformer().injectDualStack(initializer.getPipeline(), initializer.getChannel(),
					attachment);
		} else {
			server.getPipelineTransformer().injectSingleStack(initializer.getPipeline(), initializer.getChannel(),
					attachment);
		}
	}

}
