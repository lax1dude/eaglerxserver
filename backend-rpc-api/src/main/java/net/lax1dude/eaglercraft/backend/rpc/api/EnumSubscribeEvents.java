/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.EnumSet;
import java.util.Set;

public enum EnumSubscribeEvents {

	/** @see net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent */
	EVENT_WEBVIEW_OPEN_CLOSE(1),

	/** @see net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewMessageEvent */
	EVENT_WEBVIEW_MESSAGE(2),

	/** @see net.lax1dude.eaglercraft.backend.rpc.api.data.WebViewOpenCloseEvent */
	EVENT_TOGGLE_VOICE(4);

	public final int bit;

	private EnumSubscribeEvents(int bit) {
		this.bit = bit;
	}

	public static int toBits(EnumSubscribeEvents... evts) {
		int bits = 0;
		for(int i = 0; i < evts.length; ++i) {
			bits |= evts[i].bit;
		}
		return bits;
	}

	public static Set<EnumSubscribeEvents> fromBits(int bits) {
		Set<EnumSubscribeEvents> set = EnumSet.noneOf(EnumSubscribeEvents.class);
		if((bits & 1) != 0) set.add(EVENT_WEBVIEW_OPEN_CLOSE);
		if((bits & 2) != 0) set.add(EVENT_WEBVIEW_MESSAGE);
		if((bits & 4) != 0) set.add(EVENT_TOGGLE_VOICE);
		return set;
	}

}