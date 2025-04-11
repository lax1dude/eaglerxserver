package net.lax1dude.eaglercraft.backend.server.api.supervisor;

import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;

/**
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
public final class NodeResult<Out extends ISupervisorData> {

	public static <Out extends ISupervisorData> NodeResult<Out> create(int nodeId, Out result) {
		return new NodeResult<>(nodeId, result);
	}

	private final int nodeId;
	private final Out result;

	private NodeResult(int nodeId, Out result) {
		this.nodeId = nodeId;
		this.result = result;
	}

	public boolean isSuccessful() {
		return result != null;
	}

	public int getNodeId() {
		return nodeId;
	}

	public Out getResult() {
		return result;
	}

}