package pl.edu.agh.kt;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Routing {
	
	protected static final Logger logger = LoggerFactory.getLogger(Routing.class);
	
	private IOFSwitchService switchService;
	
	private Graph network = new Graph();
	private List<Host> hosts = new ArrayList<Host>();
	
	public Routing(IOFSwitchService switchService) {
		super();
		this.switchService = switchService;
	}
	
	public void runStatisticCollector(DatapathId switchId) {
		IOFSwitch sw = switchService.getSwitch(switchId);
		StatisticsCollector.createInstance(sw);
	}
	
	public void addNeighbor(DatapathId sourceId, OFPort outputPort, DatapathId neighborId, int cost) {
		Node sourceNode = network.getNode(sourceId);
		Node destinationNode = network.getNode(neighborId);
		sourceNode.putOutputPort(outputPort, destinationNode);
		sourceNode.putDestination(destinationNode, cost);
	}
		
	public void removeNeighbor(DatapathId sourceId, OFPort outputPort, DatapathId neighborId) {
		Node sourceNode = network.getNode(sourceId);
		sourceNode.getOutputPorts().remove(outputPort);
		sourceNode.getAdjacentNodes().remove(network.getNode(neighborId));
	}
		
	public void removeSwitch(DatapathId id) {
		network.getNodes().remove(network.getNode(id));
	}
	
	public void setCost(DatapathId id, OFPort port, int cost) {
		Node sourceNode = network.getNode(id);
		Node adjacentNode = sourceNode.getOutputPorts().get(port);
		if (adjacentNode != null) {
			sourceNode.putDestination(adjacentNode, cost);
		}
	}
		
	public void createFlowForPacket(Packet packet) {
		Host sourceHost = getHostByIp(packet.getSrcIp());
		Host destinationHost = getHostByIp(packet.getDstIp());
		if (sourceHost == null || destinationHost == null) {
			return;
		}
		Node sourceNode = network.getNode(sourceHost.getSwitchId());
		Dijkstra.calculateShortestPathFromSource(network, sourceNode);
		Node destinationNode = network.getNode(destinationHost.getSwitchId());
		List<Node> shortestPath = destinationNode.getShortestPath();
		for (int i = 0; i < shortestPath.size(); i++) {
			Node node = shortestPath.get(i);
			Node nextNode = null;
			if (i + 1 < shortestPath.size()) {
				nextNode = shortestPath.get(i + 1);
			} else {
				nextNode = destinationNode;
			}
			IOFSwitch sw = switchService.getSwitch(node.getId());
			OFPort outPort = getOutputPortByNodes(node, nextNode);
			if (outPort != null) {
				if (packet.getType() == PacketType.ARP) {
					Flows.insertArpFlow(sw, packet.getDstIp(), outPort);
				} else {
					Flows.insertIpFlow(sw, packet, outPort);
				}
			}
		}
		IOFSwitch sw = switchService.getSwitch(destinationNode.getId());
		if (sw != null) {
			if (packet.getType() == PacketType.ARP) {
				Flows.insertArpFlow(sw, packet.getDstIp(), destinationHost.getPort());
			} else {
				Flows.insertIpFlow(sw, packet, destinationHost.getPort());
			}
		}
		if (shortestPath.size() > 0 && packet.getType() == PacketType.IP) {
			printInfoForNewFlow(packet, shortestPath, destinationNode);
		}
	}
	
	private Host getHostByIp(IPv4Address ip) {
		for (Host host : hosts) {
			if (host.getIp().equals(ip)) {
				return host;
			}
		}
		logger.error("Host {} not found", ip.toString());
		return null;
	}
	
	private OFPort getOutputPortByNodes(Node sourceNode, Node destinationNode) {
		for(Map.Entry<OFPort, Node> entry: sourceNode.getOutputPorts().entrySet()) {
			if(entry.getValue().equals(destinationNode)) {
				return entry.getKey();
			}
		}
		logger.error("Port beetwen {} and {} not found", sourceNode.getId().toString(), destinationNode.getId().toString());
		return null;
	}
	
	private void printInfoForNewFlow(Packet packet, List<Node> path, Node destination) {
		StringBuilder pathBuilder = new StringBuilder();
		for(Node node : path) {
			pathBuilder.append(node.getId().toString().substring(18, 20)).append(" -> ");
		}
		pathBuilder.append(destination.getId().toString().substring(18, 20));
		
		String srcPort = packet.getSrcPort() != null ? ":" + packet.getSrcPort().toString() : "";
		String dstPort = packet.getDstPort() != null ? ":" + packet.getDstPort().toString() : "";
		String flow = packet.getSrcIp().toString() + srcPort + " -> " + packet.getDstIp().toString() + dstPort;
		logger.info("NEW FLOW: " +  flow + ", protocol " + getProtocolName(packet.getProtocol()) + " - path for packets: " + pathBuilder.toString());
	}
	
	private String getProtocolName(IpProtocol protocol) {
		switch(protocol.toString()) {
			case "0x1":
				return "ICMP";
			case "0x6":
				return "TCP";
			case "0x11":
				return "UDP";
			default:
				return "unknown";
		}
	}
		
	public void initHosts() {
		try {
			FileReader reader;
			reader = new FileReader("src/main/resources/hosts.json");
			StringBuilder jsonBuilder = new StringBuilder();
			char[] buffer = new char[10];
			while (reader.read(buffer) != -1) {
				jsonBuilder.append(new String(buffer));
				buffer = new char[10];
			}
			reader.close();
			ObjectMapper mapper = new ObjectMapper();
			List<HostJSON> hostJsonList = Arrays.asList(mapper.readValue(jsonBuilder.toString(), HostJSON[].class));
			for (HostJSON host : hostJsonList) {
				hosts.add(new Host(IPv4Address.of(host.getIp()), OFPort.of(host.getPort()), DatapathId.of(host.getSwitchId())));
			}
			logger.info("Hosts list initialized successfully");
		} catch (Exception e) {
			logger.error("Error during hosts list initialization");
		}
	}

}
