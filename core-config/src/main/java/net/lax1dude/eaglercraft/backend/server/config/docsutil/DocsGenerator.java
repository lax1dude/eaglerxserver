/*
 * Copyright (c) 2026 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.server.config.docsutil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocsGenerator {

	private final String title;
	private final String desc;
	private final Map<String, DocsDirectory> map = new HashMap<>();
	private String primaryFile;

	public DocsGenerator(String title, String desc) {
		this.title = title;
		this.desc = desc;
	}

	public void addPlatform(String platform, IDocDirLoader provider) throws IOException {
		DocsDirectory directory = map.get(platform);
		if (directory == null) {
			map.put(platform, directory = new DocsDirectory());
		}
		provider.call(directory);
	}

	public interface IDocDirLoader {
		void call(DocsDirectory dir) throws IOException;
	}

	private static void flatten(Map<String, Header> headers, Map<String, Content> content, String platf, String pfx,
			String title, DocsSection section) {
		for (Map.Entry<String, Object> etr : section.entries.entrySet()) {
			if (etr.getValue() instanceof DocsValue v) {
				flatten(content, platf, pfx + etr.getKey(), title + "`" + etr.getKey() + "`", v);
			} else if (etr.getValue() instanceof DocsSection v) {
				Header headerObj = new Header(title + "`" + etr.getKey() + "`");
				headerObj.platforms.add(platf);
				if (v.comment != null && !v.comment.isEmpty()) {
					Summary summaryObj = new Summary(v.comment);
					summaryObj.platforms.add(platf);
					headerObj.summary.add(summaryObj);
				}
				merge(headers, pfx + etr.getKey(), headerObj);
				flatten(headers, content, platf, pfx + etr.getKey() + ":", title + "`" + etr.getKey() + "` : ", v);
			} else if (etr.getValue() instanceof DocsList v) {
				flatten(headers, content, platf, pfx + etr.getKey(), title + "`" + etr.getKey() + "`", v);
			} else {
				throw new IllegalStateException();
			}
		}
	}

	private static void flatten(Map<String, Header> headers, Map<String, Content> content, String platf, String pfx,
			String title, DocsList section) {
		boolean hasHeader = false;
		List<String> values = new ArrayList<>();
		for (Object etr : section.entries) {
			if (etr instanceof DocsValue v) {
				values.add(v.value == null ? "null" :
					v.type != DocsValue.Type.STR ? v.value :
					("\"" + v.value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""));
			} else if (etr instanceof DocsSection v) {
				int idx = title.lastIndexOf(": ");
				if (idx == -1) {
					idx = title.lastIndexOf("&gt; ") + 3;
				}
				String t2 = title.substring(0, idx + 2) + "[" + title.substring(idx + 2) + "]";
				flatten(headers, content, platf, pfx + ":{}:", t2 + " : ", v);
				hasHeader = true;
			} else if (etr instanceof DocsList v) {
				int idx = title.lastIndexOf(": ");
				if (idx == -1) {
					idx = title.lastIndexOf("&gt; ") + 3;
				}
				String t2 = title.substring(0, idx + 2) + "[" + title.substring(idx + 2) + "]";
				flatten(headers, content, platf, pfx + ":[]:", t2, v);
				hasHeader = true;
			} else {
				throw new IllegalStateException();
			}
		}
		if (hasHeader) {
			int idx = title.lastIndexOf(": ");
			if (idx == -1) {
				idx = title.lastIndexOf("&gt; ") + 3;
			}
			String t2 = title.substring(0, idx + 2) + "[" + title.substring(idx + 2) + "]";
			Header headerObj = new Header(t2);
			headerObj.platforms.add(platf);
			if (section.comment != null && !section.comment.isEmpty()) {
				Summary summaryObj = new Summary(section.comment);
				summaryObj.platforms.add(platf);
				headerObj.summary.add(summaryObj);
			}
			merge(headers, pfx, headerObj);
		} else {
			Content contentObj = new Content(title);
			contentObj.platforms.add(platf);
			if (section.comment != null && !section.comment.isEmpty()) {
				Summary summaryObj = new Summary(section.comment);
				summaryObj.platforms.add(platf);
				contentObj.summary.add(summaryObj);
			}
			Defaults defaultsObj = new Defaults("[ " + String.join(", ", values) + " ]");
			defaultsObj.platforms.add(platf);
			contentObj.defaults.add(defaultsObj);
			merge(content, pfx, contentObj);
		}
	}

	private static void flatten(Map<String, Content> content, String platf, String pfx, String title,
			DocsValue value) {
		Content contentObj = new Content(title);
		contentObj.platforms.add(platf);
		if (value.comment != null && !value.comment.isEmpty()) {
			Summary summaryObj = new Summary(value.comment);
			summaryObj.platforms.add(platf);
			contentObj.summary.add(summaryObj);
		}
		if (!value.randomized) {
			Defaults defaultsObj = new Defaults(
				value.value == null ? "null" :
				value.type != DocsValue.Type.STR ? value.value :
				("\"" + value.value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""));
			defaultsObj.platforms.add(platf);
			contentObj.defaults.add(defaultsObj);
		}
		merge(content, pfx, contentObj);
	}

	private static void merge(Map<String, Header> headers, String pfx, Header item) {
		Header existing = headers.get(pfx);
		if (existing == null) {
			headers.put(pfx, item);
			return;
		}
		for (String str : item.platforms) {
			if (!existing.platforms.contains(str)) {
				existing.platforms.add(str);
			}
		}
		eag: for (Summary str1 : item.summary) {
			for (Summary str2 : existing.summary) {
				if (str1.summary.equals(str2.summary)) {
					for (String str : str1.platforms) {
						if (!str2.platforms.contains(str)) {
							str2.platforms.add(str);
						}
					}
					continue eag;
				}
			}
			existing.summary.add(str1);
		}
	}

	private static void merge(Map<String, Content> content, String id, Content item) {
		Content existing = content.get(id);
		if (existing == null) {
			content.put(id, item);
			return;
		}
		for (String str : item.platforms) {
			if (!existing.platforms.contains(str)) {
				existing.platforms.add(str);
			}
		}
		eag: for (Summary str1 : item.summary) {
			for (Summary str2 : existing.summary) {
				if (str1.summary.equals(str2.summary)) {
					for (String str : str1.platforms) {
						if (!str2.platforms.contains(str)) {
							str2.platforms.add(str);
						}
					}
					continue eag;
				}
			}
			existing.summary.add(str1);
		}
		eag: for (Defaults str1 : item.defaults) {
			for (Defaults str2 : existing.defaults) {
				if (str1.defaults.equals(str2.defaults)) {
					for (String str : str1.platforms) {
						if (!str2.platforms.contains(str)) {
							str2.platforms.add(str);
						}
					}
					continue eag;
				}
			}
			existing.defaults.add(str1);
		}
	}

	public void writeDocs(PrintWriter writer) {
		List<String> sortedPlatforms = new ArrayList<>(map.keySet());
		Collections.sort(sortedPlatforms);
		Map<String, Header> headers = new HashMap<>();
		Map<String, Content> sections = new HashMap<>();

		for (String platfName : sortedPlatforms) {
			DocsDirectory platf = map.get(platfName);
			for (Map.Entry<String, DocsSection> dir : platf.map.entrySet()) {
				flatten(headers, sections, platfName, dir.getKey() + ">",
						"`/" + dir.getKey() + ".cfg` &gt; ", dir.getValue());
			}
		}

		List<Map.Entry<String, Content>> sortedSections = new ArrayList<>(sections.entrySet());
		Collections.sort(sortedSections, Map.Entry.comparingByKey());

		if (primaryFile != null) {
			String pfx = primaryFile + ">";
			List<Map.Entry<String, Content>> tmp = new ArrayList<>(sortedSections.size());
			for (Map.Entry<String, Content> etr : sortedSections) {
				if (etr.getKey().startsWith(pfx)) {
					tmp.add(etr);
				}
			}
			for (Map.Entry<String, Content> etr : sortedSections) {
				if (!etr.getKey().startsWith(pfx)) {
					tmp.add(etr);
				}
			}
			sortedSections = tmp;
		}

		writer.println("## " + title);
		for (String str : desc.split("(\\r\\n|\\r|\\n)")) {
			writer.println(str);
		}

		writer.println();
		writer.println("*(Placeholder extension \".cfg\" replaced with \".yaml\", \".toml\", or \".gson\")*");

		String vigg = null;
		for (Map.Entry<String, Content> etr : sortedSections) {
			String key = etr.getKey();
			int idx = key.lastIndexOf(':');
			if (idx != -1 && (vigg == null || vigg.length() != idx || !vigg.regionMatches(0, key, 0, idx))) {
				vigg = key.substring(0, idx);
				idx = -1;
				while ((idx = key.indexOf(':', idx + 1)) != -1) {
					Header head = headers.remove(key.substring(0, idx));
					if (head != null) {
						writer.println();
						writer.println("## " + head.title + "&emsp;<sub>(" + String.join(", ", head.platforms) + ")</sub>");
						if (!head.summary.isEmpty()) {
							writer.println("**Summary:**");
							for (Summary sum : head.summary) {
								writer.println("- " + sum.summary + "&emsp;<sub>(" + String.join(", ", sum.platforms) + ")</sub>");
							}
						}
					}
				}
			}
			Content sec = etr.getValue();
			writer.println();
			writer.println("## <small>" + sec.title + "&emsp;<sub>(" + String.join(", ", sec.platforms) + ")</sub></small>");
			if (!sec.summary.isEmpty()) {
				writer.println("**Summary:**");
				for (Summary sum : sec.summary) {
					writer.println("- " + sum.summary + "&emsp;<sub>(" + String.join(", ", sum.platforms) + ")</sub>");
				}
			}
			if (!sec.defaults.isEmpty()) {
				if (!sec.summary.isEmpty()) {
					writer.println();
				}
				writer.println("**Defaults:**");
				for (Defaults def : sec.defaults) {
					writer.println("- `" + def.defaults + "`&emsp;<sub>(" + String.join(", ", def.platforms) + ")</sub>");
				}
			}
		}

		writer.flush();
	}

	public void setPrimaryFile(String primaryFile) {
		this.primaryFile = primaryFile;
	}

	private static class Header {

		private final String title;
		private final List<String> platforms;
		private final List<Summary> summary;

		private Header(String title) {
			this.title = title;
			this.platforms = new ArrayList<>();
			this.summary = new ArrayList<>();
		}

	}

	private static class Content {

		private final String title;
		private final List<String> platforms;
		private final List<Summary> summary;
		private final List<Defaults> defaults;

		private Content(String title) {
			this.title = title;
			this.platforms = new ArrayList<>();
			this.summary = new ArrayList<>();
			this.defaults = new ArrayList<>();
		}

	}

	private static class Summary {

		private final String summary;
		private final List<String> platforms;

		private Summary(String summary) {
			this.summary = summary;
			this.platforms = new ArrayList<>();
		}

	}

	private static class Defaults {

		private final String defaults;
		private final List<String> platforms;

		private Defaults(String defaults) {
			this.defaults = defaults;
			this.platforms = new ArrayList<>();
		}

	}

}
