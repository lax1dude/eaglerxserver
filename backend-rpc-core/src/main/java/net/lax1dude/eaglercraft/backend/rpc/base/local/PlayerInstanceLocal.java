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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCHandle;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCAttributeHolder;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCConsumerFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCImmediateFuture;

public class PlayerInstanceLocal<PlayerObject> extends RPCAttributeHolder
		implements IEaglerPlayer<PlayerObject>, IRPCHandle<IBasePlayerRPC<PlayerObject>> {

	private static final VarHandle FUTURE_HANDLE;
	private static final VarHandle PLAYER_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			FUTURE_HANDLE = l.findVarHandle(PlayerInstanceLocal.class, "future", IRPCFuture.class);
			PLAYER_HANDLE = l.findVarHandle(PlayerInstanceLocal.class, "handle", BasePlayerRPCLocal.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	protected final EaglerXBackendRPCLocal<PlayerObject> server;
	protected final IPlatformPlayer<PlayerObject> player;
	protected final IPlatformSubLogger logger;

	private IRPCFuture<IBasePlayerRPC<PlayerObject>> future;
	private BasePlayerRPCLocal<PlayerObject> handle;

	public PlayerInstanceLocal(EaglerXBackendRPCLocal<PlayerObject> server, IPlatformPlayer<PlayerObject> player) {
		this.server = server;
		this.player = player;
		this.logger = server.logger().createSubLogger(player.getUsername());
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return server;
	}

	public EaglerXBackendRPCLocal<PlayerObject> getEaglerXBackendRPC() {
		return server;
	}

	public IPlatformSubLogger logger() {
		return logger;
	}

	@Override
	public PlayerObject getPlayerObject() {
		return player.getPlayerObject();
	}

	protected final BasePlayerRPCLocal<PlayerObject> handle() {
		return (BasePlayerRPCLocal<PlayerObject>) PLAYER_HANDLE.getAcquire(this);
	}

	@Override
	public boolean isRPCReady() {
		return handle() != null;
	}

	@Override
	public boolean isEaglerPlayer() {
		BasePlayerRPCLocal<PlayerObject> delegate = handle();
		return delegate != null && delegate.isEaglerPlayer();
	}

	@Override
	public IEaglerPlayer<PlayerObject> asEaglerPlayer() {
		BasePlayerRPCLocal<PlayerObject> delegate = handle();
		return delegate != null && delegate.isEaglerPlayer() ? this : null;
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public String getUsername() {
		return player.getUsername();
	}

	@Override
	public boolean isVoiceCapable() {
		BasePlayerRPCLocal<PlayerObject> delegate = handle();
		return delegate != null && delegate.isVoiceCapable();
	}

	@Override
	public IVoiceManager<PlayerObject> getVoiceManager() {
		BasePlayerRPCLocal<PlayerObject> delegate = handle();
		if (delegate != null) {
			return delegate.getVoiceManager();
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandle() {
		return (IRPCHandle) this;
	}

	@Override
	public IBasePlayerRPC<PlayerObject> getIfOpen() {
		return handle();
	}

	@Override
	public IRPCFuture<IBasePlayerRPC<PlayerObject>> openFuture() {
		IRPCFuture<IBasePlayerRPC<PlayerObject>> future = (IRPCFuture<IBasePlayerRPC<PlayerObject>>) FUTURE_HANDLE
				.getAcquire(this);
		if (future != null) {
			return future;
		} else {
			eag: synchronized (this) {
				future = this.future;
				if (future != null) {
					break eag;
				}
				BasePlayerRPCLocal<PlayerObject> existingHandle = this.handle;
				if (existingHandle != null) {
					FUTURE_HANDLE.setRelease(this,
							future = RPCImmediateFuture.create(server.schedulerExecutors(), existingHandle));
				} else {
					FUTURE_HANDLE.setRelease(this,
							future = new RPCConsumerFuture<IBasePlayerRPC<PlayerObject>, IBasePlayerRPC<PlayerObject>>(
									server.schedulerExecutors()) {
								@Override
								public void accept(IBasePlayerRPC<PlayerObject> handle) {
									set(handle);
								}
							});
				}
			}
			return future;
		}
	}

	public boolean offerPlayer(net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> playerObject) {
		IRPCFuture<IBasePlayerRPC<PlayerObject>> future = null;
		BasePlayerRPCLocal<PlayerObject> existingHandle = null;
		synchronized (this) {
			existingHandle = this.handle;
			if (existingHandle != null) {
				return false;
			}
			future = this.future;
			existingHandle = createHandle(playerObject);
			PLAYER_HANDLE.setRelease(this, existingHandle);
		}
		try {
			if (existingHandle.isEaglerPlayer()) {
				server.getPlatform().eventDispatcher().dispatchPlayerReadyEvent(this);
			}
		} finally {
			if (future != null) {
				((RPCConsumerFuture<IBasePlayerRPC<PlayerObject>, IBasePlayerRPC<PlayerObject>>) future)
						.accept(existingHandle);
			}
		}
		return true;
	}

	private BasePlayerRPCLocal<PlayerObject> createHandle(
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> player) {
		net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> eagPlayer = player.asEaglerPlayer();
		if (eagPlayer != null) {
			return new EaglerPlayerRPCLocal<>(this, eagPlayer);
		} else {
			return new BasePlayerRPCLocal<>(this, player);
		}
	}

	void handleDestroyed() {
		BasePlayerRPCLocal<PlayerObject> handle = handle();
		if (handle != null) {
			handle.fireCloseListeners();
		}
	}

}
