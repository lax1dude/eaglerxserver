/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.eaglermotd.bungee;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDLogger;

public class JavaLogger implements IEaglerMOTDLogger {

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
		private IEaglerMOTDLogger parent;

		public SubLogger(String name, IEaglerMOTDLogger parent) {
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
