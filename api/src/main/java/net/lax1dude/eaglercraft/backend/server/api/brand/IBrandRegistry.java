package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

public interface IBrandRegistry {

	static final UUID BRAND_NULL = new UUID(0L, 0L);
	static final UUID BRAND_VANILLA = new UUID(0x1DCE015CD384374EL, 0x85030A4DE95E5736L);
	static final UUID BRAND_EAGLERCRAFTX_V4 = new UUID(0x4448369E4E873621L, 0x94F5E28EEB160524L);
	static final UUID BRAND_EAGLERCRAFTX_LEGACY =  new UUID(0x71D0C81201C2366AL, 0xA0D23D9AA10846EBL);
	static final UUID BRAND_EAGLERCRAFT_1_12 =  new UUID(0x522B2CE5C9B936CFL, 0xBE7C5D90F55E631AL);

	UUID getBrandUUIDClient(String brandString);

	UUID getBrandUUIDClientLegacy(String brandString);

	default void registerBrand(UUID brandUUID, String brandDesc) {
		registerBrand(brandUUID, brandDesc, false, false);
	}

	default void registerBrand(UUID brandUUID, String brandDesc, boolean hacked) {
		registerBrand(brandUUID, brandDesc, hacked, false);
	}

	void registerBrand(UUID brandUUID, String brandDesc, boolean hacked, boolean legacy);

	void unregisterBrand(UUID brandUUID);

	IBrandRegistration lookupRegisteredBrand(UUID brandUUID);

}
