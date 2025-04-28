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

package net.lax1dude.eaglercraft.backend.supervisor.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.base.Strings;

public class TableRenderer {

	private final List<String[]> rows = new ArrayList<>();

	public TableRenderer pushRow(List<Object> row) {
		int len = row.size();
		String[] strs = new String[len];
		for (int i = 0; i < len; ++i) {
			strs[i] = Objects.toString(row.get(i));
		}
		rows.add(strs);
		return this;
	}

	public TableRenderer pushRow(Object... row) {
		String[] strs = new String[row.length];
		for (int i = 0; i < row.length; ++i) {
			strs[i] = Objects.toString(row[i]);
		}
		rows.add(strs);
		return this;
	}

	public void print(Consumer<String> printer) {
		int rowCount = rows.size();
		int colCount = 0;
		int j;
		for (int i = 0; i < rowCount; ++i) {
			String[] str = rows.get(i);
			j = str.length;
			if (j > colCount) {
				colCount = j;
			}
		}
		int[] maxes = new int[colCount];
		for (int i = 0; i < rowCount; ++i) {
			String[] str = rows.get(i);
			for (int k = 0; k < str.length; ++k) {
				j = str[k].length();
				if (j > maxes[k]) {
					maxes[k] = j;
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		builder.append('+');
		for (int i = 0; i < colCount; ++i) {
			builder.append(Strings.repeat("-", maxes[i]));
			builder.append('+');
		}
		String seperator = builder.toString();
		printer.accept(seperator);
		for (int i = 0; i < rowCount; ++i) {
			String[] str = rows.get(i);
			builder = new StringBuilder();
			builder.append('|');
			for (int k = 0; k < colCount; ++k) {
				builder.append(str[k]);
				builder.append(Strings.repeat(" ", maxes[k] - str[k].length()));
				builder.append('|');
			}
			printer.accept(builder.toString());
			printer.accept(seperator);
		}
	}

}