package pl.edu.agh.kt;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;

public class Host {
	
	private IPv4Address ip;
	private OFPort port;
	private DatapathId switchId;
		
	public Host(IPv4Address ip, OFPort port, DatapathId datapathId) {
		this.ip = ip;
		this.port = port;
		this.switchId = datapathId;
	}
	
	public IPv4Address getIp() {
		return this.ip;
	}
	
	public OFPort getPort() {
		return this.port;
	}
	
	public DatapathId getSwitchId() {
		return this.switchId;
	}
		
}