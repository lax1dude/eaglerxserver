package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.util.RateLimiterBasic;

public class PlayerRateLimits {

	public static class RateLimitParams {

		public final int limitSkin;
		public final int limitCape;
		public final int limitVoiceCon;
		public final int limitVoiceReq;
		public final int limitVoiceICE;
		public final int limitBrand;
		public final int limitWebViewData;
		public final int limitWebViewMsg;
		public final int limitSkinAntagonist;
		public final int limitSvSkinAntagonist;
		public final int limitSvBrandAntagonist;

		public RateLimitParams(int limitSkin, int limitCape, int limitVoiceCon, int limitVoiceReq, int limitVoiceICE,
				int limitBrand, int limitWebViewData, int limitWebViewMsg, int limitSkinAntagonist,
				int limitSvSkinAntagonist, int limitSvBrandAntagonist) {
			this.limitSkin = limitSkin;
			this.limitCape = limitCape;
			this.limitVoiceCon = limitVoiceCon;
			this.limitVoiceReq = limitVoiceReq;
			this.limitVoiceICE = limitVoiceICE;
			this.limitBrand = limitBrand;
			this.limitWebViewData = limitWebViewData;
			this.limitWebViewMsg = limitWebViewMsg;
			this.limitSkinAntagonist = limitSkinAntagonist;
			this.limitSvSkinAntagonist = limitSvSkinAntagonist;
			this.limitSvBrandAntagonist = limitSvBrandAntagonist;
		}

	}

	private final RateLimitParams params;

	private RateLimiterBasic ratelimitSkin;
	private RateLimiterBasic ratelimitCape;
	private RateLimiterBasic ratelimitVoiceCon;
	private RateLimiterBasic ratelimitVoiceReq;
	private RateLimiterBasic ratelimitVoiceICE;
	private RateLimiterBasic ratelimitBrand;
	private RateLimiterBasic ratelimitWebViewData;
	private RateLimiterBasic ratelimitWebViewMsg;
	private RateLimiterBasic skinAntagonistTracker;
	private RateLimiterBasic svSkinAntagonistTracker;
	private RateLimiterBasic svBrandAntagonistTracker;

	public PlayerRateLimits(RateLimitParams params) {
		this.params = params;
	}

	// Note that the below functions will cause a data race initializing
	// in multi-threaded environments, but java is memory-safe so fuck it

	public boolean ratelimitSkin() {
		RateLimiterBasic limiter = ratelimitSkin;
		if(limiter == null) {
			limiter = ratelimitSkin = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitSkin);
	}

	public boolean ratelimitCape() {
		RateLimiterBasic limiter = ratelimitCape;
		if(limiter == null) {
			limiter = ratelimitCape = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitCape);
	}

	public boolean ratelimitVoiceCon() {
		RateLimiterBasic limiter = ratelimitVoiceCon;
		if(limiter == null) {
			limiter = ratelimitVoiceCon = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitVoiceCon);
	}

	public boolean ratelimitVoiceReq() {
		RateLimiterBasic limiter = ratelimitVoiceReq;
		if(limiter == null) {
			limiter = ratelimitVoiceReq = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitVoiceReq);
	}

	public boolean ratelimitVoiceICE() {
		RateLimiterBasic limiter = ratelimitVoiceICE;
		if(limiter == null) {
			limiter = ratelimitVoiceICE = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitVoiceICE);
	}

	public boolean ratelimitBrand() {
		RateLimiterBasic limiter = ratelimitBrand;
		if(limiter == null) {
			limiter = ratelimitBrand = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitBrand);
	}

	public boolean ratelimitWebViewData() {
		RateLimiterBasic limiter = ratelimitWebViewData;
		if(limiter == null) {
			limiter = ratelimitWebViewData = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitWebViewData);
	}

	public boolean ratelimitWebViewMsg() {
		RateLimiterBasic limiter = ratelimitWebViewMsg;
		if(limiter == null) {
			limiter = ratelimitWebViewMsg = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitWebViewMsg);
	}

	public boolean checkSkinAntagonist() {
		RateLimiterBasic limiter = skinAntagonistTracker;
		return limiter == null || limiter.checkState(params.limitSkinAntagonist);
	}

	public boolean ratelimitSkinAntagonist() {
		RateLimiterBasic limiter = skinAntagonistTracker;
		if(limiter == null) {
			limiter = skinAntagonistTracker = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitSkinAntagonist);
	}

	public boolean checkSvSkinAntagonist() {
		RateLimiterBasic limiter = svSkinAntagonistTracker;
		return limiter == null || limiter.checkState(params.limitSvSkinAntagonist);
	}

	public boolean ratelimitSvSkinAntagonist() {
		RateLimiterBasic limiter = svSkinAntagonistTracker;
		if(limiter == null) {
			limiter = svSkinAntagonistTracker = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitSvSkinAntagonist);
	}

	public boolean checkSvBrandAntagonist() {
		RateLimiterBasic limiter = svBrandAntagonistTracker;
		return limiter == null || limiter.checkState(params.limitSvBrandAntagonist);
	}

	public boolean ratelimitSvBrandAntagonist() {
		RateLimiterBasic limiter = svBrandAntagonistTracker;
		if(limiter == null) {
			limiter = svBrandAntagonistTracker = new RateLimiterBasic();
		}
		return limiter.rateLimit(params.limitSvBrandAntagonist);
	}

}
