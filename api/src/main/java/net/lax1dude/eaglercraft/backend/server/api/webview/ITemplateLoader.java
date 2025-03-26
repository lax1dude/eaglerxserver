package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

public interface ITemplateLoader {

	File getBaseDir();

	void setBaseDir(File file);

	Map<String, String> getVariables();

	void setVariables(Map<String, String> map);

	void setVariable(String key, String value);

	ITranslationProvider getTranslations();

	void setTranslations(ITranslationProvider translations);

	boolean isAllowEvalMacro();

	void setAllowEvalMacro(boolean enable);

	String loadWebViewTemplate(String template) throws IOException, InvalidMacroException;

	default String loadWebViewTemplate(byte[] template, Charset charset) throws IOException, InvalidMacroException {
		return loadWebViewTemplate(template, charset);
	}

	String loadWebViewTemplate(Reader reader) throws IOException, InvalidMacroException;

	default String loadWebViewTemplate(InputStream stream, Charset charset) throws IOException, InvalidMacroException {
		return loadWebViewTemplate(new InputStreamReader(stream, charset));
	}

	default String loadWebViewTemplate(File file, Charset charset) throws IOException, InvalidMacroException {
		try(InputStream is = new FileInputStream(file)) {
			return loadWebViewTemplate(is, charset);
		}
	}

	default String loadWebViewTemplate(String filename, Charset charset) throws IOException, InvalidMacroException {
		return loadWebViewTemplate(new File(getBaseDir(), filename), charset);
	}

}
