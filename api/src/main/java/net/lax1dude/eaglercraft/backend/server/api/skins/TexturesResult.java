package net.lax1dude.eaglercraft.backend.server.api.skins;

import com.google.common.base.Objects;

public final class TexturesResult {

	public static TexturesResult create(String skinURL, EnumSkinModel skinModel, String capeURL) {
		return new TexturesResult(skinURL, skinModel, capeURL);
	}

	private final String skinURL;
	private final EnumSkinModel skinModel;
	private final String capeURL;

	private TexturesResult(String skinURL, EnumSkinModel skinModel, String capeURL) {
		this.skinURL = skinURL;
		this.skinModel = skinModel;
		this.capeURL = capeURL;
	}

	public String getSkinURL() {
		return skinURL;
	}

	public EnumSkinModel getSkinModel() {
		return skinModel;
	}

	public String getCapeURL() {
		return capeURL;
	}

	public int hashCode() {
		int code = 0;
		if(skinURL != null) code += skinURL.hashCode();
		code *= 31;
		if(skinModel != null) code += skinModel.hashCode();
		code *= 31;
		if(capeURL != null) code += capeURL.hashCode();
		return code;
	}

	public boolean equals(Object o) {
		return this == o || ((o instanceof TexturesResult t) && Objects.equal(t.skinURL, skinURL)
				&& Objects.equal(t.skinModel, skinModel) && Objects.equal(t.capeURL, capeURL));
	}

}
