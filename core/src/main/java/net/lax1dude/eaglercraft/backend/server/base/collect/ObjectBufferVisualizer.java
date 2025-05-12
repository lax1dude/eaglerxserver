/*
 * HPPC
 *
 * Copyright (C) 2010-2024 Carrot Search s.c. and contributors
 * All rights reserved.
 *
 * Refer to the full license file "LICENSE.txt":
 * https://github.com/carrotsearch/hppc/blob/master/LICENSE.txt
 */
package net.lax1dude.eaglercraft.backend.server.base.collect;

/**
 * Reused buffer visualization routines.
 *
 * @see ObjectSet#visualizeKeyDistribution(int)
 * @see ObjectVTypeMap#visualizeKeyDistribution(int)
 */
class ObjectBufferVisualizer<KType> {
	static <KType> String visualizeKeyDistribution(Object[] buffer, int max, int characters) {
		final StringBuilder b = new StringBuilder();
		final char[] chars = ".123456789X".toCharArray();
		for (int i = 1, start = -1; i <= characters; i++) {
			int end = (int) ((long) i * max / characters);

			if (start + 1 <= end) {
				int taken = 0;
				int slots = 0;
				for (int slot = start + 1; slot <= end; slot++, slots++) {
					if (!((buffer[slot]) == null)) {
						taken++;
					}
				}
				b.append(chars[Math.min(chars.length - 1, taken * chars.length / slots)]);
				start = end;
			}
		}
		while (b.length() < characters) {
			b.append(' ');
		}
		return b.toString();
	}
}
