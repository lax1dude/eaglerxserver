package net.lax1dude.eaglercraft.backend.server.util;

import com.google.common.collect.ImmutableList;

public class ImmutableBuilders {

	private static final boolean BUILDER_WITH_EXPECTED_SUPPORT;

	static {
		boolean b = false;
		try {
			ImmutableList.class.getMethod("builderWithExpectedSize", int.class);
			b = true;
		}catch(ReflectiveOperationException ex) {
		}
		BUILDER_WITH_EXPECTED_SUPPORT = b;
	}

	public static <T> ImmutableList.Builder<T> listBuilderWithExpected(int cnt) {
		if(BUILDER_WITH_EXPECTED_SUPPORT) {
			return ImmutableList.builderWithExpectedSize(cnt);
		}else {
			return ImmutableList.builder();
		}
	}

}
