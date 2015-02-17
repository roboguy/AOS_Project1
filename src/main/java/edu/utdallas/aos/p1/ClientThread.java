package edu.utdallas.aos.p1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

/*
 * Implements the protocol for neighbor discovery
 */
public class ClientThread extends Thread {

	public Logger logger = LogManager.getLogger(ClientThread.class);
	public Object lock = new Object();
	private ArrayList<Node> toSend;
	public static final int MESSAGE_SIZE = 1000000;
	
	public void run() {

		synchronized (lock) {

			if (App.isInitial) {
				App.isInitial = false;
				logger.info("Initial: Waiting for 30 seconds to ensure all nodes started");

				try {
					// TODO: Change value to 30 seconds - currently set to 2 seconds
					Thread.sleep(30000);
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
						String messagae = getRequestMessage();
						String host = node.getIPAddr();
						String port = node.getPort();
						go(messagae, host, port);
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
				String searalizedReq = getRequestMessage();
				for(Node node : toSend){
					String host = node.getIPAddr();
					String port = node.getPort();
					go(searalizedReq, host, port);
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
	
	private synchronized String getRequestMessage() {
		Gson gson = new Gson();
		Node me = App.myInformation;
		ArrayList<Node> myNeighbors = new ArrayList<Node>();
		Iterator<Entry<String, Node>> nIter = App.neighboursTable.entrySet().iterator();
		while(nIter.hasNext()){
			Entry<String, Node> entry = nIter.next();
			Node n = entry.getValue();
			if(!n.getID().equals(me.getID())){
				myNeighbors.add(n);
			}
		}
		RequestObject req = new RequestObject(me);
		req.setNeighbors(myNeighbors);
		String searalizedReq = gson.toJson(req);
		return searalizedReq;
	}
	public ClientThread(){
		
	}
	public ClientThread(ArrayList<Node> toSend){
		this.toSend = toSend;
	}
	
	
	public synchronized void go(String msg, String host, String port)
	{
//		//Buffer to hold messages in byte format
//		StringBuilder hostString = new StringBuilder();
//		if(!host.contains("local") && !host.contains("utdallas")){
//			hostString.append(host);
//			hostString.append(".utdallas.edu");
//		} else {
//			hostString.append(host);
//		}
//		String ho = hostString.toString();
//		
//		ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
//		int po = Integer.parseInt(port);
//		logger.debug("Sending message to server..");
//		try
//		{
//			//Create a socket address for  server at net01 at port 5000
//			SocketAddress socketAddress = new InetSocketAddress(ho,po);
//			//Open a channel. NOT SERVER CHANNEL
//			SctpChannel sctpChannel = SctpChannel.open();
//			//Bind the channel's socket to a local port. Again this is not a server bind
//			sctpChannel.bind(new InetSocketAddress(5000));
//			//Connect the channel's socket to  the remote server
//			sctpChannel.connect(socketAddress);
//			//Before sending messages add additional information about the message
//			MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
//			//convert the string message into bytes and put it in the byte buffer
//			byteBuffer.put(msg.getBytes());
//			//Reset a pointer to point to the start of buffer 
//			byteBuffer.flip();
//			//Send a message in the channel (byte format)
//			sctpChannel.send(byteBuffer,messageInfo);
//			logger.debug("success");
//			byteBuffer.clear();
//		}
//		catch(IOException ex)
//		{
//			logger.error("Error sending message to server at "+  ho);
//			logger.error(ex.getMessage());
//		}
	}
}
