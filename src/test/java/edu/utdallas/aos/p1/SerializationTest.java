package edu.utdallas.aos.p1;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.junit.Test;

import com.google.gson.Gson;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;


public class SerializationTest {
//	public static final int MESSAGE_SIZE = 10000;
//	@Test
//	public void test(){
//		Gson gson = new Gson();
//		Node myinfo = new Node("2", "dc02.utdallas.edu", "4321", false);
//		RequestObject request = new RequestObject(myinfo);
//		request.setNeighbors(new ArrayList<Node>());
//		String serializedRequest = gson.toJson(request);
//		System.out.println(serializedRequest);
//		
//		RequestObject deserlizedRequest = gson.fromJson(serializedRequest, RequestObject.class);
//		System.out.println(deserlizedRequest.getClass());
//		assertEquals(deserlizedRequest.getClass(), RequestObject.class);	
////		System.out.println(deserlizedRequest.getType());
////		System.out.println(deserlizedRequest.getMessage());
//		System.out.println("Node ID: " + deserlizedRequest.getMyInformation().getID());
//		//go(serializedRequest);
//	}
//	
//	
//	public static void go(String message)
//	{
//		//Buffer to hold messages in byte format
//		ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
//		System.out.println("Sending "+ message + " to server.");
//		try
//		{
//			//Create a socket address for  server at net01 at port 5000
//			SocketAddress socketAddress = new InetSocketAddress("localhost",3332);
//			//Open a channel. NOT SERVER CHANNEL
//			SctpChannel sctpChannel = SctpChannel.open();
//			//Bind the channel's socket to a local port. Again this is not a server bind
//			sctpChannel.bind(new InetSocketAddress(5000));
//			//Connect the channel's socket to  the remote server
//			sctpChannel.connect(socketAddress);
//			//Before sending messages add additional information about the message
//			MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
//			//convert the string message into bytes and put it in the byte buffer
//			byteBuffer.put(message.getBytes());
//			//Reset a pointer to point to the start of buffer 
//			byteBuffer.flip();
//			//Send a message in the channel (byte format)
//			sctpChannel.send(byteBuffer,messageInfo);
//		}
//		catch(IOException ex)
//		{
//			ex.printStackTrace();
//		}
//	}

}
