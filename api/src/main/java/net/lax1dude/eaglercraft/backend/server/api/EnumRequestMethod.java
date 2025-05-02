/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the request method of an inbound HTTP request.
 */
public enum EnumRequestMethod {
	GET(0, 1), HEAD(1, 2), PUT(2, 4), DELETE(3, 8), POST(4, 16), PATCH(5, 32), OPTIONS(6, -1);

	public static final int bits = 63;

	private static final EnumRequestMethod[] VALUES = new EnumRequestMethod[] { GET, HEAD, PUT, DELETE, POST, PATCH, OPTIONS };
	private static final EnumRequestMethod[] BIT_LOOKUP = new EnumRequestMethod[] { GET, HEAD, PUT, DELETE, POST, PATCH };

	private final int id;
	private final int bit;

	private EnumRequestMethod(int id, int bit) {
		this.id = id;
		this.bit = bit;
	}

	/**
	 * Returns the ID of the request method.
	 * 
	 * <p>Used internally by the Web Server API.
	 * 
	 * @return The request method ID.
	 */
	public int id() {
		return id;
	}

	/**
	 * Returns the bit of the request method, for use in bitfields.
	 * 
	 * <p>Used internally by the Web Server API.
	 * 
	 * @return The request method bit, or {@code -1} if the request method is
	 *         {@code OPTIONS}
	 * @see #toBits(EnumRequestMethod[])
	 */
	public int bit() {
		return bit;
	}

	/**
	 * Converts an array of request methods to a bitfield.
	 * 
	 * @param methods An array of request methods
	 * @return A bitfield representing the request methods
	 * @throws IllegalArgumentException If the array contains {@code OPTIONS}.
	 * @throws NullPointerException     If the array is {@code null} or contains a
	 *                                  {@code null} value.
	 * @see #bit()
	 */
	public static int toBits(@Nonnull EnumRequestMethod[] methods) {
		int r = 0, j;
		for (int i = 0; i < methods.length; ++i) {
			j = methods[i].bit;
			if (j == -1) {
				throw new IllegalArgumentException("Cannot have OPTIONS in bitfield!");
			}
			r |= j;
		}
		return r;
	}

	/**
	 * Converts a bitfield to an array of request methods.
	 * 
	 * <p>Attempting to pass an invalid bitfield will cause undefined behavior.
	 * 
	 * @param bits The request method bitfield.
	 * @return An array of request methods.
	 */
	@Nonnull
	public static EnumRequestMethod[] fromBits(int bits) {
		bits &= EnumRequestMethod.bits;
		int cnt = Integer.bitCount(bits);
		EnumRequestMethod[] ret = new EnumRequestMethod[cnt];
		for (int i = 0; i < cnt; ++i) {
			int j = Integer.numberOfTrailingZeros(bits);
			ret[i] = BIT_LOOKUP[j];
			bits &= ((EnumRequestMethod.bits - 1) << j);
		}
		return ret;
	}

	/**
	 * Finds a request method from its internal ID.
	 * 
	 * @param id The request method ID.
	 * @return The request method enum, or {@code null} if unknown.
	 * @see #id()
	 */
	@Nullable
	public static EnumRequestMethod fromId(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : null;
	}

}
