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

package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;

public interface IWebServer {

	void registerRoute(@Nonnull Object plugin, @Nonnull RouteDesc route, @Nonnull IRequestHandler requestHandler);

	void unregisterRoute(@Nonnull Object plugin, @Nonnull RouteDesc route);

	void unregisterRoutes(@Nonnull Object plugin);

	@Nonnull
	IRequestHandler resolve(@Nonnull IEaglerListenerInfo listener, @Nonnull EnumRequestMethod method,
			@Nonnull CharSequence path);

	@Nonnull
	IRequestHandler getDefault404Handler();

	@Nonnull
	IRequestHandler get404Handler();

	@Nonnull
	IRequestHandler getDefault429Handler();

	@Nonnull
	IRequestHandler get429Handler();

	@Nonnull
	IRequestHandler getDefault500Handler();

	@Nonnull
	IRequestHandler get500Handler();

	@Nonnull
	IPreparedResponse prepareResponse(@Nonnull @WillNotClose InputStream data) throws IOException;

	@Nonnull
	IPreparedResponse prepareResponse(@Nonnull byte[] data);

	@Nonnull
	IPreparedResponse prepareResponse(@Nonnull CharSequence data, @Nonnull Charset binaryCharset);

}
