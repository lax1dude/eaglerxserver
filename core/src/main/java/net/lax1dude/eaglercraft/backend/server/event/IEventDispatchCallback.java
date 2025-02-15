package net.lax1dude.eaglercraft.backend.server.event;

public interface IEventDispatchCallback<T> {

	void complete(T result, Throwable error);

}
