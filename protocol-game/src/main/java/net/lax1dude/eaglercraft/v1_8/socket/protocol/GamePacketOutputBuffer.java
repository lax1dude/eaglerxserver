package net.lax1dude.eaglercraft.v1_8.socket.protocol;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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
public interface GamePacketOutputBuffer extends DataOutput {

	void writeVarInt(int i) throws IOException;

	void writeVarLong(long i) throws IOException;

	void writeStringMC(String str) throws IOException;

	void writeStringEaglerASCII8(String str) throws IOException;

	void writeStringEaglerASCII16(String str) throws IOException;

	void writeByteArrayMC(byte[] bytes) throws IOException;

	OutputStream stream();

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

	public static int getArrayMCSize(int len) {
		return getVarIntSize(len) + len;
	}
}
