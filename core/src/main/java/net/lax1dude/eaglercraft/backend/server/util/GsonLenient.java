package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.Method;

import com.google.gson.GsonBuilder;

@SuppressWarnings("unchecked")
public class GsonLenient {

	private static final Method meth_setStrictness;
	private static final Object enum_LENIENT;
	private static final Method meth_setLenient;

	static {
		Method _meth_setStrictness;
		Object _enum_LENIENT;
		Method _meth_setLenient;
		try {
			Class<?> class_Strictness = Class.forName("com.google.gson.Strictness");
			_enum_LENIENT = Enum.valueOf((Class) class_Strictness, "LENIENT");
			_meth_setStrictness = GsonBuilder.class.getMethod("setStrictness", class_Strictness);
			_meth_setLenient = null;
		} catch (IllegalArgumentException | ReflectiveOperationException e) {
			_meth_setStrictness = null;
			_enum_LENIENT = null;
			try {
				_meth_setLenient = GsonBuilder.class.getMethod("setLenient");
			} catch (ReflectiveOperationException ee) {
				_meth_setLenient = null;
			}
		}
		meth_setStrictness = _meth_setStrictness;
		enum_LENIENT = _enum_LENIENT;
		meth_setLenient = _meth_setLenient;
	}

	public static GsonBuilder setLenient(GsonBuilder builder) {
		try {
			if(meth_setStrictness != null) {
				meth_setStrictness.invoke(builder, enum_LENIENT);
			}else if(meth_setLenient != null) {
				meth_setLenient.invoke(builder);
			}
			return builder;
		} catch (ReflectiveOperationException e) {
			throw Util.propagateReflectThrowable(e);
		}
	}

}
