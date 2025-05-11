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

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebImpl;
import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebPlatform;

/**
 * Class to invoke the EaglerWeb constructor without a static dependency
 */
public class EaglerWebFactory {

	@SuppressWarnings("unchecked")
	public static <PlayerObject> IEaglerWebImpl<PlayerObject> create(IEaglerWebPlatform<PlayerObject> platform) {
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWeb");
			return (IEaglerWebImpl<PlayerObject>) clz.getConstructor(IEaglerWebPlatform.class).newInstance(platform);
		} catch (ReflectiveOperationException ex) {
			throw new RuntimeException(ex);
		}
	}

}
