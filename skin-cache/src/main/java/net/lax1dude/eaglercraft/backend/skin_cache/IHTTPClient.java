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

import java.net.URI;
import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;

public interface IHTTPClient {

	public static class Response {

		public final int code;
		public final boolean redirected;
		public final ByteBuf data;
		public final Throwable exception;

		public Response(int code, boolean redirected, ByteBuf data) {
			this.code = code;
			this.redirected = redirected;
			this.data = data;
			this.exception = null;
		}

		public Response(Throwable exception) {
			this.code = -1;
			this.redirected = false;
			this.data = null;
			this.exception = exception;
		}

	}

	void asyncRequest(String method, URI uri, Consumer<Response> responseCallback);

}
