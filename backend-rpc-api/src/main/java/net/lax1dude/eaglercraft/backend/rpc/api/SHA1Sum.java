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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

public final class SHA1Sum {

	@Nonnull
	public static SHA1Sum ofData(@Nonnull byte[] data) {
		return ofData(data, 0, data.length);
	}

	@Nonnull
	public static SHA1Sum ofData(@Nonnull byte[] data, int offset, int length) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(data, offset, length);
			byte[] ret = digest.digest();
			return create(ret, 0, 20);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Incompatible JRE", e);
		}
	}

	@Nonnull
	public static SHA1Sum create(@Nonnull byte[] checksum) {
		return create(checksum, 0, checksum.length);
	}

	@Nonnull
	public static SHA1Sum create(@Nonnull byte[] checksum, int offset, int length) {
		if (offset < 0)
			throw new ArrayIndexOutOfBoundsException(offset);
		if (offset + length > checksum.length)
			throw new ArrayIndexOutOfBoundsException(length);
		return create(intHelper(checksum, offset), intHelper(checksum, offset + 4), intHelper(checksum, offset + 8),
				intHelper(checksum, offset + 12), intHelper(checksum, offset + 16));
	}

	@Nonnull
	public static SHA1Sum create(int a, int b, int c, int d, int e) {
		return new SHA1Sum(a, b, c, d, e);
	}

	private final int a;
	private final int b;
	private final int c;
	private final int d;
	private final int e;

	private SHA1Sum(int a, int b, int c, int d, int e) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}

	public int getBitsA() {
		return a;
	}

	public int getBitsB() {
		return b;
	}

	public int getBitsC() {
		return c;
	}

	public int getBitsD() {
		return d;
	}

	public int getBitsE() {
		return e;
	}

	@Nonnull
	public byte[] asBytes() {
		byte[] ret = new byte[20];
		asBytes(ret, 0);
		return ret;
	}

	public void asBytes(@Nonnull byte[] dst, int off) {
		byteHelper(dst, a, 0);
		byteHelper(dst, b, 4);
		byteHelper(dst, c, 8);
		byteHelper(dst, d, 12);
		byteHelper(dst, e, 16);
	}

	private static int intHelper(@Nonnull byte[] src, int off) {
		return ((src[off] & 0xFF) << 24) | ((src[off + 1] & 0xFF) << 16) | ((src[off + 2] & 0xFF) << 8)
				| (src[off + 3] & 0xFF);
	}

	private static void byteHelper(@Nonnull byte[] dst, int a, int off) {
		dst[off] = (byte) (a >>> 24);
		dst[off + 1] = (byte) (a >>> 16);
		dst[off + 2] = (byte) (a >>> 8);
		dst[off + 3] = (byte) a;
	}

	@Override
	@Nonnull
	public String toString() {
		char[] ret = new char[40];
		hexHelper(ret, 0, a);
		hexHelper(ret, 8, b);
		hexHelper(ret, 16, c);
		hexHelper(ret, 24, d);
		hexHelper(ret, 32, e);
		return new String(ret);
	}

	private static final char[] hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static void hexHelper(@Nonnull char[] ret, int i, int j) {
		ret[i] = hex[(j >>> 28) & 0xF];
		ret[i + 1] = hex[(j >>> 24) & 0xF];
		ret[i + 2] = hex[(j >>> 20) & 0xF];
		ret[i + 3] = hex[(j >>> 16) & 0xF];
		ret[i + 4] = hex[(j >>> 12) & 0xF];
		ret[i + 5] = hex[(j >>> 8) & 0xF];
		ret[i + 6] = hex[(j >>> 4) & 0xF];
		ret[i + 7] = hex[j & 0xF];
	}

	@Override
	public int hashCode() {
		return (((a * 31 + b) * 31 + c) * 31 + d) * 31 + e;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof SHA1Sum other) && other.a == a && other.b == b && other.c == c
				&& other.d == d && other.e == e);
	}

}
