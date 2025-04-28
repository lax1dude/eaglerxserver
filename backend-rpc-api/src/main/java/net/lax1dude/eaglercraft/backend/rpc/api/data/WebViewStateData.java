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

package net.lax1dude.eaglercraft.backend.rpc.api.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

public final class WebViewStateData {

	@Nonnull
	public static WebViewStateData create(boolean webViewAllowed, boolean channelAllowed,
			@Nonnull Collection<String> openChannels) {
		if (openChannels == null) {
			throw new NullPointerException("openChannels");
		}
		return new WebViewStateData(webViewAllowed, channelAllowed, ImmutableSet.copyOf(openChannels));
	}

	private static final WebViewStateData DISABLED = new WebViewStateData(false, false, Collections.emptySet());

	@Nonnull
	public static WebViewStateData disabled() {
		return DISABLED;
	}

	private final boolean webViewAllowed;
	private final boolean channelAllowed;
	private final Set<String> openChannels;

	private WebViewStateData(boolean webViewAllowed, boolean channelAllowed, Set<String> openChannels) {
		this.webViewAllowed = webViewAllowed;
		this.channelAllowed = channelAllowed;
		this.openChannels = openChannels;
	}

	public boolean isWebViewAllowed() {
		return webViewAllowed;
	}

	public boolean isChannelAllowed() {
		return channelAllowed;
	}

	@Nonnull
	public Set<String> getOpenChannels() {
		return openChannels;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + (channelAllowed ? 1231 : 1237);
		result = 31 * result + openChannels.hashCode();
		result = 31 * result + (webViewAllowed ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WebViewStateData other))
			return false;
		if (channelAllowed != other.channelAllowed)
			return false;
		if (!openChannels.equals(other.openChannels))
			return false;
		if (webViewAllowed != other.webViewAllowed)
			return false;
		return true;
	}

}
