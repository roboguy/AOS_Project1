package edu.utdallas.aos.p1;

public class Node {
	
	private String id;
	private String ip;
	private String port;
	private boolean requestSent = false;
	private boolean isWaiting;
	
	public Node(String id, String Ipaddr, String port, boolean waiting){
		this.id = id;
		this.ip = Ipaddr;
		this.port = port;
		this.isWaiting = waiting;
	}
	
	public String getID() {
		return id;
	}
	public void setID(String iD) {
		id = iD;
	}
	public String getIPAddr() {
		return ip;
	}
	public void setIPAddr(String iPAddr) {
		ip = iPAddr;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String p) {
		port = p;
	}
	
	public boolean isRequestSent() {
		return requestSent;
	}

	public void setRequestSent(boolean responseSent) {
		this.requestSent = responseSent;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void setWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}

	@Override
	public int hashCode() {
		return Integer.parseInt(this.id);
	}

	@Override
	public boolean equals(Object obj) {
		Node rhs = (Node) obj;
		if(this.id.equals(rhs.id)){
			return true;
		} 
		else{
			return false;
		}
	}
	
}
