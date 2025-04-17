package net.lax1dude.eaglercraft.backend.rpc.api.skins;

import javax.annotation.Nonnull;

public enum EnumSkinModel {
	STEVE(0), ALEX(1);

	private final int id;

	private EnumSkinModel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Nonnull
	public static EnumSkinModel getById(int model) {
		return model == 1 ? ALEX : STEVE;
	}

}
