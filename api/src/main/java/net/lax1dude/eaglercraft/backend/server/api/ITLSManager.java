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

import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

public interface ITLSManager {

	@Nonnull
	IEaglerListenerInfo getListener();

	default void setCertificate(@Nonnull File fullChain, @Nonnull File privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull File fullChain, @Nonnull File privateKey, @Nullable String privateKeyPassword)
			throws TLSManagerException;

	default void setCertificate(@Nonnull @WillNotClose InputStream fullChain,
			@Nonnull @WillNotClose InputStream privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull InputStream fullChain, @Nonnull InputStream privateKey,
			@Nullable String privateKeyPassword) throws TLSManagerException;

	default void setCertificate(@Nonnull byte[] fullChain, @Nonnull byte[] privateKey) throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull byte[] fullChain, @Nonnull byte[] privateKey, @Nullable String privateKeyPassword)
			throws TLSManagerException;

	default void setCertificate(@Nonnull X509Certificate[] fullChain, @Nonnull PrivateKey privateKey)
			throws TLSManagerException {
		setCertificate(fullChain, privateKey, null);
	}

	void setCertificate(@Nonnull X509Certificate[] fullChain, @Nonnull PrivateKey privateKey,
			@Nullable String privateKeyPassword) throws TLSManagerException;

}
