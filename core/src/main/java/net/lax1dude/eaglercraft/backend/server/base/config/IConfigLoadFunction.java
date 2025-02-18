package net.lax1dude.eaglercraft.backend.server.base.config;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

public interface IConfigLoadFunction<T> {

	T call(IEaglerConfSection section);

}
