/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

import com.google.common.html.HtmlEscapers;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.lax1dude.eaglercraft.backend.server.api.webview.ITranslationProvider;
import net.lax1dude.eaglercraft.backend.server.api.webview.InvalidMacroException;
import net.lax1dude.eaglercraft.backend.server.util.StrTokenizer;

class TemplateParser {

	private static final Gson jsonEscaper = (new GsonBuilder()).disableHtmlEscaping().create();

	private static class State {
		private boolean evalAllowed;
		private File baseDir;
		private Function<String, String> globals;
		private ITranslationProvider translations;
		private boolean htmlEscape;
		private boolean strEscape;
		private boolean disableMacros;
		private boolean enableEval;

		private State(File baseDir, boolean evalAllowed, Function<String, String> globals,
				ITranslationProvider translations) {
			this.baseDir = baseDir;
			this.evalAllowed = evalAllowed;
			this.globals = globals;
			this.translations = translations;
		}

		private State push() {
			return new State(baseDir, evalAllowed, globals, translations);
		}
	}

	static String loadTemplate(String content, File baseDir, boolean evalAllowed, Function<String, String> globals,
			ITranslationProvider translations) throws IOException {
		return loadTemplate(content, new State(baseDir, evalAllowed, globals, translations));
	}

	private static String loadTemplate(String content, State state) throws IOException {
		StringBuilder ret = new StringBuilder();
		int i = 0, j = 0;
		while ((i = content.indexOf("{%", j)) != -1) {
			ret.append(content, j, i);
			j = i;
			i = content.indexOf("%}", j + 2);
			if (i != -1) {
				ret.append(processMacro(content.substring(j + 2, i), state));
				j = i + 2;
			} else {
				break;
			}
		}
		ret.append(content, j, content.length());
		return ret.toString();
	}

	private static String processMacro(String content, State state) throws IOException {
		String trimmed = content.trim();
		try {
			String[] strs = (new StrTokenizer(trimmed, ' ', '`')).getTokenArray();
			if (strs.length < 1) {
				return "{%" + content + "%}";
			}
			if (strs[0].equals("disablemacros") && strs.length == 2) {
				switch (strs[1]) {
				case "on":
					if (state.disableMacros) {
						return "{%" + content + "%}";
					} else {
						state.disableMacros = true;
						return "";
					}
				case "off":
					state.disableMacros = false;
					return "";
				default:
					if (state.disableMacros) {
						return "{%" + content + "%}";
					} else {
						throw new InvalidMacroException(
								"Unknown disablemacros mode: " + strs[1] + " (Expected: on, off)");
					}
				}
			} else if (!state.disableMacros) {
				switch (strs[0]) {
				case "embed":
					argCheck(3, strs.length);
					switch (strs[1]) {
					case "base64":
						return Base64.getEncoder()
								.encodeToString(loadFileToByteArray(new File(state.baseDir, strs[2])));
					case "text":
						return escapeMacroResult(loadFileToString(new File(state.baseDir, strs[2])), state);
					case "eval":
						if (state.evalAllowed) {
							return escapeMacroResult(
									loadTemplate(loadFileToString(new File(state.baseDir, strs[2])), state.push()),
									state);
						} else {
							throw new InvalidMacroException(
									"Template tried to eval file \"" + strs[2] + "\"! (eval is disabled)");
						}
					default:
						throw new InvalidMacroException(
								"Unknown embed mode: " + strs[1] + " (Expected: base64, text, eval)");
					}
				case "htmlescape":
					argCheck(2, strs.length);
					switch (strs[1]) {
					case "on":
						state.htmlEscape = true;
						return "";
					case "off":
						state.htmlEscape = false;
						return "";
					default:
						throw new InvalidMacroException("Unknown htmlescape mode: " + strs[1] + " (Expected: on, off)");
					}
				case "strescape":
					argCheck(2, strs.length);
					switch (strs[1]) {
					case "on":
						state.strEscape = true;
						return "";
					case "off":
						state.strEscape = false;
						return "";
					default:
						throw new InvalidMacroException("Unknown strescape mode: " + strs[1] + " (Expected: on, off)");
					}
				case "eval":
					argCheck(2, strs.length);
					switch (strs[1]) {
					case "on":
						if (!state.evalAllowed) {
							throw new InvalidMacroException("Template tried to enable eval! (eval is disabled)");
						}
						state.enableEval = true;
						return "";
					case "off":
						state.enableEval = false;
						return "";
					default:
						throw new InvalidMacroException("Unknown eval mode: " + strs[1] + " (Expected: on, off)");
					}
				case "global":
					argCheck(2, 3, strs.length);
					String ret = state.globals.apply(strs[1]);
					if (ret == null) {
						if (strs.length == 3) {
							ret = strs[2];
						} else {
							throw new InvalidMacroException("Unknown global \"" + strs[1] + "\"!");
						}
					}
					return escapeMacroResult(ret, state);
				case "property":
					argCheck(2, 3, strs.length);
					ret = System.getProperty(strs[1]);
					if (ret == null) {
						if (strs.length == 3) {
							ret = strs[2];
						} else {
							throw new InvalidMacroException("Unknown system property \"" + strs[1] + "\"!");
						}
					}
					return escapeMacroResult(ret, state);
				case "text":
					argCheck(2, strs.length);
					return escapeMacroResult(strs[1], state);
				case "translate":
					argCheckMin(2, strs.length);
					if (state.translations != null) {
						String[] args = new String[strs.length - 2];
						System.arraycopy(strs, 2, args, 0, args.length);
						String res = state.translations.format(strs[1], args);
						return escapeMacroResult(res != null ? res : strs[1], state);
					} else {
						return escapeMacroResult(strs[1], state);
					}
				default:
					return "{%" + content + "%}";
				}
			} else {
				return "{%" + content + "%}";
			}
		} catch (InvalidMacroException ex) {
			throw new IOException("Invalid macro: {% " + trimmed + " %}, message: " + ex.getMessage(), ex);
		} catch (Throwable th) {
			throw new IOException("Error processing: {% " + trimmed + " %}, raised: " + th.toString(), th);
		}
	}

	private static String loadFileToString(File file) throws IOException {
		try (Reader is = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			return CharStreams.toString(is);
		}
	}

	private static byte[] loadFileToByteArray(File file) throws IOException {
		try (FileInputStream is = new FileInputStream(file)) {
			return ByteStreams.toByteArray(is);
		}
	}

	private static String escapeMacroResult(String str, State state) throws IOException {
		if (str.length() > 0) {
			if (state.evalAllowed && state.enableEval) {
				str = loadTemplate(str, state.push());
			}
			if (state.strEscape) {
				str = jsonEscaper.toJson(str);
				if (str.length() >= 2) {
					str = str.substring(1, str.length() - 1);
				}
			}
			if (state.htmlEscape) {
				str = HtmlEscapers.htmlEscaper().escape(str);
			}
		}
		return str;
	}

	private static void argCheck(int expect, int actual) {
		if (expect != actual) {
			throw new InvalidMacroException("Wrong number of arguments (" + actual + ", expected " + expect + ")");
		}
	}

	private static void argCheck(int expectMin, int expectMax, int actual) {
		if (expectMin > actual || expectMax < actual) {
			throw new InvalidMacroException(
					"Wrong number of arguments (" + actual + ", expected " + expectMin + " to " + expectMax + ")");
		}
	}

	private static void argCheckMin(int expectMin, int actual) {
		if (expectMin > actual) {
			throw new InvalidMacroException(
					"Wrong number of arguments (expected " + expectMin + " or more, got " + actual + ")");
		}
	}

}