package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.util.RateLimiter;

public class PlayerRateLimits {

	private static final int LIMIT_SKIN = 240;
	private RateLimiter ratelimitSkin;
	private static final int LIMIT_CAPE = 180;
	private RateLimiter ratelimitCape;
	private static final int LIMIT_VOICE_CON = 20;
	private RateLimiter ratelimitVoiceCon;
	private static final int LIMIT_VOICE_REQ = 120;
	private RateLimiter ratelimitVoiceReq;
	private static final int LIMIT_VOICE_ICE = 600;
	private RateLimiter ratelimitVoiceICE;
	private static final int LIMIT_BRAND = 240;
	private RateLimiter ratelimitBrand;
	private static final int LIMIT_WEBVIEW_DATA = 8;
	private RateLimiter ratelimitWebViewData;
	private static final int LIMIT_WEBVIEW_MSG = 120;
	private RateLimiter ratelimitWebViewMsg;

	// Note that the below functions are not perfectly thread safe,
	// but unlikely to cause the rate limiting to be unreliable

	public boolean ratelimitSkin() {
		RateLimiter limiter = ratelimitSkin;
		if(limiter == null) {
			limiter = ratelimitSkin = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_SKIN);
	}

	public boolean ratelimitCape() {
		RateLimiter limiter = ratelimitCape;
		if(limiter == null) {
			limiter = ratelimitCape = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_CAPE);
	}

	public boolean ratelimitVoiceCon() {
		RateLimiter limiter = ratelimitVoiceCon;
		if(limiter == null) {
			limiter = ratelimitVoiceCon = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_VOICE_CON);
	}

	public boolean ratelimitVoiceReq() {
		RateLimiter limiter = ratelimitVoiceReq;
		if(limiter == null) {
			limiter = ratelimitVoiceReq = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_VOICE_REQ);
	}

	public boolean ratelimitVoiceICE() {
		RateLimiter limiter = ratelimitVoiceICE;
		if(limiter == null) {
			limiter = ratelimitVoiceICE = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_VOICE_ICE);
	}

	public boolean ratelimitBrand() {
		RateLimiter limiter = ratelimitBrand;
		if(limiter == null) {
			limiter = ratelimitBrand = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_BRAND);
	}

	public boolean ratelimitWebViewData() {
		RateLimiter limiter = ratelimitWebViewData;
		if(limiter == null) {
			limiter = ratelimitWebViewData = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_WEBVIEW_DATA);
	}

	public boolean ratelimitWebViewMsg() {
		RateLimiter limiter = ratelimitWebViewMsg;
		if(limiter == null) {
			limiter = ratelimitWebViewMsg = new RateLimiter();
		}
		return limiter.rateLimit(LIMIT_WEBVIEW_MSG);
	}

}
