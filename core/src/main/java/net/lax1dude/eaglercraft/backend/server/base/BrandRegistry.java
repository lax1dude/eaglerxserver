package net.lax1dude.eaglercraft.backend.server.base;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.ImmutableSet;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;

public class BrandRegistry implements IBrandRegistry {

	private static class BrandRegistrationVanilla implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_VANILLA;
		}

		@Override
		public String getBrandDesc() {
			return "Vanilla";
		}

		@Override
		public boolean isVanillaMinecraft() {
			return true;
		}

		@Override
		public boolean isVanillaEagler() {
			return false;
		}

		@Override
		public boolean isLegacyClient() {
			return false;
		}

		@Override
		public boolean isHackedClient() {
			return false;
		}

	}

	private static class BrandRegistrationEaglerV4 implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_EAGLERCRAFTX_V4;
		}

		@Override
		public String getBrandDesc() {
			return "EaglercraftX";
		}

		@Override
		public boolean isVanillaMinecraft() {
			return false;
		}

		@Override
		public boolean isVanillaEagler() {
			return true;
		}

		@Override
		public boolean isLegacyClient() {
			return false;
		}

		@Override
		public boolean isHackedClient() {
			return false;
		}

	}

	private static class BrandRegistrationEaglerOld implements IBrandRegistration {

		@Override
		public UUID getBrandUUID() {
			return BRAND_EAGLERCRAFTX_LEGACY;
		}

		@Override
		public String getBrandDesc() {
			return "EaglercraftX-pre-u37";
		}

		@Override
		public boolean isVanillaMinecraft() {
			return false;
		}

		@Override
		public boolean isVanillaEagler() {
			return true;
		}

		@Override
		public boolean isLegacyClient() {
			return true;
		}

		@Override
		public boolean isHackedClient() {
			return false;
		}

	}

	private static class BrandRegistration implements IBrandRegistration {

		protected final UUID uuid;
		protected final String desc;
		protected final boolean hacked;
		protected final boolean legacy;
	
		protected BrandRegistration(UUID uuid, String desc, boolean hacked, boolean legacy) {
			this.uuid = uuid;
			this.desc = desc;
			this.hacked = hacked;
			this.legacy = legacy;
		}

		@Override
		public UUID getBrandUUID() {
			return uuid;
		}

		@Override
		public String getBrandDesc() {
			return desc;
		}

		@Override
		public boolean isVanillaMinecraft() {
			return false;
		}

		@Override
		public boolean isVanillaEagler() {
			return false;
		}

		@Override
		public boolean isLegacyClient() {
			return legacy;
		}

		@Override
		public boolean isHackedClient() {
			return hacked;
		}

	}

	private final ReadWriteLock mapLock;
	private final Map<UUID, IBrandRegistration> map;

	private static final Set<UUID> builtinUUIDs = ImmutableSet.<UUID>builder()
			.add(BRAND_VANILLA)
			.add(BRAND_EAGLERCRAFTX_V4)
			.add(BRAND_EAGLERCRAFTX_LEGACY)
			.build();

	public BrandRegistry() {
		mapLock = new ReentrantReadWriteLock();
		map = new HashMap<>();
		map.put(BRAND_VANILLA, new BrandRegistrationVanilla());
		map.put(BRAND_EAGLERCRAFTX_V4, new BrandRegistrationEaglerV4());
		map.put(BRAND_EAGLERCRAFTX_LEGACY, new BrandRegistrationVanilla());
	}

	@Override
	public UUID getBrandUUIDClient(String brandString) {
		return UUID.nameUUIDFromBytes(("EaglercraftXClient:" + brandString).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public UUID getBrandUUIDClientLegacy(String brandString) {
		return UUID.nameUUIDFromBytes(("EaglercraftXClientOld:" + brandString).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void registerBrand(UUID brandUUID, String brandDesc, boolean hacked, boolean legacy) {
		if(!builtinUUIDs.contains(brandUUID)) {
			IBrandRegistration reg = new BrandRegistration(brandUUID, brandDesc, hacked, legacy);
			mapLock.writeLock().lock();
			try {
				map.put(brandUUID, reg);
			}finally {
				mapLock.writeLock().unlock();
			}
		}
	}

	@Override
	public void unregisterBrand(UUID brandUUID) {
		if(!builtinUUIDs.contains(brandUUID)) {
			mapLock.writeLock().lock();
			try {
				map.remove(brandUUID);
			}finally {
				mapLock.writeLock().unlock();
			}
		}
	}

	@Override
	public IBrandRegistration lookupRegisteredBrand(UUID brandUUID) {
		mapLock.readLock().lock();
		try {
			return map.get(brandUUID);
		}finally {
			mapLock.readLock().unlock();
		}
	}

}
