package net.lax1dude.eaglercraft.backend.rewind_v1_5;

public interface IRewindLogger {

	void info(String msg);

	void info(String msg, Throwable thrown);

	void warn(String msg);

	void warn(String msg, Throwable thrown);

	void error(String msg);

	void error(String msg, Throwable thrown);

	IRewindSubLogger createSubLogger(String name);

	public interface IRewindSubLogger extends IRewindLogger {

		IRewindLogger getParent();

		String getName();

		void setName(String name);

	}

}
