package net.lax1dude.eaglercraft.backend.eaglerweb.bungee;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebLogger;

public class JavaLogger implements IEaglerWebLogger {

	protected final Logger logger;

	public JavaLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void info(String msg, Throwable thrown) {
		logger.log(Level.INFO, msg, thrown);
	}

	@Override
	public void warn(String msg) {
		logger.warning(msg);
	}

	@Override
	public void warn(String msg, Throwable thrown) {
		logger.log(Level.WARNING, msg, thrown);
	}

	@Override
	public void error(String msg) {
		logger.severe(msg);
	}

	@Override
	public void error(String msg, Throwable thrown) {
		logger.log(Level.SEVERE, msg, thrown);
	}

	@Override
	public IRewindSubLogger createSubLogger(String name) {
		return new SubLogger(name, this);
	}

	public class SubLogger implements IRewindSubLogger {

		private String name;
		private IEaglerWebLogger parent;

		public SubLogger(String name, IEaglerWebLogger parent) {
			this.name = name;
			this.parent = parent;
		}

		@Override
		public void info(String msg) {
			logger.info("[" + name + "]: " + msg);
		}

		@Override
		public void info(String msg, Throwable thrown) {
			logger.log(Level.INFO, "[" + name + "]: " + msg, thrown);
		}

		@Override
		public void warn(String msg) {
			logger.warning("[" + name + "]: " + msg);
		}

		@Override
		public void warn(String msg, Throwable thrown) {
			logger.log(Level.WARNING, "[" + name + "]: " + msg, thrown);
		}

		@Override
		public void error(String msg) {
			logger.severe("[" + name + "]: " + msg);
		}

		@Override
		public void error(String msg, Throwable thrown) {
			logger.log(Level.SEVERE, "[" + name + "]: " + msg, thrown);
		}

		@Override
		public IRewindSubLogger createSubLogger(String name) {
			return new SubLogger(name + "|" + name, this);
		}

		@Override
		public IEaglerWebLogger getParent() {
			return parent;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

	}

}
