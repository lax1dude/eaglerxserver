package net.lax1dude.eaglercraft.backend.server.velocity.chat;

class BuilderTranslationChild<ParentType> extends BuilderTranslationBase<ParentType> {

	private final ParentType parent;

	BuilderTranslationChild(ParentType parent) {
		this.parent = parent;
	}

	@Override
	public ParentType end() {
		((IAppendCallback)parent).append(build());
		return parent;
	}

}
