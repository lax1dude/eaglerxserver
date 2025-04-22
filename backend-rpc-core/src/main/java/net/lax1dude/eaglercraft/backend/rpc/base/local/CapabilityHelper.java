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

package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilitySpec;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumCapabilityType;

class CapabilityHelper {

	static EnumCapabilityType wrap(net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType cap) {
		return EnumCapabilityType.getById(cap.getId());
	}

	static net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType unwrap(EnumCapabilityType cap) {
		return net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType.getById(cap.getId());
	}

	static EnumCapabilitySpec wrap(net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec cap) {
		return EnumCapabilitySpec.fromId(cap.getId(), cap.getVer());
	}

	static net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec unwrap(EnumCapabilitySpec cap) {
		return net.lax1dude.eaglercraft.backend.server.api.EnumCapabilitySpec.fromId(cap.getId(), cap.getVer());
	}

}
