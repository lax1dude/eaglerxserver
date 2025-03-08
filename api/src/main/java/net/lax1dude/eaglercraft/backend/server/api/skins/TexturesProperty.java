package net.lax1dude.eaglercraft.backend.server.api.skins;

public final class TexturesProperty {

	public static TexturesProperty create(String value, String signature) {
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

	public String getValue() {
		return value;
	}

	public String getSignature() {
		return signature;
	}

	public int hashCode() {
		return value.hashCode() * 31 + signature.hashCode();
	}

	public boolean equals(Object o) {
		TexturesProperty t;
		return this == o || ((o instanceof TexturesProperty) && (t = (TexturesProperty) o).value.equals(value)
				&& t.signature.equals(signature));
	}

}
