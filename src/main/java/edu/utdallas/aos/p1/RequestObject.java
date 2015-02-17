package edu.utdallas.aos.p1;

import java.util.List;

public class RequestObject {
	
	//private String type;
	//private String message;
	private Node node;
	private List<Node> neighbors;

	public Node getMyInformation() {
		return node;
	}
	public void setMyInformation(Node myInformation) {
		this.node = myInformation;
	}

	public RequestObject(Node myInformation){
		this.node = myInformation;
	}
	public List<Node> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(List<Node> myKnownNeighbors) {
		this.neighbors = myKnownNeighbors;
	}
	public RequestObject(Node node, List<Node> neighbors){
		this.node = node;
		this.neighbors = neighbors;
	}
	
}
