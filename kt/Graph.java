package pl.edu.agh.kt;

import java.util.HashSet;
import java.util.Set;

import org.projectfloodlight.openflow.types.DatapathId;

public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public Node getNode(DatapathId id) {
    	for (Node node : nodes) {
        	if (node.getId().toString().equals(id.toString())) {
        		return node;
        	}
        }
    	Node node = new Node(id);
    	nodes.add(node);
    	return node;
    }
    
    public Set<Node> getNodes() {
    	return nodes;
    }
        
}