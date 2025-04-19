package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import static org.objectweb.asm.Opcodes.*;

public class ClassProxy<T> {

	public interface IClassProxy {
	}

	public static class ProxyClassLoader extends ClassLoader {

		private final String name;
		private final byte[] data;

		protected ProxyClassLoader(ClassLoader parent, String name, byte[] data) {
			super(parent);
			this.name = name;
			this.data = data;
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			if(this.name.equals(name)) {
				return super.defineClass(name, data, 0, data.length);
			}else {
				return super.findClass(name);
			}
		}

	}

	private static final LoadingCache<HashPair<ClassLoader, Class<?>>, ClassProxy> loadingCache = CacheBuilder
			.newBuilder().build(new CacheLoader<HashPair<ClassLoader, Class<?>>, ClassProxy>() {
				@Override
				public ClassProxy load(HashPair<ClassLoader, Class<?>> arg0) throws Exception {
					return new ClassProxy(arg0.valueA, arg0.valueB);
				}
			});

	private final Method[] methods;
	private final Class<?> proxyClass;
	private final Map<Constructor<?>, Constructor<?>> ctor;

	private ClassProxy(ClassLoader loader, Class<?> parent) {
		try {
			Constructor<?>[] ctors = parent.getConstructors();
			if(ctors.length == 0) {
				throw new IllegalArgumentException("Class defines no public constructors");
			}
			this.methods = getMethodsToOverride(parent.getMethods());
			this.proxyClass = bindProxy(loader, parent, ctors, methods);
			ImmutableMap.Builder<Constructor<?>, Constructor<?>> builder = ImmutableMap.builder();
			for(int i = 0; i < ctors.length; ++i) {
				Constructor<?> c = ctors[i];
				Parameter[] params = c.getParameters();
				int l = params.length;
				Class<?>[] paramsClasses = new Class<?>[2 + l];
				for(int j = 0; j < params.length; ++j) {
					paramsClasses[j] = params[j].getType();
				}
				paramsClasses[l] = Method[].class;
				paramsClasses[l + 1] = InvocationHandler.class;
				builder.put(ctors[i], proxyClass.getConstructor(paramsClasses));
			}
			this.ctor = builder.build();
		} catch (Exception e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	private static Method[] getMethodsToOverride(Method[] meths) {
		List<Method> ret = new ArrayList<>();
		for(int i = 0; i < meths.length; ++i) {
			Method m = meths[i];
			if(m.getDeclaringClass() != Object.class && (m.getModifiers() & Modifier.FINAL) == 0) {
				ret.add(m);
			}
		}
		return ret.toArray(new Method[ret.size()]);
	}

	public T createProxy(Constructor<?> ctor, Object[] params, InvocationHandler invocationHandler) {
		Constructor<?> ctorImpl = this.ctor.get(ctor);
		if(ctorImpl == null) {
			throw new IllegalArgumentException("Unknown constructor: " + ctor.toString());
		}
		int j = params.length;
		Object[] params2 = new Object[j + 2];
		System.arraycopy(params, 0, params2, 0, j);
		params2[j] = methods;
		params2[j + 1] = invocationHandler;
		try {
			return (T) ctorImpl.newInstance(params2);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

	public static <T> T createProxy(ClassLoader loader, Class<T> parent, InvocationHandler invocationHandler) {
		Constructor<T> ctor;
		try {
			ctor = parent.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw Util.propagateReflectThrowable(e);
		}
		return createProxy(loader, parent, ctor, new Object[0], invocationHandler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(ClassLoader loader, Class<T> parent, Constructor<T> ctor, Object[] params,
			InvocationHandler invocationHandler) {
		ClassProxy<T> ret;
		try {
			ret = (ClassProxy<T>) loadingCache.get(new HashPair<>(loader, parent));
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if(t instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(t);
		}
		return ret.createProxy(ctor, params, invocationHandler);
	}

	@SuppressWarnings("unchecked")
	public static <T> ClassProxy<T> bindProxy(ClassLoader loader, Class<T> parent) {
		try {
			return (ClassProxy<T>) loadingCache.get(new HashPair<>(loader, parent));
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if(t instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(t);
		}
	}

	private static Class<?> bindProxy(ClassLoader loader, Class<?> parent, Constructor<?>[] ctors, Method[] methods) throws Exception {
		String parentName = Type.getInternalName(parent);
		String randomName = "EaglerClassProxy" + System.nanoTime();
		String proxyName = parentName + "/" + randomName;
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, proxyName, null, parentName, new String[] { Type.getInternalName(IClassProxy.class) });
		classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "meth", "[Ljava/lang/reflect/Method;", null, null).visitEnd();
		classWriter.visitField(ACC_PRIVATE | ACC_FINAL, "handler", "Ljava/lang/reflect/InvocationHandler;", null, null).visitEnd();
		MethodVisitor methodVisitor;
		for(int i = 0; i < ctors.length; ++i) {
			Constructor<?> ctor = ctors[i];
			String desc = Type.getConstructorDescriptor(ctor);
			if(!desc.endsWith(")V")) {
				throw new IllegalStateException();
			}
			String desc2 = desc.substring(0, desc.length() - 2) + "[Ljava/lang/reflect/Method;Ljava/lang/reflect/InvocationHandler;)V";
			Parameter[] params = ctor.getParameters();
			methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", desc2, null, null);
			methodVisitor.visitCode();
			Label label0 = new Label();
			methodVisitor.visitLabel(label0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			for(int j = 0; j < params.length; ++j) {
				loadParam(methodVisitor, j + 1, params[j].getType());
			}
			methodVisitor.visitMethodInsn(INVOKESPECIAL, parentName, "<init>", desc, false);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, params.length + 1);
			methodVisitor.visitFieldInsn(PUTFIELD, proxyName, "meth", "[Ljava/lang/reflect/Method;");
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, params.length + 2);
			methodVisitor.visitFieldInsn(PUTFIELD, proxyName, "handler", "Ljava/lang/reflect/InvocationHandler;");
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		for(int i = 0; i < methods.length; ++i) {
			Method meth = methods[i];
			String desc = Type.getMethodDescriptor(meth);
			methodVisitor = classWriter.visitMethod(ACC_PUBLIC, meth.getName(), desc, null, null);
			methodVisitor.visitCode();
			Label label0 = new Label();
			methodVisitor.visitLabel(label0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, proxyName, "handler", "Ljava/lang/reflect/InvocationHandler;");
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, proxyName, "meth", "[Ljava/lang/reflect/Method;");
			visitICONST(methodVisitor, i);
			methodVisitor.visitInsn(AALOAD);
			Parameter[] params = meth.getParameters();
			int k = params.length;
			if(k > 0) {
				visitICONST(methodVisitor, k);
				methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
				for(int j = 0; j < k; ++j) {
					methodVisitor.visitInsn(DUP);
					Parameter p = params[j];
					visitICONST(methodVisitor, j);
					loadParam(methodVisitor, 1 + j, p.getType());
					visitWrap(methodVisitor, p.getType());
					methodVisitor.visitInsn(AASTORE);
				}
			}else {
				methodVisitor.visitInsn(ICONST_0);
				methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
			}
			methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
			Class<?> ret = meth.getReturnType();
			visitUnwrap(methodVisitor, ret);
			visitReturn(methodVisitor, ret);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
		classWriter.visitEnd();
		String name = proxyName.replace('/', '.').replace('$', '.');
		return (new ProxyClassLoader(loader, name, classWriter.toByteArray())).loadClass(name);
	}

	private static void loadParam(MethodVisitor methodVisitor, int j, Class<?> clz) {
		if(clz == void.class) {
			throw new IllegalArgumentException();
		}else if(clz == int.class || clz == short.class || clz == byte.class || clz == boolean.class) {
			methodVisitor.visitVarInsn(ILOAD, j);
		}else if(clz == long.class) {
			methodVisitor.visitVarInsn(LLOAD, j);
		}else if(clz == float.class) {
			methodVisitor.visitVarInsn(FLOAD, j);
		}else if(clz == double.class) {
			methodVisitor.visitVarInsn(DLOAD, j);
		}else {
			methodVisitor.visitVarInsn(ALOAD, j);
		}
	}

	private static void visitICONST(MethodVisitor methodVisitor, int i) {
		switch(i) {
		case 0:
			methodVisitor.visitInsn(ICONST_0);
			break;
		case 1:
			methodVisitor.visitInsn(ICONST_1);
			break;
		case 2:
			methodVisitor.visitInsn(ICONST_2);
			break;
		case 3:
			methodVisitor.visitInsn(ICONST_3);
			break;
		case 4:
			methodVisitor.visitInsn(ICONST_4);
			break;
		case 5:
			methodVisitor.visitInsn(ICONST_5);
			break;
		default:
			if(i < -128 || i > 127) {
				methodVisitor.visitIntInsn(SIPUSH, i);
			}else if(i == -1) {
				methodVisitor.visitInsn(ICONST_M1);
			}else {
				methodVisitor.visitIntInsn(BIPUSH, i);
			}
			break;
		}
	}

	private static void visitWrap(MethodVisitor methodVisitor, Class<?> clz) {
		if(clz == void.class) {
			throw new IllegalArgumentException();
		}else if(clz == int.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		}else if(clz == short.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
		}else if(clz == byte.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
		}else if(clz == boolean.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
		}else if(clz == long.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
		}else if(clz == float.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
		}else if(clz == double.class) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
		}
	}

	private static void visitUnwrap(MethodVisitor methodVisitor, Class<?> clz) {
		if(clz == void.class) {
			methodVisitor.visitInsn(POP);
		}else if(clz == int.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
		}else if(clz == short.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Short");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
		}else if(clz == byte.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Byte");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
		}else if(clz == boolean.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		}else if(clz == long.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Long");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
		}else if(clz == float.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Float");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
		}else if(clz == double.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
		}else if(clz != Object.class) {
			methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(clz));
		}
	}

	private static void visitReturn(MethodVisitor methodVisitor, Class<?> clz) {
		if(clz == void.class) {
			methodVisitor.visitInsn(RETURN);
		}else if(clz == int.class || clz == short.class || clz == byte.class || clz == boolean.class) {
			methodVisitor.visitInsn(IRETURN);
		}else if(clz == long.class) {
			methodVisitor.visitInsn(LRETURN);
		}else if(clz == float.class) {
			methodVisitor.visitInsn(FRETURN);
		}else if(clz == double.class) {
			methodVisitor.visitInsn(DRETURN);
		}else {
			methodVisitor.visitInsn(ARETURN);
		}
	}

}
