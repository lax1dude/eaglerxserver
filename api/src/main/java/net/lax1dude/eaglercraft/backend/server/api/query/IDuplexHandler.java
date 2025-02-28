package net.lax1dude.eaglercraft.backend.server.api.query;

public interface IDuplexHandler<T> {

	void process(IQueryConnection connection, T object);

}
