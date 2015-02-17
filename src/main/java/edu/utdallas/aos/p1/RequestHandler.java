package edu.utdallas.aos.p1;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class RequestHandler extends Thread{
	
	private static Logger logger = LogManager.getLogger(RequestHandler.class);
	public Object lock = new Object();
	private String message;
	private static volatile boolean  terminated = false;
	
	@Override
	public void run(){
		synchronized (lock) {
			
			//Deseralize message using gson.
			Gson gson = new Gson();
			String messageTrimmed = message.trim();
			JsonReader reader = new JsonReader(new StringReader(messageTrimmed));
			reader.setLenient(true);
			RequestObject deserlizedRequest = gson.fromJson(reader, RequestObject.class);
			
			//Update App.neighbours table that response was received for sender all nodes in response.
			Node sender = deserlizedRequest.getMyInformation();
			logger.debug("Received a message from id:" + sender.getID() + " sender has " + deserlizedRequest.getNeighbors().size() + " neighbors.");
			Node te = App.neighboursTable.get(sender.getID());
			if(te != null){
				Node n = App.neighboursTable.get(sender.getID());
				//Update waiting value for each key to false.
				n.setWaiting(false);
				logger.info("Node exists.");
			}else {
				logger.info("New Node added - " + sender.getID() + " " + sender.getIPAddr() + " " + sender.getPort());
				sender.setRequestSent(false);
				sender.setWaiting(true);
				App.neighboursTable.put(sender.getID(), sender);
			}
			logger.debug("Checking sender's neigbhors for new information");
			//Update App.neighbours table that response was received for sender all nodes in response.
			if(deserlizedRequest.getNeighbors().size() > 0){
				for(Node n : deserlizedRequest.getNeighbors()){
					Node tes = App.neighboursTable.get(n.getID());
					if(tes !=null){
						Node no = App.neighboursTable.get(n.getID());
						//Update waiting value for each key to false.
						no.setWaiting(false);
					}else {
						n.setRequestSent(false);
						n.setWaiting(true);
						App.neighboursTable.put(n.getID(), n);
					}
				}
			}
			ArrayList<Node> toSend = new ArrayList<>();
			int waitingCount = 0;
			//check each node in neighbors table if waiting = true & a request was not sent already.
			//If such a node exists then add it to toSend list. 
			// else if waiting = false for all nodes then set App.isStopped = true
			Iterator<Entry<String, Node>> iter = App.neighboursTable.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, Node> entry = iter.next();
				Node n = entry.getValue();
				if(n.isWaiting()){
					waitingCount++;
					if(!n.isRequestSent()){
						toSend.add(n);
					}
				}
				logger.info(String.format("%-7s", n.getID()) + "\t"
						+ n.getIPAddr() + ".utdallas.edu" + "\t"
						+ n.getPort() + "\t"
						+ String.format("%-20s", n.isWaiting())
						+ String.format("%-20s", n.isRequestSent()));
			}
			//If request was not sent & waiting then start a new client thread.
			if(waitingCount == 0 && toSend.size() > 0){
				logger.error("Unexpected state: not waiting for any nodes but new nodes availabe to send requests.");
			}
			if(toSend.size()>0){
				ClientThread client = new ClientThread(toSend);
				client.start();
			}
			if(waitingCount == 0){
				terminate();
				
			} 
			
			
		}//Synchronized block ENDS
		
	}//Run method ENDS
	
	public static void terminate(){
		terminated = true;
		App.setStopped(true);
		logger.info("Final State Recorded before Termination:");
		logger.info("Node ID" + "\t" + String.format("%-17s", "Host") + "\t" + "Port" + "\t" + "Waiting(true/false)");
		//logger.info(String.format("%-7s", myInformation.getID()) + "\t" + myInformation.getIPAddr()+".utdallas.edu" + "\t" + myInformation.getPort() + "\tfalse");
		Iterator<Entry<String, Node>> iterator = App.neighboursTable.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Node> entry = iterator.next();
			//String id = entry.getKey();
			Node no = entry.getValue();
			logger.info(String.format("%-7s", no.getID())+ "\t" 
					+ no.getIPAddr()+".utdallas.edu" + "\t" 
					+ no.getPort() + "\t" 
					+ no.isWaiting() + "\t"
					+ no.isRequestSent());
		}
		
		logger.info("Terminated");
	}
	
	public static boolean isTerminated(){
		return terminated;
	}
	
	public RequestHandler(String message){
		this.message = message;
	}
}
