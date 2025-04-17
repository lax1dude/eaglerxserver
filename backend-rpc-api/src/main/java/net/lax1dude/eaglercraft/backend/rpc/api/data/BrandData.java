package net.lax1dude.eaglercraft.backend.rpc.api.data;

import java.util.UUID;

import javax.annotation.Nonnull;

public final class BrandData {

	@Nonnull
	public static BrandData create(@Nonnull String brandName, @Nonnull String brandVersion,
			@Nonnull UUID brandUUID) {
		if(brandName == null) {
			throw new NullPointerException("brandName");
		}
		if(brandVersion == null) {
			throw new NullPointerException("brandVersion");
		}
		if(brandUUID == null) {
			throw new NullPointerException("brandUUID");
		}
		return new BrandData(brandName, brandVersion, brandUUID);
	}

	private final String brandName;
	private final String brandVersion;
	private final UUID brandUUID;

	private BrandData(String brandName, String brandVersion, UUID brandUUID) {
		this.brandName = brandName;
		this.brandVersion = brandVersion;
		this.brandUUID = brandUUID;
	}

	@Nonnull
	public String getBrandName() {
		return brandName;
	}

	@Nonnull
	public String getBrandVersion() {
		return brandVersion;
	}

	@Nonnull
	public UUID getBrandUUID() {
		return brandUUID;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + brandName.hashCode();
		result = 31 * result + brandVersion.hashCode();
		result = 31 * result + brandUUID.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BrandData other))
			return false;
		if (!brandName.equals(other.brandName))
			return false;
		if (!brandVersion.equals(other.brandVersion))
			return false;
		if (!brandUUID.equals(other.brandUUID))
			return false;
		return true;
	}

}
