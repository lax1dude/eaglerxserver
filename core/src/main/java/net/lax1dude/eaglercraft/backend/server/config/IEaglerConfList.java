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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface IEaglerConfList {

	boolean exists();

	boolean initialized();

	void setComment(String comment);

	IEaglerConfSection appendSection();

	IEaglerConfList appendList();

	void appendInteger(int value);

	default void appendIntegers(List<Integer> lst) {
		lst.forEach((i) -> this.appendInteger(i));
	}

	void appendString(String string);

	default void appendStrings(List<String> lst) {
		lst.forEach(this::appendString);
	}

	int getLength();

	IEaglerConfSection getIfSection(int index);

	IEaglerConfList getIfList(int index);

	boolean isInteger(int index);

	int getIfInteger(int index, int defaultVal);

	default int getIfInteger(int index) {
		return getIfInteger(index, -1);
	}

	boolean isString(int index);

	String getIfString(int index, String defaultVal);

	default String getIfString(int index) {
		return getIfString(index, null);
	}

	default List<Integer> getAsIntegerList() {
		int len = getLength();
		List<Integer> ret = new ArrayList<>(len);
		for(int i = 0; i < len; ++i) {
			if(isInteger(i)) {
				ret.add(getIfInteger(i));
			}
		}
		return ret;
	}

	default List<Integer> getAsIntegerList(Supplier<List<Integer>> defaultContents) {
		if(!initialized()) {
			appendIntegers(defaultContents.get());
		}
		return getAsIntegerList();
	}

	default List<Integer> getAsIntegerList(Supplier<List<Integer>> defaultContents, String comment) {
		if(!initialized()) {
			setComment(comment);
			appendIntegers(defaultContents.get());
		}
		return getAsIntegerList();
	}

	default List<String> getAsStringList() {
		int len = getLength();
		List<String> ret = new ArrayList<>(len);
		for(int i = 0; i < len; ++i) {
			String str = getIfString(i);
			if(str != null) {
				ret.add(str);
			}
		}
		return ret;
	}

	default List<String> getAsStringList(Supplier<List<String>> defaultContents) {
		if(!initialized()) {
			appendStrings(defaultContents.get());
		}
		return getAsStringList();
	}

	default List<String> getAsStringList(Supplier<List<String>> defaultContents, String comment) {
		if(!initialized()) {
			setComment(comment);
			appendStrings(defaultContents.get());
		}
		return getAsStringList();
	}

}
