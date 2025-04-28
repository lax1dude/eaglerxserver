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

package net.lax1dude.eaglercraft.backend.server.api.event;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;

public interface IEaglercraftRevokeSessionQueryEvent<PlayerObject> extends IBaseServerEvent<PlayerObject> {

	public static enum EnumSessionRevokeStatus {
		SUCCESS("ok", -1), FAILED_NOT_SUPPORTED("error", 1), FAILED_NOT_ALLOWED("error", 2),
		FAILED_NOT_FOUND("error", 3), FAILED_SERVER_ERROR("error", 4);

		public final String status;
		public final int code;

		private EnumSessionRevokeStatus(String status, int code) {
			this.status = status;
			this.code = code;
		}
	}

	@Nonnull
	IQueryConnection getSocket();

	@Nonnull
	byte[] getCookieData();

	@Nonnull
	default String getCookieDataString() {
		return new String(getCookieData(), StandardCharsets.UTF_8);
	}

	@Nonnull
	EnumSessionRevokeStatus getResultStatus();

	void setResultStatus(@Nonnull EnumSessionRevokeStatus revokeStatus);

	boolean getShouldDeleteCookie();

	void setShouldDeleteCookie(boolean flag);

}
