package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindInitializer;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
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

	@Override
	public void handleRegistered(IEaglerXServerAPI<PlayerObject> server) {
		// On startup
		this.server = server;
	}

	@Override
	public void handleUnregistered(IEaglerXServerAPI<PlayerObject> server) {
		// On shutdown
	}

	@Override
	public void initializeConnection(int legacyProtocol, IEaglerXRewindInitializer<PlayerInstance<PlayerObject>> initializer) {
		PlayerInstance<PlayerObject> attachment = new PlayerInstance<>(this);
		IPacket2ClientProtocol legacyHandshake = initializer.getLegacyHandshake();
		initializer.setAttachment(attachment);
		initializer.netty().injectNettyHandlers(new RewindPacketEncoder<PlayerObject>(attachment),
				new RewindPacketDecoder<PlayerObject>(attachment));
		initializer.rewriteInitialHandshakeV2(3, 47, "EaglerXRewind", "1.5.2", false,
				legacyHandshake.getUsername().getBytes(StandardCharsets.US_ASCII));
	}

	@Override
	public void handleCreatePlayer(PlayerInstance<PlayerObject> attachment, IEaglerPlayer<PlayerObject> playerObj) {
		attachment.handleCreate(playerObj);
	}

	@Override
	public void handleDestroyPlayer(PlayerInstance<PlayerObject> attachment) {
		attachment.handleDestroy();
	}

	@Override
	public int[] getLegacyProtocols() {
		return new int[] { 69 };
	}

	@Override
	public int getEmulatedEaglerHandshake() {
		return 3;
	}

}
