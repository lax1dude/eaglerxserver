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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IMOTDConnection extends IEaglerConnection {

	@Nonnull
	String getAccept();

	@Nullable
	String getSubType();

	@Nonnull
	String getResponseType();

	void setResponseType(@Nonnull String type);

	void sendToUser();

	long getAge();

	void setMaxAge(long millis);

	long getMaxAge();

	default boolean shouldKeepAlive() {
		return getMaxAge() > 0l;
	}

	@Nullable
	byte[] getDefaultServerIcon();

	@Nullable
	byte[] getServerIcon();

	void setServerIcon(@Nullable byte[] bitmap);

	@Nonnull
	List<String> getDefaultServerMOTD();

	@Nonnull
	List<String> getServerMOTD();

	void setServerMOTD(@Nonnull List<String> motd);

	int getDefaultPlayerTotal();

	int getPlayerTotal();

	void setPlayerTotal(int total);

	int getDefaultPlayerMax();

	int getPlayerMax();

	void setPlayerMax(int total);

	default void setPlayerUnlimited() {
		setPlayerMax(-1);
	}

	@Nonnull
	List<String> getDefaultPlayerList();

	@Nonnull
	List<String> getPlayerList();

	void setPlayerList(@Nonnull List<String> list);

}
