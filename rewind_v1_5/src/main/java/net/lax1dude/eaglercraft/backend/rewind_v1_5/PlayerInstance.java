package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;

public class PlayerInstance<PlayerObject> {

	private final RewindPluginProtocol<PlayerObject> rewind;
	private IEaglerPlayer<PlayerObject> eaglerPlayer;

	private INativeZlib nativeZlib;
	private IRewindLogger logger;

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
