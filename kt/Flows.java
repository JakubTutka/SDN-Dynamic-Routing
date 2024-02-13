package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IOFSwitch;

public class Flows {

	private static final Logger logger = LoggerFactory.getLogger(Flows.class);
	
	public static short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0; // infinity
	public static short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0; // infinity
	public static short FLOWMOD_DEFAULT_PRIORITY = 100;

	protected static boolean FLOWMOD_DEFAULT_MATCH_VLAN = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_MAC = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_IP_ADDR = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_TRANSPORT = true;

	public Flows() {
		logger.info("FlowsManager() begin/end");
	}
			
	public static boolean insertIpFlow(IOFSwitch sw, Packet packet, OFPort outPort) {
		Match ipMatch = prepareIpMatch(sw, packet);
		return insertFlow(sw, ipMatch, outPort);
	}
	
	public static boolean insertArpFlow(IOFSwitch sw, IPv4Address dstIp, OFPort outPort) {
		Match arpMatch = prepareArpMatch(sw, dstIp);
		return insertFlow(sw, arpMatch, outPort);
	}
	
	private static boolean insertFlow(IOFSwitch sw, Match match, OFPort outPort) {
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		
		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<OFAction>();
		
		aob.setPort(outPort);
		actions.add(aob.build());
		
		fmb.setMatch(match).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
			.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
			.setOutPort(outPort)
			.setPriority(FLOWMOD_DEFAULT_PRIORITY);
		fmb.setActions(actions);
		
		try {
			return sw.write(fmb.build());
		} catch (Exception ex) {
			logger.error("Error {}", ex);
			return false;
		}
	}
		
	private static Match prepareIpMatch(IOFSwitch sw, Packet packet) {
		Match.Builder mb = sw.getOFFactory().buildMatch();
		
		mb.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.setExact(MatchField.IPV4_SRC, packet.getSrcIp())
			.setExact(MatchField.IPV4_DST, packet.getDstIp())
			.setExact(MatchField.IP_PROTO, packet.getProtocol());
		
		if (packet.getProtocol() == IpProtocol.TCP) {
			mb.setExact(MatchField.TCP_SRC, packet.getSrcPort()).setExact(MatchField.TCP_DST, packet.getDstPort());
		}
		if (packet.getProtocol()  == IpProtocol.UDP) {
			mb.setExact(MatchField.UDP_SRC, packet.getSrcPort()).setExact(MatchField.UDP_DST, packet.getDstPort());
		}
		
		return mb.build();
	}
	
	private static Match prepareArpMatch(IOFSwitch sw, IPv4Address dstIp) {
		Match.Builder mb = sw.getOFFactory().buildMatch();
		
		mb.setExact(MatchField.ETH_TYPE, EthType.ARP)
			.setExact(MatchField.ARP_TPA, dstIp);
		
		return mb.build();
	}

}
