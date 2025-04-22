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

package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.io.File;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class YAMLConfigLoader {

	private static final boolean MODERN;

	static {
		boolean b = false;
		try {
			Class.forName("org.yaml.snakeyaml.LoaderOptions").getMethod("setProcessComments", boolean.class);
			b = true;
		}catch(ReflectiveOperationException ex) {
		}
		MODERN = b;
	}

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		if(MODERN) {
			return net.lax1dude.eaglercraft.backend.server.config.snakeyaml.modern.YAMLConfigLoader.getConfigFile(file);
		}else {
			return net.lax1dude.eaglercraft.backend.server.config.snakeyaml.legacy.YAMLConfigLoader.getConfigFile(file);
		}
	}

}
