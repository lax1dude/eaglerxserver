package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jline.utils.InputStreamReader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class YAMLConfigLoader {

	private static final Yaml YAML = new Yaml();

	public static final int YAML_COMMENT_WRAP = 80;

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		Node obj;
		try(Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			obj = YAML.compose(reader);
		}catch(YAMLException ex) {
			throw new IOException("YAML config file has a syntax error: " + file.getAbsolutePath(), ex);
		}
		return getConfigFile(obj);
	}

	public static IEaglerConfig getConfigFile(Node node) throws IOException {
		return null;
	}

	public static void writeConfigFile(Node configIn, File file) throws IOException {
		try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			YAML.dump(file, writer);
		}
	}

	public static void createComment(String text, List<CommentLine> ret) {
		if(text != null) {
			String[] lines = wrap(text, YAML_COMMENT_WRAP, "\n", false, " ").split("\n");
			for(int i = 0; i < lines.length; ++i) {
				ret.add(new CommentLine(null, null, lines[i], CommentType.BLOCK));
			}
		}
	}

	// From an old version of apache commons lang3
	private static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords,
			String wrapOn) {
		if (str == null) {
			return null;
		}
		if (newLineStr == null) {
			newLineStr = System.lineSeparator();
		}
		if (wrapLength < 1) {
			wrapLength = 1;
		}
		final Pattern patternToWrapOn = Pattern.compile(wrapOn);
		final int inputLineLength = str.length();
		int offset = 0;
		final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

		while (offset < inputLineLength) {
			int spaceToWrapAt = -1;
			Matcher matcher = patternToWrapOn.matcher(str.substring(offset,
					Math.min((int) Math.min(Integer.MAX_VALUE, offset + wrapLength + 1L), inputLineLength)));
			if (matcher.find()) {
				if (matcher.start() == 0) {
					offset += matcher.end();
					continue;
				}
				spaceToWrapAt = matcher.start() + offset;
			}

			// only last line without leading spaces is left
			if (inputLineLength - offset <= wrapLength) {
				break;
			}

			while (matcher.find()) {
				spaceToWrapAt = matcher.start() + offset;
			}

			if (spaceToWrapAt >= offset) {
				// normal case
				wrappedLine.append(str, offset, spaceToWrapAt);
				wrappedLine.append(newLineStr);
				offset = spaceToWrapAt + 1;

			} else // really long word or URL
			if (wrapLongWords) {
				// wrap really long word one line at a time
				wrappedLine.append(str, offset, wrapLength + offset);
				wrappedLine.append(newLineStr);
				offset += wrapLength;
			} else {
				// do not wrap really long word, just extend beyond limit
				matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
				if (matcher.find()) {
					spaceToWrapAt = matcher.start() + offset + wrapLength;
				}

				if (spaceToWrapAt >= 0) {
					wrappedLine.append(str, offset, spaceToWrapAt);
					wrappedLine.append(newLineStr);
					offset = spaceToWrapAt + 1;
				} else {
					wrappedLine.append(str, offset, str.length());
					offset = inputLineLength;
				}
			}
		}

		// Whatever is left in line is short enough to just pass through
		wrappedLine.append(str, offset, str.length());

		return wrappedLine.toString();
	}

}
