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

package net.lax1dude.eaglercraft.backend.server.util;

public final class SemanticVersion {

	public static SemanticVersion parse(String str) {
		int a = str.indexOf('.');
		if (a == -1) {
			throw new IllegalArgumentException();
		}
		int b = str.indexOf('.', a + 1);
		if (b == -1) {
			throw new IllegalArgumentException();
		}
		return new SemanticVersion(Integer.parseInt(str.substring(0, a)), Integer.parseInt(str.substring(a + 1, b)),
				Integer.parseInt(str.substring(b + 1)));
	}

	private final int major;
	private final int minor;
	private final int patch;

	public SemanticVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public boolean greaterThan(SemanticVersion ver) {
		return major > ver.major
				|| (major == ver.major && (minor > ver.minor || (minor == ver.minor && patch > ver.patch)));
	}

	@Override
	public String toString() {
		return major + "." + minor + "." + patch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + patch;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof SemanticVersion other) && major == other.major && minor == other.minor
				&& patch == other.patch);
	}

}
