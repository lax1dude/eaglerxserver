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

package net.lax1dude.eaglercraft.backend.skin_cache;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.lax1dude.eaglercraft.backend.util.ILoggerAdapter;

class SkinCacheTable {

	class SkinCacheTableThreadEnv {

		protected final PreparedStatement statementLoad;
		protected final PreparedStatement statementStore;
		protected final PreparedStatement statementStoreIndex;

		protected SkinCacheTableThreadEnv(Connection conn) throws SQLException {
			statementLoad = conn.prepareStatement(
					"SELECT " + name + "_objects.TextureData "
					+ "FROM " + name + "_objects "
					+ "INNER JOIN " + name + "_indices ON " + name + "_objects.TextureID = " + name + "_indices.TextureData "
					+ "WHERE " + name + "_indices.TextureURL = ?");
			if(sqlite) {
				statementStore = conn.prepareStatement(
						"INSERT OR IGNORE INTO " + name + "_objects (TextureTime, TextureHash, TextureData) "
						+ "VALUES(?, ?, ?)");
				statementStoreIndex = conn.prepareStatement(
						"INSERT OR REPLACE INTO " + name + "_indices (TextureURL, TextureData) "
						+ "SELECT ?, TextureID FROM " + name + "_objects WHERE TextureHash = ?");
			}else {
				statementStore = conn.prepareStatement(
						"INSERT IGNORE INTO " + name + "_objects (TextureTime, TextureHash, TextureData) "
						+ "VALUES(?, ?, ?)");
				statementStoreIndex = conn.prepareStatement(
						"REPLACE INTO " + name + "_indices (TextureURL, TextureData) "
						+ "SELECT ?, TextureID FROM " + name + "_objects WHERE TextureHash = ?");
			}
		}

		void dispose() {
			SkinCacheDatastore.disposeStmt(statementLoad);
			SkinCacheDatastore.disposeStmt(statementStore);
			SkinCacheDatastore.disposeStmt(statementStoreIndex);
		}

	}

	protected final String name;
	protected final boolean sqlite;
	protected final ILoggerAdapter logger;

	protected final PreparedStatement statementCount;

	// for sqlite databases
	protected final PreparedStatement statementSQLiteListExpired;
	protected final PreparedStatement statementSQLiteListOld;
	protected final PreparedStatement statementSQLiteDeleteObject;
	protected final PreparedStatement statementSQLiteDeleteIndex;

	// for non-sqlite databases
	protected final PreparedStatement statementDeleteExpired;
	protected final PreparedStatement statementDeleteOld;

	SkinCacheTable(String name, Connection conn, boolean sqlite, ILoggerAdapter logger) throws SQLException {
		this.name = name;
		this.sqlite = sqlite;
		this.logger = logger;
		try(Statement stmt = conn.createStatement()) {
			if(sqlite) {
				// TextureID will be used by SQLite as rowid
				stmt.execute("CREATE TABLE IF NOT EXISTS "
						+ name + "_objects ("
						+ "TextureID INTEGER NOT NULL,"
						+ "TextureTime DATETIME NOT NULL,"
						+ "TextureHash BLOB NOT NULL,"
						+ "TextureData BLOB NOT NULL,"
						+ "PRIMARY KEY(TextureID ASC))");
			}else {
				stmt.execute("CREATE TABLE IF NOT EXISTS "
						+ name + "_objects ("
						+ "TextureID BIGINT NOT NULL AUTO_INCREMENT,"
						+ "TextureTime DATETIME NOT NULL,"
						+ "TextureHash BLOB NOT NULL,"
						+ "TextureData BLOB NOT NULL,"
						+ "PRIMARY KEY(TextureID))");
			}
			stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS "
					+ name + "_hash_index "
					+ "ON " + name + "_objects (TextureHash)");
			if(sqlite) {
				stmt.execute("CREATE TABLE IF NOT EXISTS "
						+ name + "_indices ("
						+ "TextureURL VARCHAR(256) NOT NULL,"
						+ "TextureData INTEGER NOT NULL,"
						+ "PRIMARY KEY(TextureURL))");
			}else {
				stmt.execute("CREATE TABLE IF NOT EXISTS "
						+ name + "_indices ("
						+ "TextureURL VARCHAR(256) NOT NULL,"
						+ "TextureData BIGINT NOT NULL,"
						+ "PRIMARY KEY(TextureURL))");
			}
			stmt.execute("CREATE INDEX IF NOT EXISTS "
					+ name + "_indices_index "
					+ "ON " + name + "_indices (TextureData)");
		}
		this.statementCount = conn.prepareStatement("SELECT COUNT(*) AS total_skins FROM " + name + "_objects");
		if(sqlite) {
			statementSQLiteListExpired = conn.prepareStatement("SELECT TextureID FROM " + name + "_objects WHERE textureTime < ?");
			statementSQLiteListOld = conn.prepareStatement("SELECT TextureID FROM " + name + "_objects ORDER BY TextureTime ASC LIMIT ?");
			statementSQLiteDeleteObject = conn.prepareStatement("DELETE FROM " + name + "_objects WHERE TextureID = ?");
			statementSQLiteDeleteIndex = conn.prepareStatement("DELETE FROM " + name + "_indices WHERE TextureData = ?");
			statementDeleteExpired = null;
			statementDeleteOld = null;
		}else {
			statementSQLiteListExpired = null;
			statementSQLiteListOld = null;
			statementSQLiteDeleteObject = null;
			statementSQLiteDeleteIndex = null;
			statementDeleteExpired = conn.prepareStatement(
					"DELETE o, i FROM " + name + "_objects o "
					+ "JOIN " + name + "_indices i ON o.TextureID = i.TextureData "
					+ "WHERE o.textureTime < ?");
			statementDeleteOld = conn.prepareStatement(
					"DELETE o, i FROM " + name + "_objects o "
					+ "JOIN " + name + "_indices i ON o.TextureID = i.TextureData "
					+ "WHERE o.TextureID IN"
						+ "(SELECT TextureID FROM "
							+ "(SELECT TextureID FROM " + name + "_objects "
							+ "ORDER BY TextureTime ASC LIMIT ?)"
						+ " AS eagler)");
		}
	}

	SkinCacheTableThreadEnv createThreadEnv(Connection conn) throws SQLException {
		return new SkinCacheTableThreadEnv(conn);
	}

	byte[] loadSkin(SkinCacheTableThreadEnv env, String skinURL) throws SQLException {
		PreparedStatement stmt = env.statementLoad;
		stmt.setString(1, skinURL);
		byte[] result = null;
		try(ResultSet set = stmt.executeQuery()) {
			if(set.next()) {
				result = set.getBytes(1);
			}
		}
		return result;
	}

	void storeSkin(SkinCacheTableThreadEnv env, String skinURL, byte[] hash, byte[] data) throws SQLException {
		PreparedStatement stmt = env.statementStore;
		stmt.setDate(1, new Date(System.currentTimeMillis()));
		stmt.setBytes(2, hash);
		stmt.setBytes(3, data);
		stmt.executeUpdate();
		stmt = env.statementStoreIndex;
		stmt.setString(1, skinURL);
		stmt.setBytes(2, hash);
		stmt.executeUpdate();
	}

	void runCleanup(int maxObjects, long expiryObjectsMillis) throws SQLException {
		Date expiryObjects = new Date(expiryObjectsMillis);
		if(sqlite) {
			statementSQLiteListExpired.setDate(1, expiryObjects);
			try(ResultSet resultSet = statementSQLiteListExpired.executeQuery()) {
				while(resultSet.next()) {
					int id = resultSet.getInt(1);
					statementSQLiteDeleteObject.setInt(1, id);
					statementSQLiteDeleteObject.executeUpdate();
					statementSQLiteDeleteIndex.setInt(1, id);
					statementSQLiteDeleteIndex.executeUpdate();
				}
			}
		}else {
			statementDeleteExpired.setDate(1, expiryObjects);
			statementDeleteExpired.executeUpdate();
		}
		int totalSkins;
		try(ResultSet set = statementCount.executeQuery()) {
			if(set.next()) {
				totalSkins = set.getInt(1);
			}else {
				throw new SQLException("Empty ResultSet recieved when checking \"" + name + "_objects\" row count");
			}
		}
		if(totalSkins > maxObjects) {
			int deleteCount = totalSkins - maxObjects + (maxObjects >> 3);
			logger.warn(name + " object cache has passed " + maxObjects + " skins in size (" + totalSkins
					+ "), deleting " + deleteCount + " skins from the cache to free space");
			if(sqlite) {
				statementSQLiteListOld.setInt(1, deleteCount);
				try(ResultSet resultSet = statementSQLiteListOld.executeQuery()) {
					while(resultSet.next()) {
						int id = resultSet.getInt(1);
						statementSQLiteDeleteObject.setInt(1, id);
						statementSQLiteDeleteObject.executeUpdate();
						statementSQLiteDeleteIndex.setInt(1, id);
						statementSQLiteDeleteIndex.executeUpdate();
					}
				}
			}else {
				statementDeleteOld.setInt(1, deleteCount);
				statementDeleteOld.executeUpdate();
			}
		}
	}

	int countSkins() {
		try(ResultSet set = statementCount.executeQuery()) {
			if(set.next()) {
				return set.getInt(1);
			}else {
				return -1;
			}
		}catch(SQLException ex) {
			logger.error("Could not count stored " + name + "!", ex);
			return -1;
		}
	}

	void dispose() {
		SkinCacheDatastore.disposeStmt(statementCount);
		SkinCacheDatastore.disposeStmt(statementSQLiteListExpired);
		SkinCacheDatastore.disposeStmt(statementSQLiteListOld);
		SkinCacheDatastore.disposeStmt(statementSQLiteDeleteObject);
		SkinCacheDatastore.disposeStmt(statementSQLiteDeleteIndex);
		SkinCacheDatastore.disposeStmt(statementDeleteExpired);
		SkinCacheDatastore.disposeStmt(statementDeleteOld);
	}

}