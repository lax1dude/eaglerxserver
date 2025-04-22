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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.ImmutableMap;

import net.lax1dude.eaglercraft.backend.server.api.ExtendedCapabilitySpec;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectIntMap;

public class ExtCapabilityMap {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Object, Set<ExtendedCapabilitySpec>> pluginCapabilities = new IdentityHashMap<>();

	private ExtendedCapabilitySpec internUUIDs(ExtendedCapabilitySpec spec) { //TODO: sort the versions so they can be matched faster
		ExtendedCapabilitySpec.Version[] vers = spec.getMajorVersions().clone();
		for(int i = 0; i < vers.length; ++i) {
			vers[i] = ExtendedCapabilitySpec.version(EaglerXServer.uuidInterner.intern(vers[i].getMajorVersion()),
					vers[i].getMinorVersions());
		}
		return ExtendedCapabilitySpec.create(vers);
	}

	public void registerCapability(Object plugin, ExtendedCapabilitySpec capability) {
		capability = internUUIDs(capability);
		lock.writeLock().lock();
		try {
			Set<ExtendedCapabilitySpec> caps = pluginCapabilities.get(plugin);
			if(caps == null) {
				pluginCapabilities.put(capability, caps = new HashSet<>());
			}
			caps.add(capability);
		}finally {
			lock.writeLock().unlock();
		}
	}

	public void unregisterCapability(Object plugin, ExtendedCapabilitySpec capability) {
		lock.writeLock().lock();
		try {
			Set<ExtendedCapabilitySpec> caps = pluginCapabilities.get(plugin);
			if(caps != null) {
				if(caps.remove(capability)) {
					if(caps.isEmpty()) {
						pluginCapabilities.remove(plugin);
					}
				}
			}
		}finally {
			lock.writeLock().unlock();
		}
	}

	public Map<UUID, Byte> acceptExtendedCapabilities(ObjectIntMap<UUID> extCapabilities) {
		Map<UUID, Byte> builder;
		lock.readLock().lock();
		try {
			Collection<Set<ExtendedCapabilitySpec>> vals = pluginCapabilities.values();
			if(vals.isEmpty()) {
				return Collections.emptyMap();
			}
			builder = new HashMap<>(extCapabilities.size());
			for(Set<ExtendedCapabilitySpec> specs : vals) {
				for(ExtendedCapabilitySpec spec : specs) {
					ExtendedCapabilitySpec.Version[] vers = spec.getMajorVersions();
					eagler: for(int j = vers.length - 1; j >= 0; --j) {
						ExtendedCapabilitySpec.Version ver = vers[j];
						int bitFieldIndex = extCapabilities.indexOf(ver.getMajorVersion());
						if(bitFieldIndex >= 0) {
							int bits = extCapabilities.indexGet(bitFieldIndex);
							int maxVer = -1;
							for (int i : ver.getMinorVersions()) {
								if(i > maxVer && (bits & (1 << i)) != 0) {
									maxVer = i;
								}
							}
							if(maxVer != -1) {
								Byte b = builder.get(ver.getMajorVersion());
								if(b != null) {
									if((b.byteValue() & 0xFF) < maxVer) {
										builder.put(ver.getMajorVersion(), (byte) maxVer);
									}
								}else {
									builder.put(ver.getMajorVersion(), (byte) maxVer);
								}
								break eagler;
							}
						}
					}
				}
			}
		}finally {
			lock.readLock().unlock();
		}
		return ImmutableMap.copyOf(builder);
	}

	public boolean isCapabilityRegistered(UUID capabilityUUID, int version) {
		lock.readLock().lock();
		try {
			Collection<Set<ExtendedCapabilitySpec>> vals = pluginCapabilities.values();
			if(vals.isEmpty()) {
				return false;
			}
			for(Set<ExtendedCapabilitySpec> specs : vals) {
				for(ExtendedCapabilitySpec spec : specs) {
					ExtendedCapabilitySpec.Version[] vers = spec.getMajorVersions();
					for(int i = 0; i < vers.length; ++i) {
						ExtendedCapabilitySpec.Version ver = vers[i];
						if(capabilityUUID.equals(ver.getMajorVersion())) {
							int[] minorVerss = ver.getMinorVersions();
							for(int j = 0, l = minorVerss.length; j < l; ++j) {
								if((minorVerss[j] & 0xFF) > version) {
									return true;
								}
							}
						}
					}
				}
			}
		}finally {
			lock.readLock().unlock();
		}
		return false;
	}

}
