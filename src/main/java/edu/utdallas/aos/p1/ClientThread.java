package edu.utdallas.aos.p1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Implements the protocol for neighbor discovery
 */
public class ClientThread extends Thread {

	public Logger logger = LogManager.getLogger(ClientThread.class);
	public Object lock = new Object();
	private ArrayList<Node> toSend;

	// TODO: Implement SCTP Client for responding to requests
	public void run() {

		synchronized (lock) {

			if (App.isInitial) {
				App.isInitial = false;
				logger.info("Initial: Waiting for 30 seconds to ensure all nodes started");

				try {
					// TODO: Change value to 30 seconds - currently set to 2 seconds
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Iterator<Entry<String, Node>> iter = App.neighboursTable.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Node> entry = iter.next();
					Node node = entry.getValue();
					String id = entry.getKey();
					boolean isWaiting = node.isWaiting();
					boolean requestSent = node.isRequestSent();
					if (isWaiting && !requestSent) {
						// TODO: Send a message to the node.
						logger.info("Request Sent to node: " + node.getID());
						node.setRequestSent(true);

						App.neighboursTable.replace(id, node);
						Node test = App.neighboursTable.get(id);
						logger.info(String.format("%-7s", test.getID()) + "\t"
								+ test.getIPAddr() + ".utdallas.edu" + "\t"
								+ test.getPort() + "\t"
								+ String.format("%-20s", test.isWaiting())
								+ String.format("%-20s", test.isRequestSent()));
					}

				}// While loop ends
			} else {
				for(Node node : toSend){
					//TODO: send a message to the node
					node.setRequestSent(true);
					App.neighboursTable.replace(node.getID(), node);
					logger.info(String.format("%-7s", node.getID()) + "\t"
							+ node.getIPAddr() + ".utdallas.edu" + "\t"
							+ node.getPort() + "\t"
							+ String.format("%-20s", node.isWaiting())
							+ String.format("%-20s", node.isRequestSent()));
					
				}
			}
			
		}// Synchronized Block ENDS

	}// RUN Method ends
	public ClientThread(){
		
	}
	public ClientThread(ArrayList<Node> toSend){
		this.toSend = toSend;
	}
}
