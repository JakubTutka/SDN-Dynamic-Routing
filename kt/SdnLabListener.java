package pl.edu.agh.kt;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.IFloodlightProviderService;

import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdnLabListener implements IOFMessageListener, IFloodlightModule {
	
	protected IFloodlightProviderService floodlightProvider;
	protected ITopologyService topologyService;
    protected IOFSwitchService switchService;
    
	protected static Logger logger;
	protected static Routing routing;
	
	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}
	
	public static Routing getRouting() {
		return routing;
	}
	
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(ITopologyService.class);
		l.add(IOFSwitchService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);
		topologyService = context.getServiceImpl(ITopologyService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		topologyService.addListener(new SdnLabTopologyListener());
		routing = new Routing(switchService);
		logger.info("******************* START **************************");
		routing.initHosts();
		
	}
	
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {		
		switch (msg.getType()) {
	    case PACKET_IN:
	    	Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
	    	if (eth.getEtherType() == EthType.ARP) {
	    		 ARP arp = (ARP) eth.getPayload();
	    		 IPv4Address srcIp = arp.getSenderProtocolAddress();
	    		 IPv4Address dstIp = arp.getTargetProtocolAddress();
	    		 if (!srcIp.isBroadcast() && !dstIp.isBroadcast()) {
		    		 routing.createFlowForPacket(new Packet(srcIp, dstIp));
		    		 routing.createFlowForPacket(new Packet(dstIp, srcIp));
	    		 }
	    	}
	    	if (eth.getEtherType() == EthType.IPv4) {
	    		 IPv4 ipv4 = (IPv4) eth.getPayload();
	    		 IPv4Address srcIp = ipv4.getSourceAddress();
	    		 IPv4Address dstIp = ipv4.getDestinationAddress();
	    		 if (!srcIp.isBroadcast() && !dstIp.isBroadcast()) {
	    			 TransportPort srcPort = null;
	    			 TransportPort dstPort = null;
	    			 if (ipv4.getProtocol() == IpProtocol.TCP) {
	    				 TCP tcp = (TCP) ipv4.getPayload();
	    	             srcPort = tcp.getSourcePort();
	    	             dstPort = tcp.getDestinationPort();
	    	         } else if (ipv4.getProtocol() == IpProtocol.UDP) {
	    	             UDP udp = (UDP) ipv4.getPayload();
	    	             srcPort = udp.getSourcePort();
	    	             dstPort = udp.getDestinationPort();
	    	         }
		    		 routing.createFlowForPacket(new Packet(srcIp, dstIp, srcPort, dstPort, ipv4.getProtocol()));
		    		 routing.createFlowForPacket(new Packet(dstIp, srcIp, dstPort, srcPort, ipv4.getProtocol()));
	    		 }
	    	}
	    	break;
	    default:
	        break;
	    }
		return Command.CONTINUE;
	}
	
}
