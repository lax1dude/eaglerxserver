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
			if (refCnt == null) {
				create = true;
				return new AtomicInteger(1);
			} else {
				refCnt.getAndIncrement();
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
		updateCertSet.computeIfPresent(cert, (certt, refCnt) -> refCnt.getAndDecrement() > 1 ? refCnt : null);
	}

	public List<IUpdateCertificateImpl> dump() {
		return ImmutableList.copyOf(updateCertSet.keySet());
	}

	public void dump(Consumer<IUpdateCertificateImpl> cb) {
		updateCertSet.keySet().forEach(cb);
	}

}
