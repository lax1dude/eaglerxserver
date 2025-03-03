package net.lax1dude.eaglercraft.backend.server.api;

public interface IOptional<T> {

	boolean isSuccess();

	@SuppressWarnings("unchecked")
	default T orNull() {
		return isSuccess() ? (T) this : null;
	}

	@SuppressWarnings("unchecked")
	default T orThrow() throws IllegalStateException {
		if(isSuccess()) {
			return (T) this;
		}else {
			throw new IllegalStateException("Resulting value is not successful");
		}
	}

}
