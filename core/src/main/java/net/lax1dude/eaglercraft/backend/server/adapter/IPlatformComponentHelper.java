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

package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformComponentHelper {

	IPlatformComponentBuilder builder();

	Class<?> getComponentType();

	Object getStandardKickAlreadyPlaying();

	String serializeLegacySection(Object component);

	String serializePlainText(Object component);

	/**
	 * Use for components that do not contain hover events
	 */
	String serializeGenericJSON(Object component);

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	String serializeLegacyJSON(Object component);

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	String serializeModernJSON(Object component);

	/**
	 * Use for components that do not contain hover events
	 */
	Object parseGenericJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on 1.8 clients
	 */
	Object parseLegacyJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for components that contain hover events, on modern clients
	 */
	Object parseModernJSON(String json) throws IllegalArgumentException;

	/**
	 * Use for simple components, on legacy clients
	 */
	Object parseLegacyText(String string) throws IllegalArgumentException;

}
