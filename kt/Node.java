package pl.edu.agh.kt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

public class Node {
	
    private final DatapathId id;
    private final Map<OFPort, Node> outputPorts;
    private final Map<Node, Integer> adjacentNodes;

    private Integer distance;
    private List<Node> shortestPath;

    public Node(DatapathId id) {
        this.id = id;
        this.distance = Integer.MAX_VALUE;
        this.outputPorts = new HashMap<>();
        this.adjacentNodes = new HashMap<>();
        this.shortestPath = new LinkedList<>();
    }
    
    public void putOutputPort(OFPort port, Node destination) {
    	outputPorts.put(port, destination);
    }
    
    public void putDestination(Node destination, int cost) {
        adjacentNodes.put(destination, cost);
    }
    
    public DatapathId getId() {
    	return id;
    }
    
	public Map<OFPort, Node> getOutputPorts() {
		return outputPorts;
	}
    
	public Map<Node, Integer> getAdjacentNodes() {
		return adjacentNodes;
	}

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return distance;
    }

    public void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }
    
    public List<Node> getShortestPath() {
        return shortestPath;
    }    
        
}
