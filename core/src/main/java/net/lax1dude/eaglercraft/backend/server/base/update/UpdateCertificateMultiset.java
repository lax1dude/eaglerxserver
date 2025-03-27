package net.lax1dude.eaglercraft.backend.server.base.update;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

public class UpdateCertificateMultiset {

	private final ConcurrentMap<IUpdateCertificateImpl, AtomicInteger> updateCertSet;

	public UpdateCertificateMultiset() {
		updateCertSet = (new MapMaker()).weakKeys().makeMap();
	}

	private static class Witness implements BiFunction<IUpdateCertificateImpl, AtomicInteger, AtomicInteger> {

		protected boolean create;

		@Override
		public AtomicInteger apply(IUpdateCertificateImpl certt, AtomicInteger refCnt) {
			if(refCnt == null) {
				create = true;
				return new AtomicInteger(1);
			}else {
				refCnt.incrementAndGet();
				return refCnt;
			}
		}

	}

	public boolean add(IUpdateCertificateImpl cert) {
		Witness witness = new Witness();
		updateCertSet.compute(cert, witness);
		return witness.create;
	}

	public void remove(IUpdateCertificateImpl cert) {
		updateCertSet.computeIfPresent(cert, (certt, refCnt) -> refCnt.decrementAndGet() > 0 ? refCnt : null);
	}

	public List<IUpdateCertificateImpl> dump() {
		return ImmutableList.copyOf(updateCertSet.keySet());
	}

	public void dump(Consumer<IUpdateCertificateImpl> cb) {
		updateCertSet.keySet().forEach(cb);
	}

}
