package net.lax1dude.eaglercraft.backend.server.api.skins;

public enum EnumSkinModel {
	STEVE(0), ALEX(1);

	private final int id;

	private EnumSkinModel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static EnumSkinModel getById(int model) {
		return model == 1 ? ALEX : STEVE;
	}

}
