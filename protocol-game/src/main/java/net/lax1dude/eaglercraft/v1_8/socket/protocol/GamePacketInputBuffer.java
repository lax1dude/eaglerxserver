/*
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

package net.lax1dude.eaglercraft.v1_8.socket.protocol;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

public interface GamePacketInputBuffer extends DataInput {

	void skipAllBytes(int n) throws IOException;

	int readVarInt() throws IOException;

	long readVarLong() throws IOException;

	String readStringMC(int maxLen) throws IOException;

	String readStringEaglerASCII8() throws IOException;

	String readStringEaglerASCII16() throws IOException;

	byte[] readByteArrayMC() throws IOException;

	int available() throws IOException;

	InputStream stream();

	byte[] toByteArray() throws IOException;

}
