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

import io.netty.util.AttributeKey;

public class PipelineAttributes {

	public static final AttributeKey<Object> EAGLER_LISTENER_DATA = AttributeKey.valueOf("$eagler0");
	public static final AttributeKey<Object> EAGLER_PIPELINE_DATA = AttributeKey.valueOf("$eagler1");
	public static final AttributeKey<Object> EAGLER_CONNECTION_DATA = AttributeKey.valueOf("$eagler2");

	public static <T> AttributeKey<T> listenerData() {
		return (AttributeKey<T>) EAGLER_LISTENER_DATA;
	}

	public static <T> AttributeKey<T> pipelineData() {
		return (AttributeKey<T>) EAGLER_PIPELINE_DATA;
	}

	public static <T> AttributeKey<T> connectionData() {
		return (AttributeKey<T>) EAGLER_CONNECTION_DATA;
	}

}
