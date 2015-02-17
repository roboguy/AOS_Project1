package edu.utdallas.aos.p1;

import java.util.concurrent.ConcurrentHashMap;

public class InitialInformation {
	
	private Node myInformation;
	private ConcurrentHashMap<String, Node> neighbors;
	
	public InitialInformation(Node myInfo, ConcurrentHashMap<String, Node> initialNeighbours) {
		this.myInformation = myInfo;
		this.neighbors = initialNeighbours;
	}

	public Node getMyInformation() {
		return myInformation;
	}

	public ConcurrentHashMap<String, Node> getNeighbors() {
		return neighbors;
	}

}
