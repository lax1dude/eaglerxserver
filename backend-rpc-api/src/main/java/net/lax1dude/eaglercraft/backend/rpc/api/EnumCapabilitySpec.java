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

package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum EnumCapabilitySpec {
	UPDATE_V0(EnumCapabilityType.UPDATE, 0),
	VOICE_V0(EnumCapabilityType.VOICE, 0),
	REDIRECT_V0(EnumCapabilityType.REDIRECT, 0),
	NOTIFICATION_V0(EnumCapabilityType.NOTIFICATION, 0),
	PAUSE_MENU_V0(EnumCapabilityType.PAUSE_MENU, 0),
	WEBVIEW_V0(EnumCapabilityType.WEBVIEW, 0),
	COOKIE_V0(EnumCapabilityType.COOKIE, 0),
	EAGLER_IP_V0(EnumCapabilityType.EAGLER_IP, 0);

	private final EnumCapabilityType type;
	private final int ver;

	private EnumCapabilitySpec(EnumCapabilityType type, int ver) {
		this.type = type;
		this.ver = ver;
	}

	@Nonnull
	public EnumCapabilityType getType() {
		return type;
	}

	public int getId() {
		return type.getId();
	}

	public int getBit() {
		return type.getBit();
	}

	public int getVer() {
		return ver;
	}

	@Nullable
	public static EnumCapabilitySpec fromId(int id, int ver) {
		if (id >= 0 && id < capsMap.length) {
			EnumCapabilitySpec[] cap = capsMap[id];
			if (cap != null) {
				if (ver >= 0 && ver < cap.length) {
					return cap[ver];
				}
			}
		}
		return null;
	}

	private static final EnumCapabilitySpec[][] capsMap = new EnumCapabilitySpec[16][];

	static {
		for (EnumCapabilitySpec cap : values()) {
			int id = cap.type.getId();
			EnumCapabilitySpec[] lst = capsMap[id];
			if (lst == null) {
				capsMap[id] = lst = new EnumCapabilitySpec[16];
			}
			if (lst.length <= cap.ver) {
				EnumCapabilitySpec[] newList = new EnumCapabilitySpec[lst.length << 1];
				System.arraycopy(lst, 0, newList, 0, lst.length);
				capsMap[id] = lst = newList;
			}
			lst[cap.ver] = cap;
		}
	}

}
