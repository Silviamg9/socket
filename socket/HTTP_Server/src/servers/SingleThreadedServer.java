package servers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadedServer implements Runnable{

	protected int serverPort = 9001;
	protected ServerSocket serversocket = null;
	protected boolean isStopped;
	protected Thread runningThread = null;
	
	public SingleThreadedServer(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void run() {
		synchronized(this) {
			this.runningThread = Thread.currentThread();
		}
		
		openServerSocket();
		
		while(! isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serversocket.accept();
				
				processClientRequest(clientSocket);
			
			}catch(IOException e) {
				if(isStopped()) {
					System.out.println("Server stoped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}

		}
		System.out.println("Server Stopped");
		
	}

	private void processClientRequest(Socket clientSocket) throws IOException{
		InputStream input = clientSocket.getInputStream();
		OutputStream output= clientSocket.getOutputStream();
		
		long time = System.currentTimeMillis();
		
		output.write(("HTTP/1.1 200 OK\n\n <html><body> Server: " + time + "</body></html>").getBytes());
		output.close();
		input.close();
		System.out.println("Request procesed: " + time);
	}

	private boolean isStopped() {
		return isStopped;
	}

	private void openServerSocket() {
		try {
			this.serversocket = new ServerSocket(this.serverPort);
		}catch(IOException ex) {
			throw new RuntimeException("Cannot open port " + serverPort);
		}
		
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serversocket.close();
		}catch(IOException e) {
			System.err.println(e);
		}
		
	}
	
	

}
