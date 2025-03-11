package net.lax1dude.eaglercraft.backend.server.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WrapUtil {

	// From an old version of apache commons lang3
	public static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords,
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
