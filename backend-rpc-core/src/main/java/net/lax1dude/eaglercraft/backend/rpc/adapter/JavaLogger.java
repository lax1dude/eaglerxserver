package net.lax1dude.eaglercraft.backend.rpc.adapter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLogger implements IPlatformLogger {

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
	public IPlatformSubLogger createSubLogger(String name) {
		return new SubLogger(name, this);
	}

	public class SubLogger implements IPlatformSubLogger {

		private String name;
		private IPlatformLogger parent;

		public SubLogger(String name, IPlatformLogger parent) {
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
		public IPlatformSubLogger createSubLogger(String name) {
			return new SubLogger(name + "|" + name, this);
		}

		@Override
		public IPlatformLogger getParent() {
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
