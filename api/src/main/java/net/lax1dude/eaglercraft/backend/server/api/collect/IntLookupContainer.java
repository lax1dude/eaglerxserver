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

/**
 * Marker interface for containers that can check if they contain a given object
 * in at least time <code>O(log n)</code> and ideally in amortized constant time
 * <code>O(1)</code>.
 */
public interface IntLookupContainer extends IntContainer {
	public boolean contains(int e);
}
