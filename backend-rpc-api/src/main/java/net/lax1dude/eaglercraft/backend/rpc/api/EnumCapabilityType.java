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

import javax.annotation.Nullable;

public enum EnumCapabilityType {
	UPDATE(0),
	VOICE(1),
	REDIRECT(2),
	NOTIFICATION(3),
	PAUSE_MENU(4),
	WEBVIEW(5),
	COOKIE(6),
	EAGLER_IP(7);

	private final int id;
	private final int bit;

	private EnumCapabilityType(int id) {
		this.id = id;
		this.bit = 1 << id;
	}

	public int getId() {
		return id;
	}

	public int getBit() {
		return bit;
	}

	@Nullable
	public static EnumCapabilityType getById(int id) {
		return id >= 0 && id < LOOKUP.length ? LOOKUP[id] : null;
	}

	private static final EnumCapabilityType[] LOOKUP = new EnumCapabilityType[8];

	static {
		for(EnumCapabilityType cap : values()) {
			LOOKUP[cap.id] = cap;
		}
	}

}
