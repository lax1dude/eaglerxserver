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

package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.lax1dude.eaglercraft.backend.rpc.api.IRPCAttributeHolder;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCAttributeKey;

public class RPCAttributeHolder implements IRPCAttributeHolder {

	private final ConcurrentMap<RPCAttributeKey<?>, Object> map = new ConcurrentHashMap<>();

	@Override
	public <T> void set(RPCAttributeKey<T> key, T value) {
		if(key == null) {
			throw new NullPointerException("key");
		}
		if(value != null) {
			map.put(key, value);
		}else {
			map.remove(key);
		}
	}

	@Override
	public <T> T get(RPCAttributeKey<T> key) {
		if(key == null) {
			throw new NullPointerException("key");
		}
		return (T) map.get(key);
	}

}
