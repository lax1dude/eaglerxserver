/*
 * Copyright (c) 2025 ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import net.lax1dude.eaglercraft.backend.server.api.IComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.nbt.EnumDataType;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTValue;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTVisitor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RewindNBTVisitorReverse implements INBTVisitor {

	public static void apply(INBTContext context, DataInput input, DataOutput output, IComponentHelper componentHelper) throws IOException {
		context.accept(input, new RewindNBTVisitorReverse(context, context.createWriter(output), componentHelper));
	}

	private final INBTContext context;
	private final INBTVisitor parent;

	private final IComponentHelper componentHelper;

	private RewindNBTVisitorReverse(INBTContext context, INBTVisitor parent, IComponentHelper componentHelper) {
		this.context = context;
		this.parent = parent;
		this.componentHelper = componentHelper;
	}

	@Override
	public INBTVisitor parent() {
		return parent;
	}

	@Override
	public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
		parent().visitRootTag(tagType);
		return this;
	}

	@Override
	public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
		parent().visitTagList(itemType, length);
		return this;
	}

	@Override
	public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
		if(tagType == EnumDataType.COMPOUND && "ExtraType".equals(tagName.value())) {
			tagName.mutate("Owner");
		} else if(tagType == EnumDataType.LIST && "pages".equals(tagName.value())) {
			parent().visitTag(tagType, tagName);
			return new PagesTransformer();
		}
		parent().visitTag(tagType, tagName);
		return this;
	}

	private class PagesTransformer implements INBTVisitor {
		@Override
		public INBTVisitor parent() {
			return RewindNBTVisitorReverse.this;
		}

		private int w = Integer.MAX_VALUE;
		private int len = Integer.MAX_VALUE;

		@Override
		public INBTVisitor visitRootTag(EnumDataType tagType) throws IOException {
			if (w >= len) {
				return parent().visitRootTag(tagType);
			}
			parent().visitRootTag(tagType);
			return this;
		}

		@Override
		public INBTVisitor visitTagList(EnumDataType itemType, int length) throws IOException {
			if (length != 0 && w == Integer.MAX_VALUE && len == Integer.MAX_VALUE) {
				len = length;
				parent().visitTagList(itemType, length);
				return this;
			} else {
				return parent().visitTagList(itemType, length);
			}
		}

		@Override
		public INBTVisitor visitTag(EnumDataType tagType, INBTValue<String> tagName) throws IOException {
			if (w >= len) {
				return parent().visitTag(tagType, tagName);
			}
			if(tagType == EnumDataType.STRING) {
				w++;
			}
			parent().visitTag(tagType, tagName);
			return this;
		}

		@Override
		public void visitTagString(INBTValue<String> str) throws IOException {
			if (w >= len) {
				parent().visitTagString(str);
				return;
			}
			String transformedText = componentHelper.serializeLegacyTextToLegacyJSON(str.value());
			parent().visitTagString(context.wrapValue(transformedText));
		}
	}
}
