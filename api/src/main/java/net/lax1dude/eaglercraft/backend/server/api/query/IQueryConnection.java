package net.lax1dude.eaglercraft.backend.server.api.query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IQueryConnection extends IEaglerConnection {

	@Nonnull
	String getAccept();

	void setHandlers(@Nonnull IDuplexBaseHandler compositeHandler);

	void setHandlers(@Nonnull IDuplexBaseHandler... compositeHandlers);

	void setStringHandler(@Nullable IDuplexStringHandler handler);

	void setJSONHandler(@Nullable IDuplexJSONHandler handler);

	void setBinaryHandler(@Nullable IDuplexBinaryHandler handler);

	long getAge();

	void setMaxAge(long millis);

	long getMaxAge();

	default boolean shouldKeepAlive() {
		return getMaxAge() > 0l;
	}

	void send(@Nonnull String string);

	void send(@Nonnull byte[] bytes);

	void sendResponse(@Nonnull String type, @Nonnull String str);

	void sendResponse(@Nonnull String type, @Nonnull JsonObject jsonObject);

}
