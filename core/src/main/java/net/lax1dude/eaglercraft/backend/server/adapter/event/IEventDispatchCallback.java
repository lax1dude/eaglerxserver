package net.lax1dude.eaglercraft.backend.server.adapter.event;

public interface IEventDispatchCallback<T> {

	void complete(T result, Throwable error);

}
