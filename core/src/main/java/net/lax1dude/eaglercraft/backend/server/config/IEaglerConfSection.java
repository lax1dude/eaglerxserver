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

package net.lax1dude.eaglercraft.backend.server.config;

import java.util.List;
import java.util.function.Supplier;

public interface IEaglerConfSection {

	boolean exists();

	boolean initialized();

	void setComment(String comment);

	IEaglerConfSection getIfSection(String name);

	IEaglerConfSection getSection(String name);

	IEaglerConfList getIfList(String name);

	IEaglerConfList getList(String name);

	List<String> getKeys();

	boolean isBoolean(String name);

	boolean getBoolean(String name);

	boolean getBoolean(String name, boolean defaultValue, String comment);

	default boolean getBoolean(String name, boolean defaultValue) {
		return getBoolean(name, defaultValue, null);
	}

	boolean getBoolean(String name, Supplier<Boolean> defaultValue, String comment);

	default boolean getBoolean(String name, Supplier<Boolean> defaultValue) {
		return getBoolean(name, defaultValue, null);
	}

	boolean isInteger(String name);

	int getInteger(String name, int defaultValue, String comment);

	default int getInteger(String name, int defaultValue) {
		return getInteger(name, defaultValue, null);
	}

	int getInteger(String name, Supplier<Integer> defaultValue, String comment);

	default int getInteger(String name, Supplier<Integer> defaultValue) {
		return getInteger(name, defaultValue, null);
	}

	boolean isString(String name);

	String getIfString(String name);

	String getString(String name, String defaultValue, String comment);

	default String getString(String name, String defaultValue) {
		return getString(name, defaultValue, null);
	}

	String getString(String name, Supplier<String> defaultValue, String comment);

	default String getString(String name, Supplier<String> defaultValue) {
		return getString(name, defaultValue, null);
	}

}
