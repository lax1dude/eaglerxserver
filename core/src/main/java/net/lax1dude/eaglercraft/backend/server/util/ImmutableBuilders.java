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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.google.common.collect.ImmutableList;

public class ImmutableBuilders {

	private static final MethodHandle builderWithExpectedSizeMethod;

	static {
		MethodHandle m = null;
		try {
			m = MethodHandles.lookup().findStatic(ImmutableList.class, "builderWithExpectedSize", MethodType.methodType(ImmutableList.Builder.class, int.class));
			ImmutableList.class.getMethod("builderWithExpectedSize", int.class);
		} catch (ReflectiveOperationException ex) {
		}
		builderWithExpectedSizeMethod = m;
	}

	public static <T> ImmutableList.Builder<T> listBuilderWithExpected(int cnt) {
		if (builderWithExpectedSizeMethod != null) {
			try {
				return (ImmutableList.Builder<T>) builderWithExpectedSizeMethod.invokeExact(cnt);
			} catch (Throwable e) {
				throw Util.propagateInvokeThrowable(e);
			}
		} else {
			return ImmutableList.builder();
		}
	}

}
