package net.lax1dude.eaglercraft.backend.supervisor.protocol.util;

public interface ILoggerSv {

	void info(String msg);

	void info(String msg, Throwable t);

	void warn(String msg);

	void warn(String msg, Throwable t);

	void error(String msg);

	void error(String msg, Throwable t);

}
