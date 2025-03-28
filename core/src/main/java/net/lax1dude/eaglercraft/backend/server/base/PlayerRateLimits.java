package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.util.RateLimiterBasic;

public class PlayerRateLimits {

	private static final int LIMIT_SKIN = 240;
	private RateLimiterBasic ratelimitSkin;
	private static final int LIMIT_CAPE = 180;
	private RateLimiterBasic ratelimitCape;
	private static final int LIMIT_VOICE_CON = 20;
	private RateLimiterBasic ratelimitVoiceCon;
	private static final int LIMIT_VOICE_REQ = 120;
	private RateLimiterBasic ratelimitVoiceReq;
	private static final int LIMIT_VOICE_ICE = 600;
	private RateLimiterBasic ratelimitVoiceICE;
	private static final int LIMIT_BRAND = 240;
	private RateLimiterBasic ratelimitBrand;
	private static final int LIMIT_WEBVIEW_DATA = 8;
	private RateLimiterBasic ratelimitWebViewData;
	private static final int LIMIT_WEBVIEW_MSG = 120;
	private RateLimiterBasic ratelimitWebViewMsg;

	// Note that the below functions are not perfectly thread safe,
	// but unlikely to cause the rate limiting to be unreliable

	public boolean ratelimitSkin() {
		RateLimiterBasic limiter = ratelimitSkin;
		if(limiter == null) {
			limiter = ratelimitSkin = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_SKIN);
	}

	public boolean ratelimitCape() {
		RateLimiterBasic limiter = ratelimitCape;
		if(limiter == null) {
			limiter = ratelimitCape = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_CAPE);
	}

	public boolean ratelimitVoiceCon() {
		RateLimiterBasic limiter = ratelimitVoiceCon;
		if(limiter == null) {
			limiter = ratelimitVoiceCon = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_VOICE_CON);
	}

	public boolean ratelimitVoiceReq() {
		RateLimiterBasic limiter = ratelimitVoiceReq;
		if(limiter == null) {
			limiter = ratelimitVoiceReq = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_VOICE_REQ);
	}

	public boolean ratelimitVoiceICE() {
		RateLimiterBasic limiter = ratelimitVoiceICE;
		if(limiter == null) {
			limiter = ratelimitVoiceICE = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_VOICE_ICE);
	}

	public boolean ratelimitBrand() {
		RateLimiterBasic limiter = ratelimitBrand;
		if(limiter == null) {
			limiter = ratelimitBrand = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_BRAND);
	}

	public boolean ratelimitWebViewData() {
		RateLimiterBasic limiter = ratelimitWebViewData;
		if(limiter == null) {
			limiter = ratelimitWebViewData = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_WEBVIEW_DATA);
	}

	public boolean ratelimitWebViewMsg() {
		RateLimiterBasic limiter = ratelimitWebViewMsg;
		if(limiter == null) {
			limiter = ratelimitWebViewMsg = new RateLimiterBasic();
		}
		return limiter.rateLimit(LIMIT_WEBVIEW_MSG);
	}

}
