package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RouteMap<T> {

	public static final int numMeths = 6;

	private final RouteTreeNode<T> rootNode = new RouteTreeNode<T>(null);

	private static class RouteTreeNode<T> {

		protected final RouteTreeNode<T> parent;

		protected RouteTreeNode(RouteTreeNode<T> parent) {
			this.parent = parent;
		}

		protected Map<String, RouteTreeNode<T>> children;
		protected RouteTreeNode<T> defaultChild;
		protected boolean isDefaultChild;

		protected IRouteEndpoint<T> endpoint;
		protected IRouteEndpoint<T> endpointDir;

		protected IRouteEndpoint<T> find(Iterator<String> tokens, boolean dir) {
			if(tokens.hasNext()) {
				String n = tokens.next();
				if(children != null) {
					RouteTreeNode<T> r = children.get(n);
					if(r != null) {
						return r.find(tokens, dir);
					}
				}
				if(defaultChild != null && tokens.hasNext()) {
					IRouteEndpoint<T> r = defaultChild.find(tokens, dir);
					if(r != null) {
						return r;
					}
				}
				if(isDefaultChild) {
					return endpoint(dir);
				}
				return null;
			}else {
				return endpoint(dir);
			}
		}

		protected IRouteEndpoint<T> endpoint(boolean dir) {
			return dir ? endpointDir : endpoint;
		}

		protected RouteTreeNode<T> getOrCreateChild(String name) {
			RouteTreeNode<T> r;
			if("*".equals(name)) {
				r = defaultChild;
				if(r == null) {
					defaultChild = r = new RouteTreeNode<>(this);
					r.isDefaultChild = true;
				}
			}else {
				r = children.get(name);
				if(r == null) {
					children.put(name, r = new RouteTreeNode<>(this));
				}
			}
			return r;
		}

	}

	private static abstract class IRouteEndpoint<T> {

		protected abstract IRouteMethods<T> getForListener(String ls);

	}

	private static abstract class IRouteMethods<T> {

		protected abstract T getForMethod(int methId);

	}

	private static class RouteEndpointAllListener<T> extends IRouteEndpoint<T> {

		protected final IRouteMethods<T> method;

		protected RouteEndpointAllListener(IRouteMethods<T> method) {
			this.method = method;
		}

		@Override
		public IRouteMethods<T> getForListener(String ls) {
			return method;
		}

	}

	private static class RouteEndpointPerListener<T> extends IRouteEndpoint<T> {

		protected final Map<String, IRouteMethods<T>> entries;

		protected RouteEndpointPerListener() {
			this.entries = new HashMap<>(4);
		}

		@Override
		public IRouteMethods<T> getForListener(String ls) {
			return entries.get(ls);
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

	}

	private static class RouteMethodPerMethod<T> extends IRouteMethods<T> {

		private final T[] obj;

		@SuppressWarnings("unchecked")
		protected RouteMethodPerMethod() {
			this.obj = (T[]) new Object[numMeths];
		}

		@Override
		public T getForMethod(int methId) {
			return obj[methId];
		}

	}

	public boolean register(Iterator<String> tokens, boolean dir, String listener, int methId, T value) {
		RouteTreeNode<T> path = rootNode;
		while(tokens.hasNext()) {
			path = path.getOrCreateChild(tokens.next());
		}
		return register0(path, dir, listener, methId, value);
	}

	private boolean register0(RouteTreeNode<T> path, boolean dir, String listener, int methId, T value) {
		return false;
	}

	public boolean remove(Iterator<String> tokens, boolean dir, String listener, int methId, T value) {
		return false;
	}

	public T get(Iterator<String> tokens, boolean dir, String listener, int methId) {
		IRouteEndpoint<T> endpoint = rootNode.find(tokens, dir);
		if(endpoint == null) {
			return null;
		}
		IRouteMethods<T> methods = endpoint.getForListener(listener);
		if(methods == null) {
			return null;
		}
		return methods.getForMethod(methId);
	}

}
