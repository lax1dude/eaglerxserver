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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import net.lax1dude.eaglercraft.backend.server.config.ReflectUtil;

class LegacyHelper {

	private static final boolean HAS_FLOWSTYLE;
	private static final boolean HAS_SCALARSTYLE;

	private static final Constructor<MappingNode> ctor_MappingNode;
	private static final Constructor<SequenceNode> ctor_SequenceNode;
	private static final Object flowStyle;
	private static final Constructor<ScalarNode> ctor_ScalarNode;
	private static final Method meth_createStyle;

	static {
		Class<?> flowStyleClz = null;
		Class<?> scalarStyleClz = null;
		try {
			flowStyleClz = Class.forName("org.yaml.snakeyaml.DumperOptions$FlowStyle");
		} catch (ClassNotFoundException ex) {
		}
		try {
			scalarStyleClz = Class.forName("org.yaml.snakeyaml.DumperOptions$ScalarStyle");
		} catch (ClassNotFoundException ex) {
		}
		try {
			Constructor<MappingNode> _ctor_MappingNode = null;
			Constructor<SequenceNode> _ctor_SequenceNode = null;
			Object _flowStyle = null;
			if (flowStyleClz != null) {
				try {
					_ctor_MappingNode = MappingNode.class.getConstructor(Tag.class, List.class, flowStyleClz);
					_ctor_SequenceNode = SequenceNode.class.getConstructor(Tag.class, List.class, flowStyleClz);
					_flowStyle = Enum.valueOf((Class) flowStyleClz, "BLOCK");
				} catch (ReflectiveOperationException ex) {
					flowStyleClz = null;
				}
			}
			if (flowStyleClz == null) {
				_ctor_MappingNode = MappingNode.class.getConstructor(Tag.class, List.class, Boolean.class);
				_ctor_SequenceNode = SequenceNode.class.getConstructor(Tag.class, List.class, Boolean.class);
				_flowStyle = null;
			}
			Constructor<ScalarNode> _ctor_ScalarNode = null;
			Method _meth_createStyle = null;
			if (scalarStyleClz != null) {
				try {
					_ctor_ScalarNode = ScalarNode.class.getConstructor(Tag.class, String.class, Mark.class, Mark.class,
							scalarStyleClz);
					_meth_createStyle = scalarStyleClz.getMethod("createStyle", Character.class);
				} catch (ReflectiveOperationException ex) {
					scalarStyleClz = null;
				}
			}
			if (scalarStyleClz == null) {
				_ctor_ScalarNode = ScalarNode.class.getConstructor(Tag.class, String.class, Mark.class, Mark.class,
						Character.class);
				_meth_createStyle = null;
			}
			ctor_MappingNode = _ctor_MappingNode;
			ctor_SequenceNode = _ctor_SequenceNode;
			flowStyle = _flowStyle;
			ctor_ScalarNode = _ctor_ScalarNode;
			meth_createStyle = _meth_createStyle;
		} catch (ReflectiveOperationException ex) {
			throw new ExceptionInInitializerError(ex);
		}
		HAS_FLOWSTYLE = flowStyleClz != null;
		HAS_SCALARSTYLE = scalarStyleClz != null;
	}

	public static MappingNode mappingNode(Tag tag, List<NodeTuple> lst) {
		try {
			if (HAS_FLOWSTYLE) {
				return ctor_MappingNode.newInstance(tag, lst, flowStyle);
			} else {
				return ctor_MappingNode.newInstance(tag, lst, Boolean.FALSE);
			}
		} catch (ReflectiveOperationException ex) {
			throw ReflectUtil.propagateReflectThrowable(ex);
		}
	}

	public static SequenceNode sequenceNode(Tag tag, List<Node> lst) {
		try {
			if (HAS_FLOWSTYLE) {
				return ctor_SequenceNode.newInstance(tag, lst, flowStyle);
			} else {
				return ctor_SequenceNode.newInstance(tag, lst, Boolean.FALSE);
			}
		} catch (ReflectiveOperationException ex) {
			throw ReflectUtil.propagateReflectThrowable(ex);
		}
	}

	public static ScalarNode scalarNode(Tag tag, String value, Character style) {
		try {
			if (HAS_SCALARSTYLE) {
				return ctor_ScalarNode.newInstance(tag, value, null, null, meth_createStyle.invoke(null, style));
			} else {
				return ctor_ScalarNode.newInstance(tag, value, null, null, style);
			}
		} catch (ReflectiveOperationException ex) {
			throw ReflectUtil.propagateReflectThrowable(ex);
		}
	}

	public static void fixScalars(Node data, Field scalarStyle) {
		if (data instanceof ScalarNode s) {
			try {
				Character c = (Character) scalarStyle.get(s);
				if (c != null && (char) c == 0) {
					scalarStyle.set(s, null);
				}
			} catch (ReflectiveOperationException ex) {
			}
		} else if (data instanceof SequenceNode s) {
			for (Node n : s.getValue()) {
				fixScalars(n, scalarStyle);
			}
		} else if (data instanceof MappingNode s) {
			for (NodeTuple n : s.getValue()) {
				fixScalars(n.getKeyNode(), scalarStyle);
				fixScalars(n.getValueNode(), scalarStyle);
			}
		}
	}

}
