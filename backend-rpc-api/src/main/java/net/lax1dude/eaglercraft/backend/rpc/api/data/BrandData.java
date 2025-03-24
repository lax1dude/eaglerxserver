package net.lax1dude.eaglercraft.backend.rpc.api.data;

public final class BrandData {

	public static BrandData create(String brandName, String brandVersion) {
		if(brandName == null) {
			throw new NullPointerException("brandName");
		}
		if(brandVersion == null) {
			throw new NullPointerException("brandVersion");
		}
		return new BrandData(brandName, brandVersion);
	}

	private final String brandName;
	private final String brandVersion;

	private BrandData(String brandName, String brandVersion) {
		this.brandName = brandName;
		this.brandVersion = brandVersion;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getBrandVersion() {
		return brandVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + brandName.hashCode();
		result = prime * result + brandVersion.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BrandData))
			return false;
		BrandData other = (BrandData) obj;
		if (!brandName.equals(other.brandName))
			return false;
		if (!brandVersion.equals(other.brandVersion))
			return false;
		return true;
	}

}
