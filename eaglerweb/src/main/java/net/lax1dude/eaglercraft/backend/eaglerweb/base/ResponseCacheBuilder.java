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

package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.lax1dude.eaglercraft.backend.eaglerweb.adapter.IEaglerWebLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataMIMEType;

class ResponseCacheBuilder {

	private final ResponseCache cache;
	private final Map<File, ResponseCacheKey> map = new HashMap<>();
	private final Function<File, ConfigDataMIMEType> typeMapper;
	private final Function<File, ResponseCacheKey> factory;

	ResponseCacheBuilder(long expiresAfter, int maxCacheFiles, int threadCount, IEaglerWebLogger loggerIn,
			Function<File, ConfigDataMIMEType> mimes) {
		cache = new ResponseCache(expiresAfter, maxCacheFiles, threadCount, loggerIn);
		typeMapper = mimes;
		factory = (f) -> {
			return new ResponseCacheKey(f, typeMapper.apply(f));
		};
	}

	ResponseCacheKey createEntry(File file) {
		return map.computeIfAbsent(file, factory);
	}

	ResponseCache build() {
		return cache.start();
	}

}
