package net.lax1dude.eaglercraft.backend.eaglerweb.base;

public interface IEaglerWebLogger {

	void info(String msg);

	void info(String msg, Throwable thrown);

	void warn(String msg);

	void warn(String msg, Throwable thrown);

	void error(String msg);

	void error(String msg, Throwable thrown);

	IRewindSubLogger createSubLogger(String name);

	public interface IRewindSubLogger extends IEaglerWebLogger {

		IEaglerWebLogger getParent();

		String getName();

		void setName(String name);

	}

}
