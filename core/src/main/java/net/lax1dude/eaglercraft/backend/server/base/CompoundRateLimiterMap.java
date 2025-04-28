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

package net.lax1dude.eaglercraft.backend.server.base;

import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener.ConfigRateLimit;
import net.lax1dude.eaglercraft.backend.server.util.EnumRateLimitState;
import net.lax1dude.eaglercraft.backend.server.util.RateLimiterExclusions;
import net.lax1dude.eaglercraft.backend.server.util.RateLimiterLocking;
import net.lax1dude.eaglercraft.backend.server.util.RateLimiterLocking.Config;

public class CompoundRateLimiterMap {

	public static CompoundRateLimiterMap create(ConfigDataListener.ConfigRateLimit ratelimitIPConfIn,
			ConfigDataListener.ConfigRateLimit ratelimitLoginConfIn,
			ConfigDataListener.ConfigRateLimit ratelimitMOTDConfIn,
			ConfigDataListener.ConfigRateLimit ratelimitQueryConfIn,
			ConfigDataListener.ConfigRateLimit ratelimitHTTPConfIn, RateLimiterExclusions ratelimitExclusions) {
		if (!ratelimitIPConfIn.isEnabled() && !ratelimitLoginConfIn.isEnabled() && !ratelimitMOTDConfIn.isEnabled()
				&& !ratelimitQueryConfIn.isEnabled() && !ratelimitHTTPConfIn.isEnabled()) {
			return null;
		}
		RateLimiterLocking.Config ratelimitIPConf = createConf(ratelimitIPConfIn);
		RateLimiterLocking.Config ratelimitLoginConf = createConf(ratelimitLoginConfIn);
		RateLimiterLocking.Config ratelimitMOTDConf = createConf(ratelimitMOTDConfIn);
		RateLimiterLocking.Config ratelimitQueryConf = createConf(ratelimitQueryConfIn);
		RateLimiterLocking.Config ratelimitHTTPConf = createConf(ratelimitHTTPConfIn);
		return new CompoundRateLimiterMap(ratelimitIPConf, ratelimitLoginConf, ratelimitMOTDConf, ratelimitQueryConf,
				ratelimitHTTPConf, ratelimitExclusions);
	}

	private static RateLimiterLocking.Config createConf(ConfigRateLimit ratelimitIPConfIn) {
		if (!ratelimitIPConfIn.isEnabled()) {
			return null;
		}
		return new RateLimiterLocking.Config(ratelimitIPConfIn.getPeriod(), ratelimitIPConfIn.getLimit(),
				ratelimitIPConfIn.getLimitLockout(), ratelimitIPConfIn.getLockoutDuration());
	}

	public static interface ICompoundRatelimits {
		EnumRateLimitState rateLimitLogin();

		EnumRateLimitState rateLimitMOTD();

		EnumRateLimitState rateLimitQuery();

		EnumRateLimitState rateLimitHTTP();
	}

	private static final ICompoundRatelimits ALWAYS_OK = new ICompoundRatelimits() {
		@Override
		public EnumRateLimitState rateLimitLogin() {
			return EnumRateLimitState.OK;
		}

		@Override
		public EnumRateLimitState rateLimitMOTD() {
			return EnumRateLimitState.OK;
		}

		@Override
		public EnumRateLimitState rateLimitQuery() {
			return EnumRateLimitState.OK;
		}

		@Override
		public EnumRateLimitState rateLimitHTTP() {
			return EnumRateLimitState.OK;
		}
	};

	private class RateLimits extends RateLimiterLocking implements ICompoundRatelimits {

		private RateLimiterLocking ratelimitLogin;
		private RateLimiterLocking ratelimitMOTD;
		private RateLimiterLocking ratelimitQuery;
		private RateLimiterLocking ratelimitHTTP;

		@Override
		public EnumRateLimitState rateLimitLogin() {
			if (ratelimitLoginConf == null) {
				return EnumRateLimitState.OK;
			}
			RateLimiterLocking limiter = ratelimitLogin;
			if (limiter == null) {
				limiter = ratelimitLogin = new RateLimiterLocking();
			}
			return limiter.rateLimit(ratelimitLoginConf);
		}

		@Override
		public EnumRateLimitState rateLimitMOTD() {
			if (ratelimitMOTDConf == null) {
				return EnumRateLimitState.OK;
			}
			RateLimiterLocking limiter = ratelimitMOTD;
			if (limiter == null) {
				limiter = ratelimitMOTD = new RateLimiterLocking();
			}
			return limiter.rateLimit(ratelimitMOTDConf);
		}

		@Override
		public EnumRateLimitState rateLimitQuery() {
			if (ratelimitQueryConf == null) {
				return EnumRateLimitState.OK;
			}
			RateLimiterLocking limiter = ratelimitQuery;
			if (limiter == null) {
				limiter = ratelimitQuery = new RateLimiterLocking();
			}
			return limiter.rateLimit(ratelimitQueryConf);
		}

		@Override
		public EnumRateLimitState rateLimitHTTP() {
			if (ratelimitHTTPConf == null) {
				return EnumRateLimitState.OK;
			}
			RateLimiterLocking limiter = ratelimitHTTP;
			if (limiter == null) {
				limiter = ratelimitHTTP = new RateLimiterLocking();
			}
			return limiter.rateLimit(ratelimitHTTPConf);
		}

	}

	private final LoadingCache<InetAddress, RateLimits> cache;

	private final RateLimiterLocking.Config ratelimitIPConf;
	private final RateLimiterLocking.Config ratelimitLoginConf;
	private final RateLimiterLocking.Config ratelimitMOTDConf;
	private final RateLimiterLocking.Config ratelimitQueryConf;
	private final RateLimiterLocking.Config ratelimitHTTPConf;
	private final RateLimiterExclusions ratelimitExclusions;

	private CompoundRateLimiterMap(Config ratelimitIPConf, Config ratelimitLoginConf, Config ratelimitMOTDConf,
			Config ratelimitQueryConf, Config ratelimitHTTPConf, RateLimiterExclusions ratelimitExclusions) {
		this.cache = CacheBuilder.newBuilder().expireAfterAccess(5l, TimeUnit.MINUTES).maximumSize(8192)
				.build(new CacheLoader<InetAddress, RateLimits>() {
					@Override
					public RateLimits load(InetAddress arg0) throws Exception {
						return new RateLimits();
					}
				});
		this.ratelimitIPConf = ratelimitIPConf;
		this.ratelimitLoginConf = ratelimitLoginConf;
		this.ratelimitMOTDConf = ratelimitMOTDConf;
		this.ratelimitQueryConf = ratelimitQueryConf;
		this.ratelimitHTTPConf = ratelimitHTTPConf;
		this.ratelimitExclusions = ratelimitExclusions;
	}

	private RateLimits load(InetAddress address) {
		try {
			return cache.get(address);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof RuntimeException ee)
				throw ee;
			throw new RuntimeException(e);
		}
	}

	public ICompoundRatelimits rateLimit(InetAddress address) {
		if (ratelimitExclusions != null && ratelimitExclusions.testExclusion(address)) {
			return ALWAYS_OK;
		} else {
			RateLimits limits = load(address);
			return (ratelimitIPConf == null || limits.rateLimit(ratelimitIPConf).isOk()) ? limits : null;
		}
	}

	public ICompoundRatelimits getRateLimit(InetAddress address) {
		if (ratelimitExclusions != null && ratelimitExclusions.testExclusion(address)) {
			return ALWAYS_OK;
		} else {
			return load(address);
		}
	}

}
