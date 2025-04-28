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

package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IComponentSerializer;
import net.lax1dude.eaglercraft.backend.server.base.config.ChatColor;

public class ComponentHelper<ComponentType> implements IComponentSerializer<ComponentType> {

	private final IPlatformComponentHelper platformImpl;

	public ComponentHelper(IPlatformComponentHelper platformImpl) {
		this.platformImpl = platformImpl;
	}

	@Override
	public String serializeLegacySection(ComponentType component) {
		if (component == null) {
			throw new NullPointerException("component is null");
		}
		return platformImpl.serializeLegacySection(component);
	}

	@Override
	public String serializePlainText(ComponentType component) {
		if (component == null) {
			throw new NullPointerException("component is null");
		}
		return platformImpl.serializePlainText(component);
	}

	@Override
	public String serializeGenericJSON(ComponentType component) {
		if (component == null) {
			throw new NullPointerException("component is null");
		}
		return platformImpl.serializeGenericJSON(component);
	}

	@Override
	public String serializeLegacyJSON(ComponentType component) {
		if (component == null) {
			throw new NullPointerException("component is null");
		}
		return platformImpl.serializeLegacyJSON(component);
	}

	@Override
	public String serializeModernJSON(ComponentType component) {
		if (component == null) {
			throw new NullPointerException("component is null");
		}
		return platformImpl.serializeModernJSON(component);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentType parseGenericJSON(String json) throws IllegalArgumentException {
		if (json == null) {
			throw new NullPointerException("json is null");
		}
		return (ComponentType) platformImpl.parseGenericJSON(json);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentType parseLegacyJSON(String json) throws IllegalArgumentException {
		if (json == null) {
			throw new NullPointerException("json is null");
		}
		return (ComponentType) platformImpl.parseLegacyJSON(json);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ComponentType parseModernJSON(String json) throws IllegalArgumentException {
		if (json == null) {
			throw new NullPointerException("json is null");
		}
		return (ComponentType) platformImpl.parseModernJSON(json);
	}

	@Override
	public String convertJSONToLegacySection(String json) throws IllegalArgumentException {
		if (json == null) {
			throw new NullPointerException("json is null");
		}
		return platformImpl.serializeLegacySection(platformImpl.parseGenericJSON(json));
	}

	@Override
	public String convertJSONToPlainText(String json) throws IllegalArgumentException {
		if (json == null) {
			throw new NullPointerException("json is null");
		}
		return platformImpl.serializePlainText(platformImpl.parseGenericJSON(json));
	}

	@Override
	public String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		if (textToTranslate == null) {
			throw new NullPointerException("textToTranslate is null");
		}
		return ChatColor.translateAlternateColorCodes(altColorChar, textToTranslate);
	}

}
