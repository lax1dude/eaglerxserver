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

package net.lax1dude.eaglercraft.backend.server.base;

public class CapabilityBits {

	public static boolean hasCapability(int mask, byte[] vers, int id, int ver) {
		int bit = 1 << id;
		if ((mask & bit) != 0) {
			int versIndex = Integer.bitCount(mask & (bit - 1));
			if (versIndex < vers.length) {
				return (vers[versIndex] & 0xFF) >= ver;
			}
		}
		return false;
	}

	public static int getCapability(int mask, byte[] vers, int id) {
		int bit = 1 << id;
		if ((mask & bit) != 0) {
			int versIndex = Integer.bitCount(mask & (bit - 1));
			if (versIndex < vers.length) {
				return vers[versIndex] & 0xFF;
			}
		}
		return -1;
	}

}
