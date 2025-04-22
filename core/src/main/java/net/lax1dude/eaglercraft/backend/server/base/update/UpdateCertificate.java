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

package net.lax1dude.eaglercraft.backend.server.base.update;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUpdateCertEAG;

public final class UpdateCertificate implements IUpdateCertificateImpl {

	private static final Cache<SHA1Sum, UpdateCertificate> interner = CacheBuilder.newBuilder().softValues().build();

	public static IUpdateCertificateImpl intern(byte[] data) {
		if(data == null) {
			throw new NullPointerException("data");
		}
		SHA1Sum sum = SHA1Sum.ofData(data);
		try {
			return interner.get(sum, () -> {
				return new UpdateCertificate(data, sum);
			});
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(e.getCause());
		}
	}

	static IUpdateCertificateImpl internUnsafe(SHA1Sum sum, byte[] data) {
		try {
			return interner.get(sum, () -> {
				return new UpdateCertificate(data, sum);
			});
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(e.getCause());
		}
	}

	public static List<IUpdateCertificateImpl> dumpAll() {
		return ImmutableList.copyOf(interner.asMap().values());
	}

	private final SHA1Sum checksum;
	private final byte[] data;
	private final int hash;
	private final SPacketUpdateCertEAG packet;

	private UpdateCertificate(byte[] data, SHA1Sum sum) {
		this.checksum = sum;
		this.data = data;
		this.hash = sum.hashCode();
		this.packet = new SPacketUpdateCertEAG(data);
	}

	@Override
	public int getLength() {
		return data.length;
	}

	@Override
	public void getBytes(byte[] dst, int offset) {
		System.arraycopy(data, 0, dst, offset, data.length);
	}

	@Override
	public void getBytes(int srcOffset, byte[] dst, int dstOffset, int length) {
		System.arraycopy(data, srcOffset, dst, dstOffset, length);
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public SPacketUpdateCertEAG packet() {
		return packet;
	}

	@Override
	public SHA1Sum checkSum() {
		return checksum;
	}

}
