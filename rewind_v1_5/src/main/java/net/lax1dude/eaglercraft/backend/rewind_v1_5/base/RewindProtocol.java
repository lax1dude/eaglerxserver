/*
 * Copyright (c) 2025 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rewind_v1_5.base;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec.RewindChannelHandler;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec.RewindHandshakeCodec;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec.RewindMessageHandler;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindInitializer;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IPacket2ClientProtocol;

public class RewindProtocol<PlayerObject> implements IEaglerXRewindProtocol<PlayerObject, RewindPlayer<PlayerObject>> {

	public static final UUID BRAND_EAGLERXREWIND_1_5_2 = UUID.fromString("65f7ac16-3354-4dfa-bd07-624922fd7962");

	private final IRewindPlatform<PlayerObject> platform;

	private IEaglerXServerAPI<PlayerObject> server;

	public RewindProtocol(IRewindPlatform<PlayerObject> platform) {
		this.platform = platform;
	}

	public IRewindPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	public IRewindLogger logger() {
		return platform.logger();
	}

	@Override
	public void handleRegistered(IEaglerXServerAPI<PlayerObject> server) {
		// On startup
		this.server = server;
		server.getBrandService().registerBrand(BRAND_EAGLERXREWIND_1_5_2, "EaglerXRewind 1.5.2");
		logger().info("EaglerXRewind protocol for Eaglercraft 1.5.2 has been registered");
	}

	@Override
	public void handleUnregistered(IEaglerXServerAPI<PlayerObject> server) {
		// On shutdown
		server.getBrandService().unregisterBrand(BRAND_EAGLERXREWIND_1_5_2);
		logger().info("EaglerXRewind protocol for Eaglercraft 1.5.2 has been unregistered");
	}

	@Override
	public void initializeConnection(int legacyProtocol, IEaglerXRewindInitializer<RewindPlayer<PlayerObject>> initializer) {
		IEaglerConnection eaglerConnection = initializer.getConnection();
		IPacket2ClientProtocol legacyHandshake = initializer.getLegacyHandshake();
		IMessageController messageController = initializer.requestMessageController();
		IOutboundInjector outboundInjector = initializer.requestOutboundInjector();
		
		String realAddr = eaglerConnection.getRealAddress();
		if(realAddr == null) {
			realAddr = eaglerConnection.getSocketAddress().toString();
		}
		
		RewindPlayer<PlayerObject> attachment = new RewindPlayer<>(this, messageController, outboundInjector,
				initializer.netty().getChannel(), realAddr + "|" + legacyHandshake.getUsername());
		initializer.setAttachment(attachment);
		
		messageController.setOutboundHandler(new RewindMessageHandler(attachment));
		
		initializer.netty().injectNettyHandlers((new RewindChannelHandler<PlayerObject>(attachment))
				.setCodec(new RewindHandshakeCodec<>(legacyHandshake)));
		
		initializer.rewriteInitialHandshakeV2(5, 47, "EaglerXRewind", "1.5.2", false,
				legacyHandshake.getUsername().getBytes(StandardCharsets.US_ASCII));
	}

	@Override
	public void handleCreatePlayer(RewindPlayer<PlayerObject> attachment, IEaglerPlayer<PlayerObject> playerObj) {
		attachment.handlePlayerCreate(playerObj);
	}

	@Override
	public void handleDestroyPlayer(RewindPlayer<PlayerObject> attachment) {
		attachment.handlePlayerDestroy();
	}

	@Override
	public int[] getLegacyProtocols() {
		return new int[] { 69 };
	}

	@Override
	public int getEmulatedEaglerHandshake() {
		return 5;
	}

}
