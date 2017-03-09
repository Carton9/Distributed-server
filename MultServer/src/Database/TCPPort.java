package Database;

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
import Subserver.Database;
import Subserver.ProcessThread;

public class TCPPort implements Runnable {
	private static ReadWriteLock UserDataListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerData> UserDataList=new ArrayList<ServerData>();
	private Socket client = null;  
	boolean alive;
	
	final private int port= 9900;
	private InetAddress serverAddress;
	private ReadWriteLock outputLock = new ReentrantReadWriteLock(); 
	private ObjectOutputStream output = null;  
	private ReadWriteLock inputLock = new ReentrantReadWriteLock();
	private ObjectInputStream input = null;
	public void addData(ServerData data){
		try {  
			UserDataListLock.writeLock().lock();
			UserDataList.add(data); 
        } finally {  
        	UserDataListLock.writeLock().unlock();  
        }  
	}
	private ServerData getData(){
		ServerData output;
		try {  
			UserDataListLock.writeLock().lock();
			output=UserDataList.get(0); 
			UserDataList.remove(0);
        } finally {  
        	UserDataListLock.writeLock().unlock();  
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
			}finally {  
				inputLock.writeLock().unlock();  
	        }  
			if(IOData!=null){
				processData=(ServerData)IOData;
				ProcessThread processor=Database.getProcessThread();
				processor.addData(processData);
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
				}finally {  
					outputLock.writeLock().unlock();  
		        }
			}
		}
	}

}
