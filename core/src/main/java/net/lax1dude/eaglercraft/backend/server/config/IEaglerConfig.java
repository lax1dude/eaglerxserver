package net.lax1dude.eaglercraft.backend.server.config;

import java.io.IOException;

public interface IEaglerConfig {

	EnumConfigFormat getFormat();

	IEaglerConfSection getRoot();

	boolean isModified();

	boolean saveIfModified() throws IOException;

}
