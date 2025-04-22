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

package net.lax1dude.eaglercraft.backend.server.api;

import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nonnull;

public final class ExtendedCapabilitySpec {

	@Nonnull
	public static ExtendedCapabilitySpec create(@Nonnull UUID majorVersion, @Nonnull int... minorVersions) {
		return new ExtendedCapabilitySpec(version(majorVersion, minorVersions));
	}

	@Nonnull
	public static ExtendedCapabilitySpec create(@Nonnull Version... majorVersions) {
		return new ExtendedCapabilitySpec(majorVersions);
	}

	@Nonnull
	public static Version version(@Nonnull UUID majorVersion, @Nonnull int... minorVersions) {
		return new Version(majorVersion, minorVersions);
	}

	public static final class Version {

		private final UUID majorVersion;
		private final int[] minorVersions;

		private Version(UUID majorVersion, int... minorVersions) {
			for(int i = 0; i < minorVersions.length; ++i) {
				int j = minorVersions[i];
				if(j < 0 || j > 31) {
					throw new IllegalArgumentException("Illegal subversion " + minorVersions[i] + ", must be between 0 to 31");
				}
			}
			this.majorVersion = majorVersion;
			this.minorVersions = minorVersions;
		}

		@Nonnull
		public UUID getMajorVersion() {
			return majorVersion;
		}

		@Nonnull
		public int[] getMinorVersions() {
			return minorVersions;
		}

		@Override
		public int hashCode() {
			int result = 31 + ((majorVersion == null) ? 0 : majorVersion.hashCode());
			result = 31 * result + Arrays.hashCode(minorVersions);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Version other))
				return false;
			if (majorVersion == null) {
				if (other.majorVersion != null)
					return false;
			} else if (!majorVersion.equals(other.majorVersion))
				return false;
			if (!Arrays.equals(minorVersions, other.minorVersions))
				return false;
			return true;
		}

	}

	private final Version[] majorVersions;

	private ExtendedCapabilitySpec(Version... majorVersions) {
		this.majorVersions = majorVersions;
	}

	@Nonnull
	public Version[] getMajorVersions() {
		return majorVersions;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(majorVersions);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExtendedCapabilitySpec other))
			return false;
		if (!Arrays.equals(majorVersions, other.majorVersions))
			return false;
		return true;
	}

}
