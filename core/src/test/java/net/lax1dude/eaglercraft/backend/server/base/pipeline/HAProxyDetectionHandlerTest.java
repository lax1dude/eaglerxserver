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

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import net.lax1dude.eaglercraft.backend.server.util.IPAddressSet;

public class HAProxyDetectionHandlerTest {

	private static class TestEmbeddedChannel extends EmbeddedChannel {

		private final SocketAddress remoteAddress;

		private TestEmbeddedChannel(SocketAddress remoteAddress, ChannelHandler... handlers) {
			super(handlers);
			this.remoteAddress = remoteAddress;
		}

		@Override
		protected SocketAddress remoteAddress0() {
			return remoteAddress;
		}

	}

	private static class RecordingHandler extends ChannelInboundHandlerAdapter {

		private boolean read;

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			read = true;
			ctx.fireChannelRead(msg);
		}

	}

	@Test
	public void rejectsProxyV2FromUnlistedPeer() {
		RecordingHandler decoder = new RecordingHandler();
		EmbeddedChannel channel = new TestEmbeddedChannel(new InetSocketAddress("203.0.113.8", 25565),
				new HAProxyDetectionHandler(decoder, IPAddressSet.create(Collections.emptyList())), decoder);
		try {
			channel.writeInbound(Unpooled.wrappedBuffer(new byte[] { 0x0D, 0x0A, 0x0D, 0x0A, 0x00, 0x0D, 0x0A,
					0x51, 0x55, 0x49, 0x54, 0x0A }));
			assertFalse(channel.isOpen());
			assertFalse(decoder.read);
		} finally {
			channel.finishAndReleaseAll();
		}
	}

	@Test
	public void rejectsProxyV1FromUnlistedPeer() {
		RecordingHandler decoder = new RecordingHandler();
		EmbeddedChannel channel = new TestEmbeddedChannel(new InetSocketAddress("203.0.113.8", 25565),
				new HAProxyDetectionHandler(decoder, IPAddressSet.create(Collections.emptyList())), decoder);
		try {
			channel.writeInbound(Unpooled.wrappedBuffer("PROXY ".getBytes(StandardCharsets.US_ASCII)));
			assertFalse(channel.isOpen());
			assertFalse(decoder.read);
		} finally {
			channel.finishAndReleaseAll();
		}
	}

	@Test
	public void acceptsProxyV2FromListedPeer() {
		RecordingHandler decoder = new RecordingHandler();
		EmbeddedChannel channel = new TestEmbeddedChannel(new InetSocketAddress("10.20.30.40", 25565),
				new HAProxyDetectionHandler(decoder, IPAddressSet.create(Collections.singletonList("10.0.0.0/8"))),
				decoder);
		try {
			assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(new byte[] { 0x0D, 0x0A, 0x0D, 0x0A, 0x00, 0x0D,
					0x0A, 0x51, 0x55, 0x49, 0x54, 0x0A })));
			assertTrue(channel.isOpen());
			assertNotNull(channel.pipeline().context(decoder));
			assertTrue(decoder.read);
		} finally {
			channel.finishAndReleaseAll();
		}
	}

	@Test
	public void acceptsProxyV1FromListedPeer() {
		RecordingHandler decoder = new RecordingHandler();
		EmbeddedChannel channel = new TestEmbeddedChannel(new InetSocketAddress("2001:db8:1::4", 25565),
				new HAProxyDetectionHandler(decoder,
						IPAddressSet.create(Collections.singletonList("2001:db8:1::/48"))),
				decoder);
		try {
			assertTrue(channel.writeInbound(Unpooled.wrappedBuffer("PROXY ".getBytes(StandardCharsets.US_ASCII))));
			assertTrue(channel.isOpen());
			assertNotNull(channel.pipeline().context(decoder));
			assertTrue(decoder.read);
		} finally {
			channel.finishAndReleaseAll();
		}
	}

	@Test
	public void acceptsDirectTrafficFromUnlistedPeer() {
		RecordingHandler decoder = new RecordingHandler();
		EmbeddedChannel channel = new TestEmbeddedChannel(new InetSocketAddress("203.0.113.8", 25565),
				new HAProxyDetectionHandler(decoder, IPAddressSet.create(Collections.emptyList())), decoder);
		try {
			assertTrue(channel.writeInbound(
					Unpooled.wrappedBuffer("GET / HTTP/1.1\r\n".getBytes(StandardCharsets.US_ASCII))));
			assertTrue(channel.isOpen());
			assertNull(channel.pipeline().context(decoder));
			assertFalse(decoder.read);
			ByteBuf inbound = channel.readInbound();
			try {
				assertEquals("GET / HTTP/1.1\r\n", inbound.toString(StandardCharsets.US_ASCII));
			} finally {
				inbound.release();
			}
		} finally {
			channel.finishAndReleaseAll();
		}
	}

	@Test
	public void matchesIpv4AndIpv6Subnets() {
		IPAddressSet addresses = IPAddressSet.create(Arrays.asList("192.0.2.0/24", "2001:db8:2::/48"));
		assertTrue(addresses.contains(new InetSocketAddress("192.0.2.255", 1)));
		assertFalse(addresses.contains(new InetSocketAddress("192.0.3.1", 1)));
		assertTrue(addresses.contains(new InetSocketAddress("2001:db8:2::ffff", 1)));
		assertFalse(addresses.contains(new InetSocketAddress("2001:db8:3::1", 1)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectsInvalidSubnet() {
		IPAddressSet.create(Collections.singletonList("192.0.2.1/33"));
	}

}
