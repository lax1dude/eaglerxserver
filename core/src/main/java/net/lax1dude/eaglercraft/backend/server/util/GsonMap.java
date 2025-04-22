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

import java.lang.reflect.Field;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonMap {

	private static final Field mapField;

	static {
		Field _mapField = null;
		try {
			JsonObject.class.getMethod("asMap");
		}catch(ReflectiveOperationException ex) {
			try {
				_mapField = JsonObject.class.getDeclaredField("members");
				_mapField.setAccessible(true);
			}catch(ReflectiveOperationException exx) {
				throw new ExceptionInInitializerError(exx);
			}
		}
		mapField = _mapField;
	}

	public static Map<String, JsonElement> asMap(JsonObject object) {
		if(mapField == null) {
			return object.asMap();
		}else {
			try {
				return (Map<String, JsonElement>) mapField.get(object);
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
		}
	}

}
