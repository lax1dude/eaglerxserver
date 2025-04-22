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

package net.lax1dude.eaglercraft.backend.server.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.net.InetAddresses;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;

public class RateLimiterExclusions {

	private static class Exclusion4 {

		protected final int addr;
		protected final int mask;
		protected final int subnet;

		protected Exclusion4(int addr, int subnet) {
			this.mask = ~((1 << (32 - subnet)) - 1);
			this.addr = addr & mask;
			this.subnet = subnet;
		}

	}

	private static class Exclusion6 {

		protected final long addrHi;
		protected final long addrLo;
		protected final long maskHi;
		protected final long maskLo;
		protected final int subnet;

		protected Exclusion6(long addrHi, long addrLo, int subnet) {
			if(subnet > 64) {
				this.maskHi = -1l;
				this.maskLo = ~((1l << (128 - subnet)) - 1);
			}else {
				this.maskHi = ~((1l << (64 - subnet)) - 1);
				this.maskLo = 0l;
			}
			this.addrHi = addrHi & maskHi;
			this.addrLo = addrLo & maskLo;
			this.subnet = subnet;
		}

	}

	public static RateLimiterExclusions create(List<String> list, IPlatformLogger logger) {
		List<Exclusion4> lst4 = new ArrayList<>();
		List<Exclusion6> lst6 = new ArrayList<>();
		for(String str : list) {
			int slashIdx = str.lastIndexOf('/');
			InetAddress addr;
			int subnet;
			if(slashIdx != -1) {
				try {
					addr = InetAddresses.forString(str.substring(0, slashIdx));
					subnet = Integer.parseInt(str.substring(slashIdx + 1));
				}catch(IllegalArgumentException ex) {
					logger.warn("Skipping invalid ratelimit exclusion: \"" + str + "\"", ex);
					continue;
				}
			}else {
				addr = InetAddresses.forString(str);
				subnet = -1;
			}
			if(addr instanceof Inet6Address addr6) {
				byte[] addrBytes = addr6.getAddress();
				long addrHi = ((long) (addrBytes[0] & 0xFF) << 56l) | ((long) (addrBytes[1] & 0xFF) << 48l)
						| ((long) (addrBytes[2] & 0xFF) << 40l) | ((long) (addrBytes[3] & 0xFF) << 32l)
						| ((long) (addrBytes[4] & 0xFF) << 24l) | ((long) (addrBytes[5] & 0xFF) << 16l)
						| ((long) (addrBytes[6] & 0xFF) << 8l) | (long) (addrBytes[7] & 0xFF);
				long addrLo = ((long) (addrBytes[8] & 0xFF) << 56l) | ((long) (addrBytes[9] & 0xFF) << 48l)
						| ((long) (addrBytes[10] & 0xFF) << 40l) | ((long) (addrBytes[11] & 0xFF) << 32l)
						| ((long) (addrBytes[12] & 0xFF) << 24l) | ((long) (addrBytes[13] & 0xFF) << 16l)
						| ((long) (addrBytes[14] & 0xFF) << 8l) | (long) (addrBytes[15] & 0xFF);
				lst6.add(new Exclusion6(addrHi, addrLo, subnet != -1 ? subnet : 128));
			}else if(addr instanceof Inet4Address addr4) {
				byte[] addrBytes = addr4.getAddress();
				int addrInt = ((addrBytes[0] & 0xFF) << 24) | ((addrBytes[1] & 0xFF) << 16)
						| ((addrBytes[2] & 0xFF) << 8) | (addrBytes[3] & 0xFF);
				lst4.add(new Exclusion4(addrInt, subnet != -1 ? subnet : 32));
			}else {
				logger.warn("Skipping unknown ratelimit address: \"" + addr + "\" (" + addr.getClass().getName() + ")");
			}
		}
		Collections.sort(lst4, (a, b) -> a.subnet - b.subnet);
		Collections.sort(lst6, (a, b) -> a.subnet - b.subnet);
		return new RateLimiterExclusions(ImmutableList.copyOf(lst4), ImmutableList.copyOf(lst6));
	}

	private final ImmutableList<Exclusion4> lst4;
	private final ImmutableList<Exclusion6> lst6;

	private RateLimiterExclusions(ImmutableList<Exclusion4> lst4, ImmutableList<Exclusion6> lst6) {
		this.lst4 = lst4;
		this.lst6 = lst6;
	}

	public boolean testExclusion(InetAddress addr) {
		if(addr instanceof Inet6Address addr2) {
			return testExclusion6(addr2);
		}else if(addr instanceof Inet4Address addr2) {
			return testExclusion4(addr2);
		}else {
			return false;
		}
	}

	public boolean testExclusion4(Inet4Address addr) {
		int l = lst4.size();
		if(l == 0) {
			return false;
		}
		byte[] addrBytes = addr.getAddress();
		int addrInt = ((addrBytes[0] & 0xFF) << 24) | ((addrBytes[1] & 0xFF) << 16)
				| ((addrBytes[2] & 0xFF) << 8) | (addrBytes[3] & 0xFF);
		for(int i = 0; i < l; ++i) {
			Exclusion4 ex = lst4.get(i);
			if((addrInt & ex.mask) == ex.addr) {
				return true;
			}
		}
		return false;
	}

	public boolean testExclusion6(Inet6Address addr) {
		int l = lst6.size();
		if(l == 0) {
			return false;
		}
		byte[] addrBytes = addr.getAddress();
		long addrHi = ((long) (addrBytes[0] & 0xFF) << 56l) | ((long) (addrBytes[1] & 0xFF) << 48l)
				| ((long) (addrBytes[2] & 0xFF) << 40l) | ((long) (addrBytes[3] & 0xFF) << 32l)
				| ((long) (addrBytes[4] & 0xFF) << 24l) | ((long) (addrBytes[5] & 0xFF) << 16l)
				| ((long) (addrBytes[6] & 0xFF) << 8l) | (long) (addrBytes[7] & 0xFF);
		long addrLo = ((long) (addrBytes[8] & 0xFF) << 56l) | ((long) (addrBytes[9] & 0xFF) << 48l)
				| ((long) (addrBytes[10] & 0xFF) << 40l) | ((long) (addrBytes[11] & 0xFF) << 32l)
				| ((long) (addrBytes[12] & 0xFF) << 24l) | ((long) (addrBytes[13] & 0xFF) << 16l)
				| ((long) (addrBytes[14] & 0xFF) << 8l) | (long) (addrBytes[15] & 0xFF);
		for(int i = 0; i < l; ++i) {
			Exclusion6 ex = lst6.get(i);
			if((addrHi & ex.maskHi) == ex.addrHi && (addrLo & ex.maskLo) == ex.addrLo) {
				return true;
			}
		}
		return false;
	}

}
