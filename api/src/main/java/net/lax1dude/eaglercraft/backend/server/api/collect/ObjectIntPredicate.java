/*
 * HPPC
 *
 * Copyright (C) 2010-2024 Carrot Search s.c. and contributors
 * All rights reserved.
 *
 * Refer to the full license file "LICENSE.txt":
 * https://github.com/carrotsearch/hppc/blob/master/LICENSE.txt
 */
package net.lax1dude.eaglercraft.backend.server.api.collect;

/** A predicate that applies to <code>Object</code>, <code>int</code> pairs. */
public interface ObjectIntPredicate<KType> {
	public boolean apply(KType key, int value);
}
