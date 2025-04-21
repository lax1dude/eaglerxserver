package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.CharStreams;

import net.lax1dude.eaglercraft.backend.server.api.webview.ITemplateLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.ITranslationProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.InvalidMacroException;

public class TemplateLoader implements ITemplateLoader {

	private static final File defaultDir = (new File(".")).getAbsoluteFile();

	private final WebViewService<?> owner;
	private File baseDir;
	private Map<String, String> variables;
	private ITranslationProvider translations;
	private boolean allowEval;

	TemplateLoader(WebViewService<?> owner, File baseDir, Map<String, String> variables,
			ITranslationProvider translations, boolean allowEval) {
		this.owner = owner;
		this.baseDir = baseDir != null ? baseDir : defaultDir;
		this.variables = variables;
		this.translations = translations;
		this.allowEval = allowEval;
	}

	@Override
	public File getBaseDir() {
		return baseDir;
	}

	@Override
	public void setBaseDir(File file) {
		if(file == null) {
			throw new NullPointerException("file");
		}
		baseDir = file;
	}

	@Override
	public Map<String, String> getVariables() {
		if(variables == null) {
			variables = new HashMap<>();
		}
		return variables;
	}

	@Override
	public void setVariables(Map<String, String> map) {
		variables = map;
	}

	@Override
	public void setVariable(String key, String value) {
		if(key == null) {
			throw new NullPointerException("key");
		}
		if(variables == null) {
			if(value == null) {
				return;
			}
			variables = new HashMap<>();
		}
		if(value != null) {
			variables.put(key, value);
		}else {
			variables.remove(key);
		}
	}

	@Override
	public void removeVariable(String key) {
		if(key == null) {
			throw new NullPointerException("key");
		}
		if(variables != null) {
			variables.remove(key);
		}
	}

	@Override
	public ITranslationProvider getTranslations() {
		return translations;
	}

	@Override
	public void setTranslations(ITranslationProvider tp) {
		translations = tp;
	}

	@Override
	public boolean isAllowEvalMacro() {
		return allowEval;
	}

	@Override
	public void setAllowEvalMacro(boolean enable) {
		allowEval = enable;
	}

	@Override
	public String loadWebViewTemplate(String template) throws IOException, InvalidMacroException {
		if(template == null) {
			throw new NullPointerException("template");
		}
		final Map<String, String> globals = owner.getTemplateGlobals();
		return TemplateParser.loadTemplate(template, baseDir, allowEval, (key) -> {
			if(variables != null) {
				String str = variables.get(key);
				if(str != null) {
					return str;
				}
			}
			return globals.get(key);
		}, translations);
	}

	@Override
	public String loadWebViewTemplate(Reader reader) throws IOException, InvalidMacroException {
		if(reader == null) {
			throw new NullPointerException("reader");
		}
		return loadWebViewTemplate(CharStreams.toString(reader));
	}

}
