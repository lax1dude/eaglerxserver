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

package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.base.message.ByteBufInputWrapper;
import net.lax1dude.eaglercraft.backend.server.base.message.ByteBufOutputWrapper;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.util.IInjectedPayload;

class InjectedRPCPayload implements IInjectedPayload {

	private final String name;
	private final ISupervisorData data;

	InjectedRPCPayload(String name, ISupervisorData data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public int writePayload(ByteBuf buf) {
		int ret = buf.writeCharSequence(name, StandardCharsets.US_ASCII);
		serialize(buf, data);
		return ret;
	}

	static void serialize(ByteBuf buf, ISupervisorData data) {
		try {
			data.write(new ByteBufOutputWrapper(buf));
		} catch (Exception e) {
			throw new IllegalStateException("Failed to serialize supervisor data: " + data, e);
		}
	}

	static ISupervisorData deserialize(ByteBuf buf, SupervisorDataType dataType) throws Exception {
		if (dataType == SupervisorDataType.VOID_TYPE) {
			return ISupervisorData.VOID;
		} else {
			if (buf == null) {
				buf = Unpooled.EMPTY_BUFFER;
			}
			ISupervisorData ret;
			try {
				ret = dataType.ctor.newInstance();
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
			ret.read(new ByteBufInputWrapper(buf));
			return ret;
		}
	}

}
