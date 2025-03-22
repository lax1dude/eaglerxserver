package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindInitializer;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IPacket2ClientProtocol;

public class RewindPluginProtocol<PlayerObject> implements IEaglerXRewindProtocol<PlayerObject, PlayerInstance<PlayerObject>> {

	private final IRewindPlatform<PlayerObject> platform;

	private IEaglerXServerAPI<PlayerObject> server;

	public RewindPluginProtocol(IRewindPlatform<PlayerObject> platform) {
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
		logger().info("EaglerXRewind protocol for Eaglercraft 1.5.2 has been registered");
		this.server = server;
	}

	@Override
	public void handleUnregistered(IEaglerXServerAPI<PlayerObject> server) {
		// On shutdown
		logger().info("EaglerXRewind protocol for Eaglercraft 1.5.2 has been unregistered");
	}

	@Override
	public void initializeConnection(int legacyProtocol, IEaglerXRewindInitializer<PlayerInstance<PlayerObject>> initializer) {
		IEaglerConnection eaglerConnection = initializer.getConnection();
		IPacket2ClientProtocol legacyHandshake = initializer.getLegacyHandshake();
		IMessageController messageController = initializer.createMessageController();
		IOutboundInjector outboundInjector = initializer.createOutboundInjector();
		
		String realAddr = eaglerConnection.getRealAddress();
		if(realAddr == null) {
			realAddr = eaglerConnection.getSocketAddress().toString();
		}
		
		PlayerInstance<PlayerObject> attachment = new PlayerInstance<>(this, messageController, outboundInjector,
				initializer.netty().getChannel(), realAddr + "|" + legacyHandshake.getUsername());
		initializer.setAttachment(attachment);
		
		messageController.setOutboundHandler(new RewindMessageHandler(attachment));
		
		initializer.netty().injectNettyHandlers((new RewindChannelHandler<PlayerObject>(attachment))
				.setCodec(new RewindHandshakeCodec<>(legacyHandshake)));
		
		initializer.rewriteInitialHandshakeV2(5, 47, "EaglerXRewind", "1.5.2", false,
				legacyHandshake.getUsername().getBytes(StandardCharsets.US_ASCII));
	}

	@Override
	public void handleCreatePlayer(PlayerInstance<PlayerObject> attachment, IEaglerPlayer<PlayerObject> playerObj) {
		attachment.handlePlayerCreate(playerObj);
	}

	@Override
	public void handleDestroyPlayer(PlayerInstance<PlayerObject> attachment) {
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
