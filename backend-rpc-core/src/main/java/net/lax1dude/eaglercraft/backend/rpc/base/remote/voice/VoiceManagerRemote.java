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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.EaglerXBackendRPCRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.WrongVCPacketException;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCCapable;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAllowed;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCCapable;

public class VoiceManagerRemote<PlayerObject> extends SerializationContext implements IVoiceManager<PlayerObject> {

	private static final VarHandle SERVER_ENABLE_HANDLE;
	private static final VarHandle LAST_STATE_HANDLE;
	private static final VarHandle ACTIVE_CHANNEL_HANDLE;
	private static final VarHandle CURRENT_VOICE_CHANNEL_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			SERVER_ENABLE_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "isServerEnable", int.class);
			LAST_STATE_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "lastVoiceState", EnumVoiceState.class);
			ACTIVE_CHANNEL_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "activeChannel",
					VoiceChannel.Context.class);
			CURRENT_VOICE_CHANNEL_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "currentVoiceChannel",
					IVoiceChannel.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	final VoiceChannel<PlayerObject>.Context aquireActiveChannel() {
		return (VoiceChannel<PlayerObject>.Context) ACTIVE_CHANNEL_HANDLE.getAcquire(this);
	}

	final VoiceChannel<PlayerObject>.Context xchgActiveChannel(VoiceChannel<PlayerObject>.Context newValue) {
		return (VoiceChannel<PlayerObject>.Context) ACTIVE_CHANNEL_HANDLE.getAndSet(this, newValue);
	}

	final PlayerInstanceRemote<PlayerObject> player;
	final VoiceServiceRemote<PlayerObject> voice;

	private boolean isAlive = true;
	private boolean isManaged = true;
	private volatile int isServerEnable = 0;
	private volatile EnumVoiceState lastVoiceState = EnumVoiceState.SERVER_DISABLE;
	private volatile VoiceChannel<PlayerObject>.Context activeChannel = null;
	private IVoiceChannel currentVoiceChannel = DisabledChannel.INSTANCE;
	private BackendVCProtocolHandler handler = null;
	private final Object initLock = new Object();

	VoiceManagerRemote(PlayerInstanceRemote<PlayerObject> player, VoiceServiceRemote<PlayerObject> voice) {
		super(player.serializationContext);
		this.player = player;
		this.voice = voice;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		return voice;
	}

	@Override
	protected IPlatformLogger logger() {
		return player.logger();
	}

	public boolean isVoiceCapable() {
		return (int) SERVER_ENABLE_HANDLE.getAcquire(this) != 0;
	}

	@Override
	public EnumVoiceState getVoiceState() {
		if ((IVoiceChannel) CURRENT_VOICE_CHANNEL_HANDLE.getOpaque(this) != DisabledChannel.INSTANCE) {
			VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
			if (ch != null) {
				return ch.isConnected() ? EnumVoiceState.ENABLED : EnumVoiceState.DISABLED;
			}
		}
		return EnumVoiceState.SERVER_DISABLE;
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		return (IVoiceChannel) CURRENT_VOICE_CHANNEL_HANDLE.getAcquire(this);
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		setVoiceChannel0(channel);
		onStateChanged();
	}

	private void setVoiceChannel0(IVoiceChannel channel) {
		if (channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
		}
		if (channel != DisabledChannel.INSTANCE && (!(channel instanceof VoiceChannel ch) || ch.owner != voice)) {
			throw new IllegalArgumentException("Unknown voice channel");
		}
		IVoiceChannel oldChannel;
		synchronized (this) {
			if (!isAlive) {
				return;
			}
			oldChannel = currentVoiceChannel;
			if (channel == oldChannel) {
				return;
			}
			CURRENT_VOICE_CHANNEL_HANDLE.setRelease(this, channel);
		}
		switchChannels(oldChannel, channel);
	}

	@Override
	public boolean isWorldManaged() {
		return isManaged;
	}

	@Override
	public void setWorldManaged(boolean managed) {
		isManaged = managed;
	}

	void handleWorldChanged(String worldName) {
		if (isManaged) {
			setVoiceChannel0(DisabledChannel.INSTANCE);
			setVoiceChannel0(voice.getWorldVoiceChannel(worldName));
			onStateChanged();
		}
	}

	public void destroyVoiceManager() {
		IVoiceChannel oldChannel;
		synchronized (this) {
			if (!isAlive) {
				return;
			}
			isAlive = false;
			oldChannel = currentVoiceChannel;
			if (DisabledChannel.INSTANCE == oldChannel) {
				return;
			}
			CURRENT_VOICE_CHANNEL_HANDLE.setRelease(this, DisabledChannel.INSTANCE);
		}
		((VoiceChannel<PlayerObject>) oldChannel).removeFromChannel(this, true);
	}

	private void switchChannels(IVoiceChannel oldChannel, IVoiceChannel newChannel) {
		if (newChannel != DisabledChannel.INSTANCE) {
			if (oldChannel == DisabledChannel.INSTANCE) {
				enableVoice();
			}
			((VoiceChannel<PlayerObject>) newChannel).addToChannel(this);
		} else {
			if (oldChannel != DisabledChannel.INSTANCE) {
				((VoiceChannel<PlayerObject>) oldChannel).removeFromChannel(this, true);
			}
			disableVoice();
		}
	}

	public void handleInboundVoiceMessage(byte[] packet) {
		EaglerVCPacket pkt;
		eagler: if ((int) SERVER_ENABLE_HANDLE.getAcquire(this) == 0) {
			synchronized (initLock) {
				if ((int) SERVER_ENABLE_HANDLE.getAcquire(this) != 0) {
					break eagler;
				}
				try {
					pkt = deserialize(EaglerVCProtocol.INIT, packet);
				} catch (Exception ex) {
					onException(ex);
					return;
				}
				handleInboundVoiceHandshake(pkt);
				return;
			}
		}
		try {
			pkt = deserialize(EaglerVCProtocol.V1, packet);
		} catch (Exception ex) {
			onException(ex);
			return;
		}
		try {
			pkt.handlePacket(handler);
		} catch (Exception ex) {
			onException(new IllegalStateException(
					"Failed to handle inbound voice RPC packet: " + pkt.getClass().getSimpleName(), ex));
		}
	}

	public void handleInboundVoiceHandshake(EaglerVCPacket packet) {
		if (packet instanceof CPacketVCCapable pkt) {
			eagler: {
				int[] vers = pkt.versions;
				for (int i = 0; i < vers.length; ++i) {
					if (vers[i] == 1) {
						break eagler;
					}
				}
				logger().error("Unsupported voice RPC handshake recieved!");
				return;
			}
			EaglerXBackendRPCRemote<PlayerObject> server = player.getEaglerXBackendRPC();
			VoiceServiceRemote<PlayerObject> service = (VoiceServiceRemote<PlayerObject>) server.getVoiceService();
			byte[] packetOut;
			try {
				packetOut = serialize(EaglerVCProtocol.INIT,
						new SPacketVCCapable(1, false, service.getOverrideICEServers(), service.getICEServersStr()));
			} catch (Exception ex) {
				onException(ex);
				return;
			}
			handler = new BackendV1VCProtocolHandler(this);
			SERVER_ENABLE_HANDLE.setRelease(this, 1);
			player.getPlatformPlayer().sendData(server.getChannelVoiceName(), packetOut);
			String worldName = player.getPlatformPlayer().getWorldName();
			IVoiceChannel setChannel = worldName != null ? voice.getWorldVoiceChannel(worldName)
					: voice.getGlobalVoiceChannel();
			player.getEaglerXBackendRPC().getPlatform().eventDispatcher().dispatchVoiceCapableEvent(player, setChannel,
					(res) -> {
						setVoiceChannel(res.getTargetChannel());
					});
		} else {
			throw new WrongVCPacketException();
		}
	}

	private void onException(Exception ex) {
		logger().error("Caught exception while processing voice RPC packets!", ex);
	}

	void writeOutboundVoicePacket(EaglerVCPacket packet) {
		byte[] pkt;
		try {
			pkt = serialize(EaglerVCProtocol.V1, packet);
		} catch (Exception ex) {
			onException(ex);
			return;
		}
		player.getPlatformPlayer().sendData(player.getEaglerXBackendRPC().getChannelVoiceName(), pkt);
	}

	private void enableVoice() {
		if ((int) SERVER_ENABLE_HANDLE.compareAndExchange(this, 1, 2) == 1) {
			writeOutboundVoicePacket(new SPacketVCAllowed(true));
		}
	}

	private void disableVoice() {
		if ((int) SERVER_ENABLE_HANDLE.compareAndExchange(this, 2, 1) == 2) {
			writeOutboundVoicePacket(new SPacketVCAllowed(false));
		}
	}

	void onStateChanged() {
		onStateChanged(getVoiceState());
	}

	void onStateChanged(EnumVoiceState newState) {
		EnumVoiceState oldState = (EnumVoiceState) LAST_STATE_HANDLE.getAndSet(this, newState);
		if (newState != oldState) {
			player.getEaglerXBackendRPC().getPlatform().eventDispatcher().dispatchVoiceChangeEvent(player, oldState,
					newState);
		}
	}

	void handlePlayerSignalPacketTypeConnect() {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if (ch != null) {
			ch.handleVoiceSignalPacketTypeConnect();
		}
	}

	void handlePlayerSignalPacketTypeRequest(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if (ch != null) {
			ch.handleVoiceSignalPacketTypeRequest(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	void handlePlayerSignalPacketTypeICE(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if (ch != null) {
			ch.handleVoiceSignalPacketTypeICE(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	void handlePlayerSignalPacketTypeDesc(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if (ch != null) {
			ch.handleVoiceSignalPacketTypeDesc(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	void handlePlayerSignalPacketTypeDisconnectPeer(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if (ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnectPeer(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	void handlePlayerSignalPacketTypeDisconnect() {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if (ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnect();
		}
	}

}
