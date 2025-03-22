package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.api.IComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;

public class PlayerInstance<PlayerObject> {

	private final RewindPluginProtocol<PlayerObject> rewind;
	private final IMessageController messageController;
	private final IOutboundInjector outboundInjector;
	private final Channel channel;
	private final IRewindLogger logger;
	private IEaglerPlayer<PlayerObject> eaglerPlayer;

	private INativeZlib nativeZlib;
	private INBTContext nbtContext;
	private IComponentHelper componentHelper;

	private double x = 0;
	private double y = 0;
	private double z = 0;
	private float yaw = 0;
	private float pitch = 0;

	private byte[] temp;

	public PlayerInstance(RewindPluginProtocol<PlayerObject> rewind, IMessageController messageController,
			IOutboundInjector outboundInjector, Channel channel, String logName) {
		this.rewind = rewind;
		this.messageController = messageController;
		this.outboundInjector = outboundInjector;
		this.channel = channel;
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

	public IMessageController getMessageController() {
		return messageController;
	}

	public IOutboundInjector getOutboundInjector() {
		return outboundInjector;
	}

	public Channel getChannel() {
		return channel;
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

	public IComponentHelper getComponentHelper() {
		if(this.componentHelper == null) {
			this.componentHelper = rewind.getServerAPI().getComponentHelper();
		}
		return this.componentHelper;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setLook(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
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
