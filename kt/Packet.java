package pl.edu.agh.kt;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

public class Packet {

	private final IPv4Address srcIp;
	private final IPv4Address dstIp;
	private final TransportPort srcPort;
	private final TransportPort dstPort;
	private final IpProtocol protocol;
	
	private final PacketType type;
	
	public Packet(IPv4Address srcIp, IPv4Address dstIp) {
		this.srcIp = srcIp;
		this.dstIp = dstIp;
		this.srcPort = null;
		this.dstPort = null;
		this.protocol = null;
		this.type = PacketType.ARP;
	}
	
	public Packet(IPv4Address srcIp, IPv4Address dstIp, TransportPort srcPort, TransportPort dstPort, IpProtocol protocol) {
		this.srcIp = srcIp;
		this.dstIp = dstIp;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.protocol = protocol;
		this.type = PacketType.IP;
	}
	
	public IPv4Address getSrcIp() {
		return srcIp;
	}

	public IPv4Address getDstIp() {
		return dstIp;
	}

	public TransportPort getSrcPort() {
		return srcPort;
	}

	public TransportPort getDstPort() {
		return dstPort;
	}

	public IpProtocol getProtocol() {
		return protocol;
	}	
	
	public PacketType getType() {
		return type;
	}	

}
