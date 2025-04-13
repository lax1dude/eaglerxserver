package net.lax1dude.eaglercraft.backend.util;

public interface ILoggerAdapter {

	void info(String msg);

	void info(String msg, Throwable thrown);

	void warn(String msg);

	void warn(String msg, Throwable thrown);

	void error(String msg);

	void error(String msg, Throwable thrown);

}
