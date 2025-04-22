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
