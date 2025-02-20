package net.lax1dude.eaglercraft.backend.server.bungee.chat;

class BuilderTextChild<ParentType> extends BuilderTextBase<ParentType> {

	private final ParentType parent;

	BuilderTextChild(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		((IAppendCallback)parent).append(build());
		return parent;
	}

}
