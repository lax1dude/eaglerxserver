package net.lax1dude.eaglercraft.backend.rpc.api.data;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public final class TexturesData {

	@Nonnull
	public static TexturesData create(@Nonnull IEaglerPlayerSkin skin, @Nonnull IEaglerPlayerCape cape) {
		if(skin == null) {
			throw new NullPointerException("skin");
		}
		if(cape == null) {
			throw new NullPointerException("cape");
		}
		return new TexturesData(skin, cape);
	}

	private final IEaglerPlayerSkin skin;
	private final IEaglerPlayerCape cape;

	private TexturesData(IEaglerPlayerSkin skin, IEaglerPlayerCape cape) {
		this.skin = skin;
		this.cape = cape;
	}

	@Nonnull
	public IEaglerPlayerSkin getSkin() {
		return skin;
	}

	@Nonnull
	public IEaglerPlayerCape getCape() {
		return cape;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + cape.hashCode();
		result = 31 * result + skin.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TexturesData other))
			return false;
		if (!cape.equals(other.cape))
			return false;
		if (!skin.equals(other.skin))
			return false;
		return true;
	}

}
