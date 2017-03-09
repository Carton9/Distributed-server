package Gateserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import DataType.ServerData;

public class TCPPort implements Runnable {
	private static ReadWriteLock MessageListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerData> MessageList=new ArrayList<ServerData>();
	private Socket client = null;  
	boolean alive;
	
	final private int port= 9900;
	private InetAddress serverAddress;
	private ReadWriteLock outputLock = new ReentrantReadWriteLock(); 
	private ObjectOutputStream output = null;  
	private ReadWriteLock inputLock = new ReentrantReadWriteLock();
	private ObjectInputStream input = null; 
	
	public ServerData checkLoop(ServerData commend){
		Object IOData = null;
		ServerData processData=null;
		try {
			inputLock.writeLock().lock();
			outputLock.writeLock().lock();
			output.writeObject(commend); 
			while(IOData==null)IOData = input.readObject();
			processData=(ServerData)IOData;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {  
			inputLock.writeLock().unlock();  
			outputLock.writeLock().unlock();  
        }  
		return processData;
	}
	public void addData(ServerData data){
		try {  
			MessageListLock.writeLock().lock();
			MessageList.add(data); 
        } finally {  
        	MessageListLock.writeLock().unlock();  
        }  
	}
	private ServerData getData(){
		ServerData output;
		try {  
			MessageListLock.writeLock().lock();
			output=MessageList.get(0); 
			MessageList.remove(0);
        } finally {  
        	MessageListLock.writeLock().unlock();  
        }  
		return output;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerData processData=null;
		Object IOData = null;
		try {
			client=new Socket(serverAddress,port);
			client.setSoTimeout(10000);
			output = new ObjectOutputStream(client.getOutputStream());
			input = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			alive=false;
			return;
		}
		while(true){
			try {
				inputLock.writeLock().lock();
				IOData = input.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {  
				inputLock.writeLock().unlock();  
	        }  
			if(IOData!=null){
				processData=(ServerData)IOData;
				Database.addMessage(processData);
			}
			processData=getData();
			if(processData!=null){
				try {
					outputLock.writeLock().lock();
					output.writeObject(processData); 
					output.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {  
					outputLock.writeLock().unlock();  
		        } 
			}
			else{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
