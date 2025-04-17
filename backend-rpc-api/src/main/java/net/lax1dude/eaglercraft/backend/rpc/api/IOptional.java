package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IOptional<T> {

	boolean isSuccess();

	@Nullable
	@SuppressWarnings("unchecked")
	default T orNull() {
		return isSuccess() ? (T) this : null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default T orDefault(@Nonnull Supplier<T> defaultValue) {
		if(isSuccess()) {
			return (T) this;
		}else {
			return defaultValue.get();
		}
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	default T orThrow() throws IllegalStateException {
		if(isSuccess()) {
			return (T) this;
		}else {
			throw new IllegalStateException("Resulting value is not successful");
		}
	}

}
