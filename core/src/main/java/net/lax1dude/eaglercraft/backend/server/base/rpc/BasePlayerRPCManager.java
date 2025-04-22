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

package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCEnabled;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.SPacketRPCEnabledFailure;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public abstract class BasePlayerRPCManager<PlayerObject> {

	private static final VarHandle CONTEXT_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			CONTEXT_HANDLE = l.findVarHandle(BasePlayerRPCManager.class, "context", BasePlayerRPCContext.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	protected final BackendRPCService<PlayerObject> service;
	protected volatile BasePlayerRPCContext<PlayerObject> context;

	BasePlayerRPCManager(BackendRPCService<PlayerObject> service) {
		this.service = service;
	}

	public abstract BasePlayerInstance<PlayerObject> getPlayer();

	public abstract boolean isEaglerPlayer();

	public void sendRPCInitPacket(EaglerBackendRPCPacket packet) {
		byte[] data;
		try {
			data = service.handshakeCtx.serialize(packet);
		} catch (IOException e) {
			handleException(e);
			return;
		}
		getPlayer().getPlatformPlayer().sendDataBackend(service.getRPCChannel(), data);
	}

	public void sendRPCPacket(EaglerBackendRPCPacket packet) {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			byte[] data;
			try {
				data = ctx.serialize(packet);
			} catch (IOException e) {
				handleException(e);
				return;
			}
			getPlayer().getPlatformPlayer().sendDataBackend(service.getRPCChannel(), data);
		}
	}

	public void handleRPCPacketData(byte[] data) {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			EaglerBackendRPCPacket packet;
			try {
				packet = ctx.deserialize(data);
			} catch (IOException e) {
				handleException(e);
				return;
			}
			try {
				packet.handlePacket(ctx.packetHandler());
			} catch(Exception e) {
				handleException(e);
			}
		}else {
			EaglerBackendRPCPacket packet;
			try {
				packet = service.handshakeCtx.deserialize(data);
			} catch (IOException e) {
				handleException(e);
				return;
			}
			if(packet instanceof CPacketRPCEnabled pkt) {
				boolean V1 = false, V2 = false;
				for(int i : pkt.supportedProtocols) {
					if(i == 1) V1 = true;
					if(i == 2) V2 = true;
					if(V2) break;
				}
				if(V2) {
					handleEnabled(EaglerBackendRPCProtocol.V2);
				}else if(V1) {
					handleEnabled(EaglerBackendRPCProtocol.V1);
				}else {
					sendRPCInitPacket(new SPacketRPCEnabledFailure(SPacketRPCEnabledFailure.FAILURE_CODE_OUTDATED_SERVER));
				}
			}else {
				handleException(new IllegalStateException("Unexpected packet type for handshake: " + packet.getClass().getName()));
				sendRPCInitPacket(new SPacketRPCEnabledFailure(SPacketRPCEnabledFailure.FAILURE_CODE_INTERNAL_ERROR));
			}
		}
	}

	BasePlayerRPCContext<PlayerObject> context() {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			return ctx;
		}else {
			throw new IllegalStateException();
		}
	}

	void handleException(Exception ex) {
		getPlayer().getEaglerXServer().logger().error("Exception thrown while handling backend RPC packet for \"" + getPlayer().getUsername() + "\"!", ex);
	}

	protected abstract void handleEnabled(EaglerBackendRPCProtocol ver);

	protected void handleEnableContext(BasePlayerRPCContext<PlayerObject> ctx) {
		CONTEXT_HANDLE.setRelease(this, ctx);
	}

	void handleDisabled() {
		CONTEXT_HANDLE.setRelease(this, null);
	}

	public void handleServerPreConnect() {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			ctx.handleDisabled();
		}
	}

	public void handleServerPostConnect() {
		sendReadyMessage();
	}

	protected abstract void sendReadyMessage();

	public void fireWebViewOpenClose(boolean open, String channel) {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			ctx.fireWebViewOpenClose(open, channel);
		}
	}

	public void fireWebViewMessage(String channel, boolean binary, byte[] data) {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			ctx.fireWebViewMessage(channel, binary, data);
		}
	}

	public void fireToggleVoice(EnumVoiceState oldVoiceState, EnumVoiceState newVoiceState) {
		BasePlayerRPCContext<PlayerObject> ctx = (BasePlayerRPCContext<PlayerObject>) CONTEXT_HANDLE.getAcquire(this);
		if(ctx != null) {
			ctx.fireToggleVoice(oldVoiceState, newVoiceState);
		}
	}

}
