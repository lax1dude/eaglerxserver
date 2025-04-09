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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt;

import io.netty.buffer.ByteBuf;

public interface EaglerSupervisorPacket {

	void readPacket(ByteBuf buffer);

	void writePacket(ByteBuf buffer);

	void handlePacket(EaglerSupervisorHandler handler);

	public static int getVarIntSize(int input) {
		for (int i = 1; i < 5; ++i) {
			if ((input & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 5;
	}

	public static int getVarLongSize(long input) {
		for (int i = 1; i < 9; ++i) {
			if ((input & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 9;
	}

	public static int readVarInt(ByteBuf buffer) {
		int i = 0;
		int j = 0;

		while (true) {
			int b0 = buffer.readUnsignedByte();
			i |= (b0 & 127) << j++ * 7;
			if (j > 5) {
				throw new IllegalArgumentException("VarInt too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}

		return i;
	}

	public static long readVarLong(ByteBuf buffer) {
		long i = 0L;
		int j = 0;

		while (true) {
			int b0 = buffer.readUnsignedByte();
			i |= (long) (b0 & 127) << j++ * 7;
			if (j > 10) {
				throw new IllegalArgumentException("VarLong too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}

		return i;
	}

	public static void writeVarInt(ByteBuf buffer, int i) {
		while ((i & -128) != 0) {
			buffer.writeByte(i & 127 | 128);
			i >>>= 7;
		}
		buffer.writeByte(i);
	}

	public static void writeVarLong(ByteBuf buffer, long i) {
		while ((i & -128L) != 0L) {
			buffer.writeByte((int) (i & 127L) | 128);
			i >>>= 7;
		}
		buffer.writeByte((int) i);
	}

}