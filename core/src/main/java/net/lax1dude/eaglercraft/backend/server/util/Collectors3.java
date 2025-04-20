package net.lax1dude.eaglercraft.backend.server.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@SuppressWarnings("unchecked")
public class Collectors3 {

	public static final Collector<Object, ?, List<Object>> IMMUTABLE_LIST;
	public static final Collector<Object, ?, Set<Object>> IMMUTABLE_SET;

	static {
		Collector<Object, ?, List<Object>> c1;
		try {
			c1 = (Collector<Object, ?, List<Object>>) ImmutableList.class.getMethod("toImmutableList").invoke(null);
		}catch(ReflectiveOperationException ex) {
			c1 = Collectors.toUnmodifiableList();
		}
		Collector<Object, ?, Set<Object>> c2;
		try {
			c2 = (Collector<Object, ?, Set<Object>>) ImmutableSet.class.getMethod("toImmutableSet").invoke(null);
		}catch(ReflectiveOperationException ex) {
			c2 = Collectors.toUnmodifiableSet();
		}
		IMMUTABLE_LIST = c1;
		IMMUTABLE_SET = c2;
	}

	@SuppressWarnings("rawtypes")
	public static <E> Collector<E, ?, List<E>> toImmutableList() {
		return (Collector) IMMUTABLE_LIST;
	}

	@SuppressWarnings("rawtypes")
	public static <E> Collector<E, ?, Set<E>> toImmutableSet() {
		return (Collector) IMMUTABLE_SET;
	}

}
