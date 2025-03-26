package net.lax1dude.eaglercraft.backend.server.base.update;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

public class UpdateCertificateMultiset {

	private final ConcurrentMap<IUpdateCertificateImpl, AtomicInteger> updateCertSet;

	public UpdateCertificateMultiset() {
		updateCertSet = (new MapMaker()).weakKeys().makeMap();
	}

	public void add(IUpdateCertificateImpl cert) {
		updateCertSet.compute(cert, (certt, refCnt) -> {
			if(refCnt == null) {
				return new AtomicInteger(1);
			}else {
				refCnt.incrementAndGet();
				return refCnt;
			}
		});
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
