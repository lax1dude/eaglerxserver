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

/** A procedure that applies to <code>Object</code>, <code>int</code> pairs. */
public interface ObjectIntProcedure<KType> {
	public void apply(KType key, int value);
}
