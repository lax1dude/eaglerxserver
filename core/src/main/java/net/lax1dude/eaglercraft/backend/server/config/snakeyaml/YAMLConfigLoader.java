package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;
import net.lax1dude.eaglercraft.backend.server.config.WrapUtil;

public class YAMLConfigLoader {

	private static final Yaml YAML;

	static {
		LoaderOptions loadOpts = new LoaderOptions();
		loadOpts.setProcessComments(true);
		DumperOptions dumpOpts = new DumperOptions();
		dumpOpts.setPrettyFlow(true);
		dumpOpts.setDefaultFlowStyle(FlowStyle.FLOW);
		dumpOpts.setProcessComments(true);
		YAML = new Yaml(new Constructor(loadOpts), new Representer(dumpOpts), dumpOpts);
	}

	public static final int YAML_COMMENT_WRAP = 80;

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		Node obj;
		try(Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
			obj = YAML.compose(reader);
		}catch(FileNotFoundException ex) {
			obj = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.BLOCK);
		}catch(YAMLException ex) {
			throw new IOException("YAML config file has a syntax error: " + file.getAbsolutePath(), ex);
		}
		if(!(obj instanceof MappingNode obj2)) {
			throw new IOException("Root node " + obj.getClass().getSimpleName() + " is not a map!");
		}
		return getConfigFile(file, obj2);
	}

	public static IEaglerConfig getConfigFile(File file, MappingNode node) throws IOException {
		YAMLConfigBase base = new YAMLConfigBase(file);
		base.root = new YAMLConfigSection(base, node, null, node.getValue().size() > 0);
		return base;
	}

	public static void writeConfigFile(Node configIn, File file) throws IOException {
		File p = file.getAbsoluteFile().getParentFile();
		if(p != null && !p.isDirectory() && !p.mkdirs()) {
			throw new IOException("Could not create directory: " + p.getAbsolutePath());
		}
		try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			YAML.serialize(configIn, writer);
		}
	}

	public static void createComment(String text, List<CommentLine> ret) {
		if(text != null) {
			String[] lines = WrapUtil.wrap(text, YAML_COMMENT_WRAP, "\n", false, " ").split("\n");
			for(int i = 0; i < lines.length; ++i) {
				ret.add(new CommentLine(null, null, " " + lines[i], CommentType.BLOCK));
			}
		}
	}

	public static void createCommentHelper(String text, ScalarNode ret) {
		List<CommentLine> lst = ret.getBlockComments();
		if(lst == null) {
			lst = new ArrayList<>();
			ret.setBlockComments(lst);
		}else {
			lst.clear();
		}
		createComment(text, lst);
	}

}
