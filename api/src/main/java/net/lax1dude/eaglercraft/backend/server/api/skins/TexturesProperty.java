package net.lax1dude.eaglercraft.backend.server.api.skins;

import javax.annotation.Nonnull;

public final class TexturesProperty {

	public static TexturesProperty create(@Nonnull String value, @Nonnull String signature) {
		if(value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if(signature == null) {
			throw new IllegalArgumentException("signature cannot be null");
		}
		return new TexturesProperty(value, signature);
	}

	private final String value;
	private final String signature;

	private TexturesProperty(String value, String signature) {
		this.value = value;
		this.signature = signature;
	}

	@Nonnull
	public String getValue() {
		return value;
	}

	@Nonnull
	public String getSignature() {
		return signature;
	}

	public int hashCode() {
		return value.hashCode() * 31 + signature.hashCode();
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof TexturesProperty t) && t.value.equals(value)
				&& t.signature.equals(signature));
	}

}
