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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindService;

public class RewindService<PlayerObject> implements IEaglerXRewindService<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final boolean enabled;

	private final ReadWriteLock registerLock;
	private final Map<IEaglerXRewindProtocol<PlayerObject, ?>, int[]> registeredProtocols;
	private final IEaglerXRewindProtocol<PlayerObject, ?>[] protocolIds;

	public RewindService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.enabled = server.getConfig().getSettings().getProtocols().isEaglerXRewindAllowed();
		this.registerLock = enabled ? new ReentrantReadWriteLock() : null;
		this.registeredProtocols = enabled ? new IdentityHashMap<>() : null;
		this.protocolIds = enabled ? new IEaglerXRewindProtocol[256] : null;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isActive() {
		if(!enabled) {
			return false;
		}
		registerLock.readLock().lock();
		try {
			return !registeredProtocols.isEmpty();
		}finally {
			registerLock.readLock().unlock();
		}
	}

	@Override
	public void registerLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, ?> protocolHandler) {
		if(!enabled) {
			throw new UnsupportedOperationException("EaglerXRewind is not enabled on this server");
		}
		if(protocolHandler == null) {
			throw new NullPointerException("protocolHandler");
		}
		int handshakeProtocol = protocolHandler.getEmulatedEaglerHandshake();
		if(!server.isEaglerHandshakeSupported(handshakeProtocol)) {
			throw new UnsupportedOperationException("Unsupported handshake protocol version: " + handshakeProtocol);
		}
		int[] protocols = protocolHandler.getLegacyProtocols();
		if(protocols == null || protocols.length == 0) {
			return;
		}
		protocols = protocols.clone();
		for(int i = 0; i < protocols.length; ++i) {
			int j = protocols[i];
			if(j < 0 && j > 255) {
				throw new UnsupportedOperationException("Invalid legacy protocol version: " + j);
			}
		}
		registerLock.writeLock().lock();
		try {
			if(registeredProtocols.putIfAbsent(protocolHandler, protocols) == null) {
				for(int i = 0; i < protocols.length; ++i) {
					protocolIds[protocols[i]] = protocolHandler;
				}
			}else {
				return;
			}
		}finally {
			registerLock.writeLock().unlock();
		}
		protocolHandler.handleRegistered(server);
	}

	@Override
	public void unregisterLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, ?> protocolHandler) {
		if(!enabled) {
			return;
		}
		if(protocolHandler == null) {
			throw new NullPointerException("protocolHandler");
		}
		registerLock.writeLock().lock();
		try {
			int[] protocols = registeredProtocols.remove(protocolHandler);
			if(protocols != null) {
				for(int i = 0; i < protocols.length; ++i) {
					protocolIds[protocols[i]] = null;
				}
			}else {
				return;
			}
		}finally {
			registerLock.writeLock().unlock();
		}
		protocolHandler.handleUnregistered(server);
	}

	@Override
	public boolean hasLegacyProtocol(int protocolVersion) {
		if(!enabled || protocolVersion < 0 || protocolVersion > 255) {
			return false;
		}
		registerLock.readLock().lock();
		try {
			return protocolIds[protocolVersion] != null;
		}finally {
			registerLock.readLock().unlock();
		}
	}

	public IEaglerXRewindProtocol<PlayerObject, ?> getProtocol(int protocolVersion) {
		if(!enabled || protocolVersion < 0 || protocolVersion > 255) {
			return null;
		}
		registerLock.readLock().lock();
		try {
			return protocolIds[protocolVersion];
		}finally {
			registerLock.readLock().unlock();
		}
	}

}
