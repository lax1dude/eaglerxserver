package net.lax1dude.eaglercraft.backend.server.base.update;

import net.lax1dude.eaglercraft.backend.server.api.IUpdateCertificate;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketUpdateCertEAG;

public interface IUpdateCertificateImpl extends IUpdateCertificate {

	SPacketUpdateCertEAG packet();

	SHA1Sum checkSum();

}
