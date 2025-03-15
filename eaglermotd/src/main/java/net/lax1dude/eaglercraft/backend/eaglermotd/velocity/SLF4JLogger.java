package net.lax1dude.eaglercraft.backend.eaglermotd.velocity;

import org.slf4j.Logger;

import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDLogger;

public class SLF4JLogger implements IEaglerMOTDLogger {

	protected final Logger logger;

	public SLF4JLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void info(String msg, Throwable thrown) {
		logger.info(msg, thrown);
	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}

	@Override
	public void warn(String msg, Throwable thrown) {
		logger.warn(msg, thrown);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void error(String msg, Throwable thrown) {
		logger.error(msg, thrown);
	}

	@Override
	public IRewindSubLogger createSubLogger(String name) {
		return new SubLogger(name, this);
	}

	public class SubLogger implements IRewindSubLogger {

		private String name;
		private IEaglerMOTDLogger parent;

		public SubLogger(String name, IEaglerMOTDLogger parent) {
			this.name = name;
			this.parent = parent;
		}

		@Override
		public void info(String msg) {
			logger.info("[{}]: {}", name, msg);
		}

		@Override
		public void info(String msg, Throwable thrown) {
			logger.info("[" + name + "]: " + msg, thrown);
		}

		@Override
		public void warn(String msg) {
			logger.warn("[{}]: {}", name, msg);
		}

		@Override
		public void warn(String msg, Throwable thrown) {
			logger.warn("[" + name + "]: " + msg, thrown);
		}

		@Override
		public void error(String msg) {
			logger.error("[{}]: {}", name, msg);
		}

		@Override
		public void error(String msg, Throwable thrown) {
			logger.error("[" + name + "]: " + msg, thrown);
		}

		@Override
		public IRewindSubLogger createSubLogger(String name) {
			return new SubLogger(name + "|" + name, this);
		}

		@Override
		public IEaglerMOTDLogger getParent() {
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
