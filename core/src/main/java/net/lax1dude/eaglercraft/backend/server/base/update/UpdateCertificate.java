package net.lax1dude.eaglercraft.backend.server.base.update;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUpdateCertEAG;

public final class UpdateCertificate implements IUpdateCertificateImpl {

	private static final Cache<SHA1Sum, UpdateCertificate> interner = CacheBuilder.newBuilder()
			.weakKeys().weakValues().build();

	public static IUpdateCertificateImpl intern(byte[] data) {
		if(data == null) {
			throw new NullPointerException("data");
		}
		SHA1Sum sum = SHA1Sum.create(data);
		try {
			return interner.get(sum, () -> {
				return new UpdateCertificate(data, sum);
			});
		} catch (ExecutionException e) {
			Throwables.throwIfUnchecked(e.getCause());
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
