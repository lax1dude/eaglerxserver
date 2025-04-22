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

package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public class RouteMap<L, T> {

	public static final int numMeths = 6;

	private final RouteTreeNode<L, T> rootNode = new RouteTreeNode<L, T>(null);

	private static class RouteTreeNode<L, T> {

		protected final RouteTreeNode<L, T> parent;

		protected RouteTreeNode(RouteTreeNode<L, T> parent) {
			this.parent = parent;
		}

		protected Map<String, RouteTreeNode<L, T>> children;
		protected RouteTreeNode<L, T> defaultChild;
		protected boolean isDefaultChild;

		protected IRouteEndpoint<L, T> endpoint;
		protected IRouteEndpoint<L, T> endpointDir;

		protected RouteTreeNode<L, T> find(Iterator<CharSequence> tokens, boolean dir) {
			if(tokens.hasNext()) {
				CharSequence n = tokens.next();
				if(children != null) {
					RouteTreeNode<L, T> r = children.get(n);
					if(r != null) {
						return r.find(tokens, dir);
					}
				}
				if(defaultChild != null) {
					RouteTreeNode<L, T> r = defaultChild.find(tokens, dir);
					if(r != null) {
						return r;
					}
				}
				if(isDefaultChild) {
					return this;
				}
				return null;
			}else {
				return this;
			}
		}

		protected RouteTreeNode<L, T> getOrCreateChild(String name) {
			RouteTreeNode<L, T> r;
			if("*".equals(name)) {
				r = defaultChild;
				if(r == null) {
					defaultChild = r = new RouteTreeNode<>(this);
					r.isDefaultChild = true;
				}
			}else {
				if(children == null) {
					r = null;
					children = new HashMap<>();
				}else {
					r = children.get(name);
				}
				if(r == null) {
					children.put(name, r = new RouteTreeNode<>(this));
				}
			}
			return r;
		}

		protected final IRouteEndpoint<L, T> getEndpoint(boolean dir) {
			return dir ? endpointDir : endpoint;
		}

		protected final void setEndpoint(boolean dir, IRouteEndpoint<L, T> val) {
			if(dir) {
				endpointDir = val;
			}else {
				endpoint = val;
			}
		}

	}

	private static abstract class IRouteEndpoint<L, T> {

		protected abstract IRouteMethods<T> getForListener(L ls);

		protected abstract boolean allListener();

	}

	private static abstract class IRouteMethods<T> {

		protected abstract T getForMethod(int methId);

		protected abstract boolean allMethod();

	}

	private static class RouteEndpointAllListener<L, T> extends IRouteEndpoint<L, T> {

		protected final IRouteMethods<T> method;

		protected RouteEndpointAllListener(IRouteMethods<T> method) {
			this.method = method;
		}

		@Override
		public IRouteMethods<T> getForListener(L ls) {
			return method;
		}

		@Override
		protected boolean allListener() {
			return true;
		}

	}

	private static class RouteEndpointPerListener<L, T> extends IRouteEndpoint<L, T> {

		protected final Map<L, IRouteMethods<T>> entries;

		protected RouteEndpointPerListener() {
			this.entries = new HashMap<>(4);
		}

		@Override
		public IRouteMethods<T> getForListener(L ls) {
			return entries.get(ls);
		}

		@Override
		protected boolean allListener() {
			return false;
		}

	}

	private static class RouteMethodAllMethods<T> extends IRouteMethods<T> {

		private final T obj;

		protected RouteMethodAllMethods(T obj) {
			this.obj = obj;
		}

		@Override
		public T getForMethod(int methBit) {
			return obj;
		}

		@Override
		protected boolean allMethod() {
			return true;
		}

	}

	private static class RouteMethodPerMethod<T> extends IRouteMethods<T> {

		private final T[] obj;
		private int count;

		@SuppressWarnings("unchecked")
		protected RouteMethodPerMethod() {
			this.obj = (T[]) new Object[numMeths];
		}

		@Override
		public T getForMethod(int methId) {
			return obj[methId];
		}

		@Override
		protected boolean allMethod() {
			return false;
		}

	}

	public boolean register(Iterator<CharSequence> tokens, boolean dir, L listener, int methId, T value) {
		RouteTreeNode<L, T> path = rootNode;
		while(tokens.hasNext()) {
			path = path.getOrCreateChild(tokens.next().toString());
		}
		IRouteEndpoint<L, T> endpoint = path.getEndpoint(dir);
		if(listener == null) {
			if(endpoint == null) {
				path.setEndpoint(dir, new RouteEndpointAllListener<>(boostrapMethods(methId, value)));
				return true;
			}else if(endpoint.allListener()) {
				if(methId != -1) {
					IRouteMethods<T> method = endpoint.getForListener(null);
					if(method instanceof RouteMethodPerMethod<T> meth) {
						return addMethod(meth, methId, value);
					}else {
						return false;
					}
				}else {
					return false;
				}
			}else {
				return false;
			}
		}else {
			if(endpoint == null) {
				RouteEndpointPerListener<L, T> tmp1 = new RouteEndpointPerListener<>();
				tmp1.entries.put(listener, boostrapMethods(methId, value));
				path.setEndpoint(dir, tmp1);
				return true;
			}else if(endpoint instanceof RouteEndpointPerListener<L, T> tmp1) {
				IRouteMethods<T> method = tmp1.entries.get(listener);
				if(method != null) {
					if(methId != -1) {
						if(method instanceof RouteMethodPerMethod<T> meth) {
							return addMethod(meth, methId, value);
						}else {
							return false;
						}
					}else {
						return false;
					}
				}else {
					tmp1.entries.put(listener, boostrapMethods(methId, value));
					return true;
				}
			}else {
				return false;
			}
		}
	}

	private IRouteMethods<T> boostrapMethods(int methId, T value) {
		if(methId != -1) {
			RouteMethodPerMethod<T> tmp = new RouteMethodPerMethod<>();
			tmp.obj[methId] = value;
			++tmp.count;
			return tmp;
		}else {
			return new RouteMethodAllMethods<>(value);
		}
	}

	private boolean addMethod(RouteMethodPerMethod<T> meth, int methId, T value) {
		if(meth.obj[methId] == null) {
			meth.obj[methId] = value;
			++meth.count;
			return true;
		}else {
			return false;
		}
	}

	public boolean remove(Iterator<CharSequence> tokens, boolean dir, L listener, int methId, T value) {
		RouteTreeNode<L, T> endpointNode = rootNode.find(tokens, dir);
		if(endpointNode == null) {
			return false;
		}
		IRouteEndpoint<L, T> endpoint = endpointNode.getEndpoint(dir);
		if(endpoint == null) {
			return false;
		}else {
			if(listener == null) {
				if(endpoint instanceof RouteEndpointAllListener<L, T> tmp) {
					if(methId != -1) {
						if(tmp.method instanceof RouteMethodPerMethod<T> tmp2) {
							if(tmp2.obj[methId] == value) {
								tmp2.obj[methId] = null;
								if(--tmp2.count == 0) {
									deleteEndpoint(endpointNode, dir);
								}
								return true;
							}else {
								return false;
							}
						}else {
							return false;
						}
					}else {
						if(tmp.method instanceof RouteMethodAllMethods<T> tmp2) {
							if(tmp2.obj == value) {
								deleteEndpoint(endpointNode, dir);
								return true;
							}else {
								return false;
							}
						}else {
							return false;
						}
					}
				}else {
					return false;
				}
			}else {
				if(endpoint instanceof RouteEndpointPerListener<L, T> tmp) {
					IRouteMethods<T> method = tmp.entries.get(listener);
					if(method != null) {
						if(methId != -1) {
							if(method instanceof RouteMethodPerMethod<T> tmp2) {
								if(tmp2.obj[methId] == value) {
									tmp2.obj[methId] = null;
									if(--tmp2.count == 0) {
										tmp.entries.remove(listener);
										if(tmp.entries.isEmpty()) {
											deleteEndpoint(endpointNode, dir);
										}
									}
									return true;
								}else {
									return false;
								}
							}else {
								return false;
							}
						}else {
							if(method instanceof RouteMethodAllMethods<T> tmp2) {
								if(tmp2.obj == value) {
									tmp.entries.remove(listener);
									if(tmp.entries.isEmpty()) {
										deleteEndpoint(endpointNode, dir);
									}
									return true;
								}else {
									return false;
								}
							}else {
								return false;
							}
						}
					}else {
						return false;
					}
				}else {
					return false;
				}
			}
		}
	}

	private void deleteEndpoint(RouteTreeNode<L, T> endpointNode, boolean dir) {
		endpointNode.setEndpoint(dir, null);
		deleteNode(endpointNode);
	}

	private void deleteNode(RouteTreeNode<L, T> endpointNode) {
		RouteTreeNode<L, T> parent = endpointNode.parent;
		if (parent != null && endpointNode.endpoint == null && endpointNode.endpointDir == null
				&& endpointNode.children == null && endpointNode.defaultChild == null) {
			if(parent.defaultChild == endpointNode) {
				parent.defaultChild = null;
			}else {
				if(parent.children != null) {
					Iterator<RouteTreeNode<L, T>> itr = parent.children.values().iterator();
					while(itr.hasNext()) {
						if(itr.next() == endpointNode) {
							itr.remove();
							break;
						}
					}
					if(parent.children.isEmpty()) {
						parent.children = null;
					}
				}
			}
			deleteNode(parent);
		}
	}

	public void get(Iterator<CharSequence> tokens, boolean dir, L listener, int methId, Result<T> result) {
		RouteTreeNode<L, T> endpointNode = rootNode.find(tokens, dir);
		if(endpointNode == null) {
			result.result = null;
			return;
		}
		IRouteEndpoint<L, T> endpoint;
		boolean isDir;
		if(dir) {
			endpoint = endpointNode.endpointDir;
			if(endpoint == null) {
				endpoint = endpointNode.endpoint;
				isDir = false;
			}else {
				isDir = true;
			}
		}else {
			endpoint = endpointNode.endpoint;
			if(endpoint == null) {
				endpoint = endpointNode.endpointDir;
				isDir = true;
			}else {
				isDir = false;
			}
		}
		if(endpoint == null) {
			result.result = null;
			return;
		}
		IRouteMethods<T> methods = endpoint.getForListener(listener);
		if(methods == null) {
			result.result = null;
			return;
		}
		T ret = methods.getForMethod(methId);
		if(ret != null) {
			result.result = ret;
			result.directory = isDir;
		}else {
			result.result = null;
		}
	}

	public static final List<EnumRequestMethod> allMethods = ImmutableList.of(EnumRequestMethod.GET,
			EnumRequestMethod.HEAD, EnumRequestMethod.PUT, EnumRequestMethod.DELETE, EnumRequestMethod.POST,
			EnumRequestMethod.PATCH);

	public void getOptions(Iterator<CharSequence> tokens, boolean dir, L listener, Result<List<EnumRequestMethod>> result) {
		RouteTreeNode<L, T> endpointNode = rootNode.find(tokens, dir);
		if(endpointNode == null) {
			result.result = null;
			return;
		}
		IRouteEndpoint<L, T> endpoint;
		boolean isDir;
		if(dir) {
			endpoint = endpointNode.endpointDir;
			if(endpoint == null) {
				endpoint = endpointNode.endpoint;
				isDir = false;
			}else {
				isDir = true;
			}
		}else {
			endpoint = endpointNode.endpoint;
			if(endpoint == null) {
				endpoint = endpointNode.endpointDir;
				isDir = true;
			}else {
				isDir = false;
			}
		}
		if(endpoint == null) {
			result.result = null;
			return;
		}
		IRouteMethods<T> methods = endpoint.getForListener(listener);
		if(methods == null) {
			result.result = null;
			return;
		}
		result.directory = isDir;
		if(methods instanceof RouteMethodPerMethod) {
			if(dir == isDir) {
				RouteMethodPerMethod<T> perMethod = (RouteMethodPerMethod<T>) methods;
				List<EnumRequestMethod> meths = new ArrayList<>(numMeths);
				Object[] objArr = perMethod.obj;
				for(int i = 0, j = perMethod.count; i < numMeths && j > 0; ++i) {
					if(objArr[i] != null) {
						meths.add(EnumRequestMethod.fromId(i));
						--j;
					}
				}
				result.result = meths;
			}else {
				result.result = Collections.emptyList();
			}
		}else {
			result.result = allMethods;
		}
	}

	public void dump(Consumer<String> printer) {
		dumpNode(rootNode, "", printer);
	}

	private void dumpNode(RouteTreeNode<L, T> node, String indent, Consumer<String> printer) {
		printer.accept(indent + "endpoint: " + node.endpoint);
		printer.accept(indent + "endpointDir: " + node.endpointDir);
		printer.accept(indent + "parent: " + node.parent);
		printer.accept(indent + "isDefaultChild: " + node.isDefaultChild);
		printer.accept(indent + "defaultChild:");
		if(node.defaultChild != null) {
			dumpNode(node.defaultChild, indent + "  ", printer);
		}else {
			printer.accept(indent + "  (none)");
		}
		printer.accept(indent + "children:");
		if(node.children != null) {
			for(Map.Entry<String, RouteTreeNode<L, T>> etr : node.children.entrySet()) {
				printer.accept(indent + "  \"" + etr.getKey() + "\":");
				dumpNode(etr.getValue(), indent + "    ", printer);
			}
		}else {
			printer.accept(indent + "  (none)");
		}
	}

	public static class Result<T> {

		public T result;
		public boolean directory;

	}

}
