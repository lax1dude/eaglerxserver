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

package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

public enum EnumWebViewPerms {
	JAVASCRIPT(1),
	MESSAGE_API(2),
	STRICT_CSP(4);

	private final int bit;

	private EnumWebViewPerms(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	@Nonnull
	public static Set<EnumWebViewPerms> fromBits(int bits) {
		Set<EnumWebViewPerms> ret = EnumSet.noneOf(EnumWebViewPerms.class);
		if ((bits & 1) != 0)
			ret.add(JAVASCRIPT);
		if ((bits & 2) != 0)
			ret.add(MESSAGE_API);
		if ((bits & 4) != 0)
			ret.add(STRICT_CSP);
		return ret;
	}

	public static int toBits(@Nonnull Set<EnumWebViewPerms> set) {
		int ret = 0;
		for (EnumWebViewPerms perm : set) {
			ret |= perm.bit;
		}
		return ret;
	}

}
