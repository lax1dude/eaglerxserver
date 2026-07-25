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

package net.lax1dude.eaglercraft.backend.server.util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.net.InetAddresses;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;

public final class IPAddressSet {

	public static IPAddressSet create(List<String> entries) {
		List<IpSubnetFilterRule> ipv4Rules = new ArrayList<>();
		List<IpSubnetFilterRule> ipv6Rules = new ArrayList<>();
		for (String entry : entries) {
			try {
				String value = entry.trim();
				int slash = value.lastIndexOf('/');
				InetAddress address;
				int prefix;
				if (slash == -1) {
					address = InetAddresses.forString(value);
					prefix = address instanceof Inet4Address ? 32 : 128;
				} else {
					address = InetAddresses.forString(value.substring(0, slash));
					prefix = Integer.parseInt(value.substring(slash + 1));
				}
				IpSubnetFilterRule rule = new IpSubnetFilterRule(address, prefix, IpFilterRuleType.ACCEPT);
				if (address instanceof Inet4Address) {
					ipv4Rules.add(rule);
				} else if (address instanceof Inet6Address) {
					ipv6Rules.add(rule);
				} else {
					throw new IllegalArgumentException("Unsupported IP address: " + value);
				}
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Invalid IP address or subnet: \"" + entry + "\"", ex);
			}
		}
		return new IPAddressSet(ImmutableList.copyOf(ipv4Rules), ImmutableList.copyOf(ipv6Rules));
	}

	private final ImmutableList<IpSubnetFilterRule> ipv4Rules;
	private final ImmutableList<IpSubnetFilterRule> ipv6Rules;

	private IPAddressSet(ImmutableList<IpSubnetFilterRule> ipv4Rules,
			ImmutableList<IpSubnetFilterRule> ipv6Rules) {
		this.ipv4Rules = ipv4Rules;
		this.ipv6Rules = ipv6Rules;
	}

	public boolean contains(SocketAddress address) {
		if (!(address instanceof InetSocketAddress inetAddress) || inetAddress.isUnresolved()) {
			return false;
		}
		InetAddress addressValue = inetAddress.getAddress();
		List<IpSubnetFilterRule> rules;
		if (addressValue instanceof Inet4Address) {
			rules = ipv4Rules;
		} else if (addressValue instanceof Inet6Address) {
			rules = ipv6Rules;
		} else {
			return false;
		}
		for (int i = 0, l = rules.size(); i < l; ++i) {
			if (rules.get(i).matches(inetAddress)) {
				return true;
			}
		}
		return false;
	}

}
