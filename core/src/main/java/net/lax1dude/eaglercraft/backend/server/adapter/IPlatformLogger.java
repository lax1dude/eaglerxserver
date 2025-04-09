package net.lax1dude.eaglercraft.backend.server.adapter;

import net.lax1dude.eaglercraft.backend.supervisor.protocol.util.ILoggerSv;

public interface IPlatformLogger extends ILoggerSv {

	void info(String msg);

	void info(String msg, Throwable thrown);

	void warn(String msg);

	void warn(String msg, Throwable thrown);

	void error(String msg);

	void error(String msg, Throwable thrown);

	IPlatformSubLogger createSubLogger(String name);

}
