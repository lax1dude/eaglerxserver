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

package net.lax1dude.eaglercraft.backend.supervisor.status;

public class SkinCacheStatus {

	public final int eaglerPlayerPreset;
	public final int eaglerPlayerCustom;
	public final boolean downloadEnabled;
	public final int downloadedInMemory;
	public final int downloadedInDatabase;

	public SkinCacheStatus(int eaglerPlayerPreset, int eaglerPlayerCustom, boolean downloadEnabled,
			int downloadedInMemory, int downloadedInDatabase) {
		this.eaglerPlayerPreset = eaglerPlayerPreset;
		this.eaglerPlayerCustom = eaglerPlayerCustom;
		this.downloadEnabled = downloadEnabled;
		this.downloadedInMemory = downloadedInMemory;
		this.downloadedInDatabase = downloadedInDatabase;
	}

}