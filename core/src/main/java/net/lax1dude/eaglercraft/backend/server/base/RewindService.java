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
	public <T> void registerLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, T> protocolHandler) {
		if(!enabled) {
			throw new UnsupportedOperationException("EaglerXRewind is not enabled on this server");
		}
		int handshakeProtocol = protocolHandler.getEmulatedEaglerHandshake();
		if(!server.isEaglerHandshakeSupported(handshakeProtocol)) {
			throw new UnsupportedOperationException("Unsupported handshake protocol version: " + handshakeProtocol);
		}
		int[] protocols = protocolHandler.getLegacyProtocols();
		if(protocols == null || protocols.length == 0) {
			return;
		}
		registerLock.writeLock().lock();
		try {
			if(!registeredProtocols.containsKey(protocolHandler)) {
				for(int i = 0; i < protocols.length; ++i) {
					int j = protocols[i];
					if(j < 0 && j > 255) {
						throw new UnsupportedOperationException("Invalid legacy protocol version: " + j);
					}
				}
				protocols = protocols.clone();
				registeredProtocols.put(protocolHandler, protocols);
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
	public <T> void unregisterLegacyProtocol(IEaglerXRewindProtocol<PlayerObject, T> protocolHandler) {
		if(!enabled) {
			return;
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
