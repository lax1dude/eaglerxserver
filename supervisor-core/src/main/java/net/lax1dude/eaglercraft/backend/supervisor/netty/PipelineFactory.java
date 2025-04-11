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

package net.lax1dude.eaglercraft.backend.supervisor.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorDecoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorEncoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.Varint21FrameDecoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.Varint21FrameEncoder;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.util.ILoggerSv;
import net.lax1dude.eaglercraft.backend.supervisor.server.SupervisorServerHandshakeHandler;
import net.lax1dude.eaglercraft.backend.supervisor.status.HttpStatusRequestHandler;
import net.lax1dude.eaglercraft.backend.supervisor.util.LoggerSv;

public class PipelineFactory {

	private static final Logger logger = LoggerFactory.getLogger("PipelineFactory");

	public static final AttributeKey<SocketAddress> LOCAL_ADDRESS = AttributeKey.newInstance("LocalAddress");

	private static boolean enableEpoll = false;
	private static boolean enableKQueue = false;
	private static int threadPoolSize = 0;

	public static final WriteBufferWaterMark MARK = new WriteBufferWaterMark(524288, 1048576);

	private static final ILoggerSv HANDLER_LOGGER = new LoggerSv(LoggerFactory.getLogger("SupervisorPacketHandler"));

	static {
		String ts = System.getProperty("eaglerxsupervisor.threadPoolSize");
		if(ts != null) {
			try {
				threadPoolSize = Integer.parseInt(ts);
				logger.info("Setting thread pool size: {}", threadPoolSize);
			}catch(NumberFormatException ex) {
				logger.warn("Invalid thread pool size: {}", ts);
			}
		}
		if(!PlatformDependent.isWindows()) {
			if(Boolean.parseBoolean(System.getProperty("eaglerxsupervisor.kqueue", "true"))) {
				if(enableKQueue = KQueue.isAvailable()) {
					logger.info("Enabled kqueue support");
				}else if(System.getProperty("eaglerxsupervisor.kqueue") != null) {
					logger.warn("Tried to enable kqueue, but it is not available!", KQueue.unavailabilityCause());
				}
			}
			if(Boolean.parseBoolean(System.getProperty("eaglerxsupervisor.epoll", "true"))) {
				if(enableEpoll = Epoll.isAvailable()) {
					logger.info("Enabled epoll support");
				}else if(System.getProperty("eaglerxsupervisor.epoll") != null) {
					logger.warn("Tried to enable epoll, but it is not available!", Epoll.unavailabilityCause());
				}
			}
		}
	}

	public static EventLoopGroup createEventLoopGroup() {
		ThreadFactory factory = (new ThreadFactoryBuilder()).setNameFormat("Supervisor IO Thread #%1$d").build();
		if(enableKQueue) {
			return new KQueueEventLoopGroup(threadPoolSize, factory);
		}else if(enableEpoll) {
			return new EpollEventLoopGroup(threadPoolSize, factory);
		}else {
			return new NioEventLoopGroup(threadPoolSize, factory);
		}
	}

	public static ChannelInitializer<Channel> getServerChildInitializer(EaglerXSupervisorServer server, int readTimeout) {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.config().setAllocator(PooledByteBufAllocator.DEFAULT).setWriteBufferWaterMark(MARK);
				try {
					channel.config().setOption(ChannelOption.IP_TOS, 24);
				} catch (ChannelException var3) {
				}
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(readTimeout));
				pipeline.addLast("Varint21FrameDecoder", new Varint21FrameDecoder());
				pipeline.addLast("Varint21FrameEncoder", new Varint21FrameEncoder());
				SupervisorDecoder dec = new SupervisorDecoder(EaglerSupervisorProtocol.CLIENT_TO_SERVER);
				pipeline.addLast("SupervisorDecoder", dec);
				SupervisorEncoder enc = new SupervisorEncoder(EaglerSupervisorProtocol.SERVER_TO_CLIENT);
				pipeline.addLast("SupervisorEncoder", enc);
				SupervisorPacketHandler h = new SupervisorPacketHandler(HANDLER_LOGGER, channel, dec, enc, null);
				h.setConnectionHandler(new SupervisorServerHandshakeHandler(server, h));
				pipeline.addLast("SupervisorPacketHandler", h);
			}
		};
	}

	public static ChannelInitializer<Channel> getStatusChildInitializer(EaglerXSupervisorServer server, int readTimeout) {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(readTimeout));
				pipeline.addLast("HttpServerCodec", new HttpServerCodec());
				pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(65535));
				pipeline.addLast("HttpStatusRequestHandler", new HttpStatusRequestHandler(server));
			}
		};
	}

	public static ChannelFuture bindListener(EventLoopGroup eventLoopGroup, SocketAddress address, ChannelInitializer<Channel> initializer) {
		return (new ServerBootstrap()).option(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.channel(PipelineFactory.getServerChannel(address))
				.group(eventLoopGroup)
				.attr(PipelineFactory.LOCAL_ADDRESS, address)
				.localAddress(address)
				.childHandler(initializer)
				.bind();
	}

	public static Class<? extends Channel> getClientChannel(SocketAddress address) {
		if (address != null && (address instanceof DomainSocketAddress)) {
			if(!enableEpoll) {
				throw new IllegalStateException("Epoll required to have UNIX sockets");
			}
			return EpollDomainSocketChannel.class;
		}else if(enableKQueue) {
			return KQueueSocketChannel.class;
		}else if(enableEpoll) {
			return EpollSocketChannel.class;
		}else {
			return NioSocketChannel.class;
		}
	}

	public static Class<? extends ServerChannel> getServerChannel(SocketAddress address) {
		if (address != null && (address instanceof DomainSocketAddress)) {
			if(!enableEpoll) {
				throw new IllegalStateException("Epoll required to have UNIX sockets");
			}
			return EpollServerDomainSocketChannel.class;
		}else if(enableKQueue) {
			return KQueueServerSocketChannel.class;
		}else if(enableEpoll) {
			return EpollServerSocketChannel.class;
		}else {
			return NioServerSocketChannel.class;
		}
	}

	public static SocketAddress getAddr(String hostline) {
		URI uri = null;
		try {
			uri = new URI(hostline);
		} catch (URISyntaxException ex) {
		}

		if (uri != null && "unix".equals(uri.getScheme())) {
			return new DomainSocketAddress(uri.getPath());
		}

		if (uri == null || uri.getHost() == null) {
			try {
				uri = new URI("tcp://" + hostline);
			} catch (URISyntaxException ex) {
				throw new IllegalArgumentException("Bad hostline: " + hostline, ex);
			}
		}

		if (uri.getHost() == null) {
			throw new IllegalArgumentException("Invalid host/address: " + hostline);
		}

		return new InetSocketAddress(uri.getHost(), (uri.getPort()) == -1 ? 36900 : uri.getPort());
	}

}