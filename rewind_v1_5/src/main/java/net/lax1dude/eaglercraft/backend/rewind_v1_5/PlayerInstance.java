package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;

public class PlayerInstance<PlayerObject> {

	private final RewindPluginProtocol<PlayerObject> rewind;
	private final IRewindLogger logger;
	private IEaglerPlayer<PlayerObject> eaglerPlayer;

	private INativeZlib nativeZlib;
	private INBTContext nbtContext;

	private byte[] temp;

	public PlayerInstance(RewindPluginProtocol<PlayerObject> rewind, String logName) {
		this.rewind = rewind;
		this.logger = rewind.logger().createSubLogger(logName);
	}

	public RewindPluginProtocol<PlayerObject> getRewind() {
		return rewind;
	}

	public IRewindLogger logger() {
		return logger;
	}

	public IEaglerPlayer<PlayerObject> getPlayer() {
		return eaglerPlayer;
	}

	public INativeZlib getNativeZlib() {
		if(this.nativeZlib == null) {
			this.nativeZlib = rewind.getServerAPI().createNativeZlib(true, false, 0);
		}
		return this.nativeZlib;
	}

	public INBTContext getNBTContext() {
		if(this.nbtContext == null) {
			this.nbtContext = rewind.getServerAPI().getNBTHelper().createThreadContext(512);
		}
		return this.nbtContext;
	}

	public byte[] getTempBuffer() {
		if(this.temp == null) {
			this.temp = new byte[1024];
		}
		return this.temp;
	}

	public void handlePlayerCreate(IEaglerPlayer<PlayerObject> eaglerPlayer) {
		this.eaglerPlayer = eaglerPlayer;
	}

	public void handlePlayerDestroy() {

	}

	public void releaseNatives() {
		if(this.nativeZlib != null) {
			this.nativeZlib.release();
			this.nativeZlib = null;
		}
	}

}
