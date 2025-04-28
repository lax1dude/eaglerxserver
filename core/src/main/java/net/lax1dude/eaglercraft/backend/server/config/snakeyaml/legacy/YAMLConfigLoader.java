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

package net.lax1dude.eaglercraft.backend.server.config.snakeyaml.legacy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.BaseRepresenter;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class YAMLConfigLoader {

	private static final Yaml YAML;

	static {
		DumperOptions dumpOpts = new DumperOptions();
		dumpOpts.setPrettyFlow(true);
		dumpOpts.setDefaultFlowStyle(FlowStyle.FLOW);
		Representer representer;
		try {
			representer = Representer.class.getConstructor(DumperOptions.class).newInstance(dumpOpts);
		} catch (ReflectiveOperationException ex) {
			try {
				representer = Representer.class.getConstructor().newInstance();
			} catch (ReflectiveOperationException exx) {
				throw new ExceptionInInitializerError(exx);
			}
		}
		Field scalarStyleField = null;
		try {
			scalarStyleField = ScalarNode.class.getDeclaredField("style");
			if (scalarStyleField.getType() == Character.class) {
				scalarStyleField.setAccessible(true);
			} else {
				scalarStyleField = null;
			}
		} catch (ReflectiveOperationException ex) {
		}
		final Field scalarStyleFieldF = scalarStyleField;
		try {
			Field f = BaseRepresenter.class.getDeclaredField("multiRepresenters");
			f.setAccessible(true);
			((Map<Class<?>, Represent>) f.get(representer)).put(Node.class, new Represent() {
				@Override
				public Node representData(Object data) {
					if (scalarStyleFieldF != null) {
						LegacyHelper.fixScalars((Node) data, scalarStyleFieldF);
					}
					return (Node) data;
				}
			});
		} catch (ReflectiveOperationException exx) {
			throw new ExceptionInInitializerError(exx);
		}
		YAML = new Yaml(representer, dumpOpts);
	}

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		Node obj;
		try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			synchronized (YAML) {
				obj = YAML.compose(reader);
			}
		} catch (FileNotFoundException ex) {
			obj = null;
		} catch (YAMLException ex) {
			throw new IOException("YAML config file has a syntax error: " + file.getAbsolutePath(), ex);
		}
		if (obj == null) {
			obj = LegacyHelper.mappingNode(Tag.MAP, new ArrayList<>());
		}
		if (!(obj instanceof MappingNode obj2)) {
			throw new IOException("Root node " + obj.getClass().getSimpleName() + " is not a map!");
		}
		return getConfigFile(file, obj2);
	}

	public static IEaglerConfig getConfigFile(File file, MappingNode node) throws IOException {
		YAMLConfigBase base = new YAMLConfigBase(file);
		base.root = new YAMLConfigSection(base, node, node.getValue().size() > 0);
		return base;
	}

	public static void writeConfigFile(Node configIn, File file) throws IOException {
		File p = file.getAbsoluteFile().getParentFile();
		if (p != null && !p.isDirectory() && !p.mkdirs()) {
			throw new IOException("Could not create directory: " + p.getAbsolutePath());
		}
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			synchronized (YAML) {
				YAML.dump(configIn, writer);
			}
		}
	}

}
