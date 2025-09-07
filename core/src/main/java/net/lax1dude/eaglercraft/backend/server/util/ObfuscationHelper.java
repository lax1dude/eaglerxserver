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
import java.lang.reflect.Method;

public class ObfuscationHelper {
    public static Method method(
            Class<?> type,
            String[] nameCandidates,
            Class<?> returnTypeOrNull,
            Class<?>... params) {
        for (String n : nameCandidates) {
            if (n == null || n.isEmpty())
                continue;
            try {
                return accessible(type.getMethod(n, params));
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                return accessible(type.getDeclaredMethod(n, params));
            } catch (ReflectiveOperationException ignored) {
            }
        }
        Method best = null;
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getParameterCount() != params.length)
                    continue;
                boolean ok = true;
                Class<?>[] ps = m.getParameterTypes();
                for (int i = 0; i < ps.length && ok; i++)
                    ok = ps[i] == params[i];
                if (!ok)
                    continue;
                if (returnTypeOrNull != null && !returnTypeOrNull.isAssignableFrom(m.getReturnType()))
                    continue;
                if (best != null)
                    throw new IllegalStateException("Ambiguous method on " + type + ": " + best + " vs " + m);
                best = m;
            }
        }
        if (best != null)
            return accessible(best);
        throw new IllegalStateException("Method not found on " + type.getName());
    }

    public static Method method(
            Class<?> type,
            String[] nameCandidates,
            java.util.function.Predicate<Method> accept) {
        for (String n : nameCandidates) {
            if (n == null || n.isEmpty())
                continue;
            try {
                Method m = accessible(type.getMethod(n));
                if (accept.test(m))
                    return m;
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                Method m = accessible(type.getDeclaredMethod(n));
                if (accept.test(m))
                    return m;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        Method best = null;
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (!accept.test(m))
                    continue;
                if (best != null)
                    throw new IllegalStateException("Ambiguous method on " + type + ": " + best + " vs " + m);
                best = m;
            }
        }
        if (best != null)
            return accessible(best);
        throw new IllegalStateException("Method not found by predicate on " + type.getName());
    }

    public static Field field(Class<?> type, String[] nameCandidates, Class<?> fieldTypeOrNull) {
        for (String n : nameCandidates) {
            if (n == null || n.isEmpty())
                continue;
            try {
                return accessible(type.getField(n));
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                return accessible(type.getDeclaredField(n));
            } catch (ReflectiveOperationException ignored) {
            }
        }
        Field best = null;
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (fieldTypeOrNull != null && f.getType() != fieldTypeOrNull)
                    continue;
                if (best != null)
                    throw new IllegalStateException("Ambiguous field on " + type + ": " + best + " vs " + f);
                best = f;
            }
        }
        if (best != null)
            return accessible(best);
        throw new IllegalStateException("Field not found on " + type.getName());
    }

    private static <T extends java.lang.reflect.AccessibleObject> T accessible(T m) {
        m.setAccessible(true);
        return m;
    }
}
