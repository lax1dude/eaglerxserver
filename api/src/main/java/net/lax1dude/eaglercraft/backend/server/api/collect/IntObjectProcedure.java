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

/** A procedure that applies to <code>int</code>, <code>Object</code> pairs. */
public interface IntObjectProcedure<VType> {
	public void apply(int key, VType value);
}
