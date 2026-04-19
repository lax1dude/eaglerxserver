/*
 * Copyright (c) 2026 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.server.config.docsutil;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfList;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;

class DocsList implements IEaglerConfList {

	final List<Object> entries = new ArrayList<>();
	private boolean initialized;
	String comment;

	DocsList() {
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public boolean initialized() {
		return initialized;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public IEaglerConfSection appendSection() {
		DocsSection object = new DocsSection();
		entries.add(object);
		initialized = true;
		return object;
	}

	@Override
	public IEaglerConfList appendList() {
		DocsList object = new DocsList();
		entries.add(object);
		initialized = true;
		return object;
	}

	@Override
	public void appendInteger(int value) {
		DocsValue object = new DocsValue(DocsValue.Type.INT, Integer.toString(value), null, false);
		entries.add(object);
		initialized = true;
	}

	@Override
	public void appendString(String string) {
		DocsValue object = new DocsValue(DocsValue.Type.STR, string, null, false);
		entries.add(object);
		initialized = true;
	}

	@Override
	public int getLength() {
		return entries.size();
	}

	@Override
	public IEaglerConfSection getIfSection(int index) {
		if (index < 0 || index >= entries.size())
			return null;
		if (entries.get(index) instanceof DocsSection s) {
			return s;
		}
		return null;
	}

	@Override
	public IEaglerConfList getIfList(int index) {
		if (index < 0 || index >= entries.size())
			return null;
		if (entries.get(index) instanceof DocsList l) {
			return l;
		}
		return null;
	}

	@Override
	public boolean isInteger(int index) {
		if (index < 0 || index >= entries.size())
			return false;
		if (entries.get(index) instanceof DocsValue v) {
			return v.type == DocsValue.Type.INT;
		}
		return false;
	}

	@Override
	public int getIfInteger(int index, int defaultVal) {
		if (index < 0 || index >= entries.size())
			return defaultVal;
		if (entries.get(index) instanceof DocsValue v && v.type == DocsValue.Type.INT) {
			return Integer.parseInt(v.value);
		}
		return defaultVal;
	}

	@Override
	public boolean isString(int index) {
		if (index < 0 || index >= entries.size())
			return false;
		if (entries.get(index) instanceof DocsValue v) {
			return v.type == DocsValue.Type.STR;
		}
		return false;
	}

	@Override
	public String getIfString(int index, String defaultVal) {
		if (index < 0 || index >= entries.size())
			return defaultVal;
		if (entries.get(index) instanceof DocsValue v && v.type == DocsValue.Type.STR) {
			return v.value;
		}
		return defaultVal;
	}

}
