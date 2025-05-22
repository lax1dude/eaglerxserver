/*
 * Copyright (c) 2022-2025 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EaglerDrivers {

	private static Driver initializeDriver(String address, String driverClass, File baseFolder, ILoggerAdapter logger) {
		ClassLoader classLoader;
		if (address.equalsIgnoreCase("classpath")) {
			classLoader = EaglerDrivers.class.getClassLoader();
		} else {
			classLoader = driversJARs.get(address);
			if (classLoader == null) {
				File driver;
				if (address.equalsIgnoreCase("internal")) {
					driver = new File(baseFolder, "drivers/sqlite-jdbc.jar");
					driver.getParentFile().mkdirs();
					if (!driver.isFile()) {
						try (InputStream is = EaglerDrivers.class.getResourceAsStream(
									"/net/lax1dude/eaglercraft/backend/skin_cache/libs/sqlite-jdbc.library")) {
							if (is == null) {
								throw new IOException(
										"Missing classpath resource: net/lax1dude/eaglercraft/backend/skin_cache/libs/sqlite-jdbc.library");
							}
							try (OutputStream os = new FileOutputStream(driver)) {
								is.transferTo(os);
							}
						} catch (IOException ex) {
							throw new ExceptionInInitializerError(ex);
						}
					}
				} else {
					driver = new File(address);
				}
				URL driverURL;
				try {
					driverURL = driver.toURI().toURL();
				} catch (MalformedURLException ex) {
					logger.error("Invalid JDBC driver path: " + address);
					throw new ExceptionInInitializerError(ex);
				}
				classLoader = URLClassLoader.newInstance(new URL[] { driverURL }, ClassLoader.getSystemClassLoader());
				driversJARs.put(address, classLoader);
			}
		}

		Class<?> loadedDriver;
		try {
			loadedDriver = classLoader.loadClass(driverClass);
		} catch (ClassNotFoundException ex) {
			logger.error("Could not find JDBC driver class: " + driverClass);
			throw new ExceptionInInitializerError(ex);
		}
		Driver sqlDriver = null;
		try {
			sqlDriver = (Driver) loadedDriver.getConstructor().newInstance();
		} catch (Throwable ex) {
			logger.error("Could not initialize JDBC driver class: " + driverClass);
			throw new ExceptionInInitializerError(ex);
		}

		return sqlDriver;
	}

	private static final Map<String, ClassLoader> driversJARs = new HashMap<>();
	private static final Map<String, Driver> driversDrivers = new HashMap<>();

	public static Connection connectToDatabase(String address, String driverClass, String driverPath, Properties props,
			File baseFolder, ILoggerAdapter logger) throws SQLException {
		if (driverClass.equalsIgnoreCase("internal")) {
			driverClass = "org.sqlite.JDBC";
		}
		if (driverPath == null) {
			try {
				Class.forName(driverClass);
			} catch (ClassNotFoundException e) {
				throw new SQLException("Driver class not found in JRE: " + driverClass, e);
			}
			return DriverManager.getConnection(address, props);
		} else {
			String driverMapPath = "" + driverPath + "?" + driverClass;
			Driver dv = driversDrivers.get(driverMapPath);
			if (dv == null) {
				dv = initializeDriver(driverPath, driverClass, baseFolder, logger);
				driversDrivers.put(driverMapPath, dv);
			}
			return dv.connect(address, props);
		}
	}

}