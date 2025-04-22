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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessagePool {
	
	public final String poolName;
	public final List<MessagePoolEntry> messagePool = new ArrayList<>();
	
	public MessagePool(String s) {
		this.poolName = s;
	}
	
	public void sort() {
		Collections.sort(messagePool);
	}
	
	public MessagePoolEntry pickNew() {
		if(messagePool.size() <= 0) {
			return null;
		}
		float f = 0.0f;
		for(MessagePoolEntry m : messagePool) {
			f += m.weight;
		}
		f *= ThreadLocalRandom.current().nextFloat();
		float f2 = 0.0f;
		for(MessagePoolEntry m : messagePool) {
			f2 += m.weight;
			if(f2 >= f) {
				return m;
			}
		}
		return messagePool.get(0);
	}

	public MessagePoolEntry pickDefault() {
		for(MessagePoolEntry m : messagePool) {
			if("default".equalsIgnoreCase(m.name)) {
				return m;
			}
		}
		return pickNew();
	}

}
