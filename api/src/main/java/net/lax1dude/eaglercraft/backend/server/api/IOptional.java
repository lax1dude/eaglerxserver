package net.lax1dude.eaglercraft.backend.server.api;

import java.util.function.Supplier;

public interface IOptional<T> {

	boolean isSuccess();

	@SuppressWarnings("unchecked")
	default T orNull() {
		return isSuccess() ? (T) this : null;
	}

	@SuppressWarnings("unchecked")
	default T orDefault(Supplier<T> defaultValue) {
		if(isSuccess()) {
			return (T) this;
		}else {
			return defaultValue.get();
		}
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
