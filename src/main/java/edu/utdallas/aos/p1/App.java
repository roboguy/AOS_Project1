package edu.utdallas.aos.p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

/**
 * AOS Project 1 - Distributed Node Discovery
 * 
 * @author sudhanshu iyer - sxi120530
 *
 */

public class App {
	public static final int MESSAGE_SIZE = 10000;
	private static final Logger logger = LogManager.getLogger(App.class);
	public static volatile ConcurrentHashMap<String, Node> neighboursTable;
	public static volatile Node myInformation;
	public static volatile boolean isInitial = true;
	private static volatile boolean isStopped = false;
	
	public static SctpServerChannel sctpServerChannel;
	
	public static void main(String[] args) throws InterruptedException {

		if (args.length != 1) {
			System.err.println("Usage: Project1 <node_ID>");
			logger.error("Invalid Input Paramters or not enoguh input paramters.");
			System.exit(2);
		}
		String nodeID = args[0];
		logger.debug("Program Started");
		logger.debug("Reading Config file at: node.conf");
		InitialInformation info = readConfig(nodeID);
		neighboursTable = info.getNeighbors();
		myInformation = info.getMyInformation();
		
		logger.info("Round 0 - Initial Information");
		logger.info("Node ID" + "\t" + String.format("%-17s", "Host") + "\t" + "Port" + "\t" + String.format("%-20s","Waiting(true/false)") + "Sent" );
		//logger.info(String.format("%-7s", myInformation.getID()) + "\t" + myInformation.getIPAddr()+".utdallas.edu" + "\t" + myInformation.getPort() + "\tfalse");
		
		Iterator<Entry<String, Node>> visitor = neighboursTable.entrySet().iterator();
		while(visitor.hasNext()){
			Entry<String, Node> entry = visitor.next();
			//String id = entry.getKey();
			Node no = entry.getValue();
			logger.info(String.format("%-7s", no.getID())+ "\t" + no.getIPAddr()+".utdallas.edu" + "\t" + no.getPort() + "\t" + String.format("%-20s",no.isWaiting()) + String.format("%-20s", no.isRequestSent()));
		}
		
		ClientThread client = new ClientThread();
		client.start();
		
		//Buffer to hold messages in byte format
			
			ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
			String message;
			try
			{
				int port = Integer.parseInt(myInformation.getPort());
				logger.info("Starting Server on port " + port);
				//Open a server channel
				sctpServerChannel = SctpServerChannel.open();
				//Create a socket addess in the current machine at port 5000
				InetSocketAddress serverAddr = new InetSocketAddress(port);
				//Bind the channel's socket to the server in the current machine at port 5000
				sctpServerChannel.bind(serverAddr);
				//Server goes into a permanent loop accepting connections from clients			
				while(!RequestHandler.isTerminated())
				{
					//Listen for a connection to be made to this socket and accept it
					//The method blocks until a connection is made
					//Returns a new SCTPChannel between the server and client
					SctpChannel sctpChannel = sctpServerChannel.accept();
					MessageInfo messageInfo = sctpChannel.receive(byteBuffer,null,null);
					logger.debug(messageInfo);
					message = byteToString(byteBuffer);
					byteBuffer.clear();
					//logger.debug(message);
					
					RequestHandler handler = new RequestHandler(message);
					handler.start();
					
					//handler.join();
					//logger.debug(RequestHandler.isTerminated());
				}
			}
			catch(IOException ex)
			{
				logger.error("Failed to start server. Full stack trace available below.");
				logger.error(ex.getMessage());
			}

		logger.info("Final State Recorded before Termination:");
		logger.info("Node ID" + "\t" + String.format("%-17s", "Host") + "\t" + "Port" + "\t" + "Waiting(true/false)");
		//logger.info(String.format("%-7s", myInformation.getID()) + "\t" + myInformation.getIPAddr()+".utdallas.edu" + "\t" + myInformation.getPort() + "\tfalse");
		Iterator<Entry<String, Node>> iter = neighboursTable.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Node> entry = iter.next();
			//String id = entry.getKey();
			Node no = entry.getValue();
			logger.info(String.format("%-7s", no.getID())+ "\t" + no.getIPAddr()+".utdallas.edu" + "\t" + no.getPort() + "\t" + no.isWaiting());
		}
		
		logger.info("Terminated");
	} // Main method
	
	public static String byteToString(ByteBuffer byteBuffer)
	{
		byteBuffer.position(0);
		byteBuffer.limit(MESSAGE_SIZE);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}
	
	private static InitialInformation readConfig(String nodeID) {
		Scanner confScanner = null;
		File config = new File("node.conf");
		String numNodes = "";
		String[] initialKnowledge = null;
		String[] location = null;
		boolean first = true;
		boolean second = true;
		int num = 0;
		try {
			confScanner = new Scanner(
					new BufferedReader(new FileReader(config)));
		} catch (FileNotFoundException e) {
			logger.error("Unable to Read config file. Please make sure file exists and is located at project root.");
		}

		while (confScanner.hasNext()) {
			String line = confScanner.nextLine();
			if (first && line.contains("#") || line.length() < 1)
				continue;
			else if (first && !line.contains("#")) {
				logger.info("Total Number of Nodes: " + line);
				numNodes = line;
				first = false;
			} // First If block
			num = Integer.parseInt(numNodes);
			initialKnowledge = new String[num];

			if (!first && second) {
				int count = 0;
				boolean notDone = true;
				while (confScanner.hasNext() && notDone) {

					String knowledge = confScanner.nextLine();
					if (!knowledge.contains("#")
							&& knowledge.trim().length() > 0) {
						initialKnowledge[count] = knowledge;
						count++;
						if (count == num) {
							notDone = false;
						}
					}
				}
				second = false;
			}// Second if block
			
			int count = 0;
			boolean notDone1 = true;
			location = new String[num];
			if (!first && !second) {
				while (confScanner.hasNext() && notDone1) {

					String loc = confScanner.nextLine();
					if (!loc.contains("#")
							&& loc.trim().length() > 0) {
						location[count] = loc;
						count++;
						if (count == num) {
							notDone1 = false;
						}
					}
				}
			}

		}
		confScanner.close();
		return generateNeighbourTable(nodeID, initialKnowledge, location);
	}

	private static InitialInformation generateNeighbourTable(String nodeID, String[] initialKnowledge, String[] location) {
		ConcurrentHashMap<String, Node> initialNeighbours = new ConcurrentHashMap<String, Node>();
		if(nodeID == null || location == null || initialKnowledge == null){
			logger.error("Unable to read node config. Null Initial Knowledge.");
		}
		int id = Integer.parseInt(nodeID);
		String list = initialKnowledge[id];
		list = list.replaceAll("\\s+","");
		String subList = list.substring(list.lastIndexOf('{') + 1, list.lastIndexOf('}')); 
		//System.out.println(subList);
		String[] neighbourNodes = subList.split(",");
		int[] neighborIDs = new int[neighbourNodes.length];
		for(int count = 0; count < neighbourNodes.length; count++){
			neighborIDs[count] = Integer.parseInt(neighbourNodes[count]);
		}
		String loc = location[id];
		
		String[] loc_data = loc.split("\\t+");
		Node myInfo = new Node(loc_data[0].trim(), loc_data[1].trim(), loc_data[2].trim(), false);
		myInfo.setRequestSent(true);
		
		ArrayList<Node> neighbors = new ArrayList<Node>();
		
		for(Integer neighbor : neighborIDs){
			String locate = location[neighbor];
			String[] locate_data = locate.split("\\t+");
			Node nei = new Node(locate_data[0].trim(), locate_data[1].trim(), locate_data[2].trim(), true);
			neighbors.add(nei);
		}
		initialNeighbours.put(myInfo.getID(), myInfo);
		for(Node neighbour : neighbors){
			initialNeighbours.put(neighbour.getID(), neighbour);
		}
		
		InitialInformation initial = new InitialInformation(myInfo, initialNeighbours);
		return initial;
	}

	public static boolean isStopped() {
		return isStopped;
	}

	public static void setStopped(boolean isStopped) {
		App.isStopped = isStopped;
	}

}
