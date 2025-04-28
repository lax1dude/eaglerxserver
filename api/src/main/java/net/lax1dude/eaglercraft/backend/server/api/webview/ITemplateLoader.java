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

package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

public interface ITemplateLoader {

	@Nonnull
	File getBaseDir();

	void setBaseDir(@Nonnull File file);

	@Nonnull
	Map<String, String> getVariables();

	void setVariables(@Nonnull Map<String, String> map);

	void setVariable(@Nonnull String key, @Nullable String value);

	void removeVariable(@Nonnull String key);

	@Nullable
	ITranslationProvider getTranslations();

	void setTranslations(@Nullable ITranslationProvider translations);

	boolean isAllowEvalMacro();

	void setAllowEvalMacro(boolean enable);

	@Nonnull
	String loadWebViewTemplate(@Nonnull String template) throws IOException, InvalidMacroException;

	@Nonnull
	default String loadWebViewTemplate(@Nonnull byte[] template, @Nonnull Charset charset)
			throws IOException, InvalidMacroException {
		return loadWebViewTemplate(template, charset);
	}

	@Nonnull
	String loadWebViewTemplate(@Nonnull @WillNotClose Reader reader) throws IOException, InvalidMacroException;

	@Nonnull
	default String loadWebViewTemplate(@Nonnull @WillNotClose InputStream stream, @Nonnull Charset charset)
			throws IOException, InvalidMacroException {
		return loadWebViewTemplate(new InputStreamReader(stream, charset));
	}

	@Nonnull
	default String loadWebViewTemplate(@Nonnull File file, @Nonnull Charset charset)
			throws IOException, InvalidMacroException {
		try (InputStream is = new FileInputStream(file)) {
			return loadWebViewTemplate(is, charset);
		}
	}

	@Nonnull
	default String loadWebViewTemplate(@Nonnull String filename, @Nonnull Charset charset)
			throws IOException, InvalidMacroException {
		return loadWebViewTemplate(new File(getBaseDir(), filename), charset);
	}

}
