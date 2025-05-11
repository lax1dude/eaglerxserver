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

package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDImpl;
import net.lax1dude.eaglercraft.backend.eaglermotd.adapter.IEaglerMOTDPlatform;

/**
 * Class to invoke the EaglerMOTD constructor without a static dependency
 */
public class EaglerMOTDFactory {

	@SuppressWarnings("unchecked")
	public static <PlayerObject> IEaglerMOTDImpl<PlayerObject> create(IEaglerMOTDPlatform<PlayerObject> platform) {
		try {
			Class<?> clz = Class.forName("net.lax1dude.eaglercraft.backend.eaglermotd.base.EaglerMOTD");
			return (IEaglerMOTDImpl<PlayerObject>) clz.getConstructor(IEaglerMOTDPlatform.class).newInstance(platform);
		} catch (ReflectiveOperationException ex) {
			throw new RuntimeException(ex);
		}
	}

}
