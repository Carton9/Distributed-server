package Gateserver;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import DataType.ServerData;

public class Database {
	public static String serverName;
	private static ReadWriteLock TCPPortLock = new ReentrantReadWriteLock();  
	private static TCPPort TCPPort=null;
	
	private static ReadWriteLock UDPPortListLock = new ReentrantReadWriteLock();  
	private static ArrayList<UDPPort> UDPPortList=new ArrayList<UDPPort>();
	
	private static ReadWriteLock ServerDataListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerData> ServerDataList=new ArrayList<ServerData>();
	
	private static ReadWriteLock ProcessThreadListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ProcessThread> ProcessThreadList=new ArrayList<ProcessThread>();
	private static int ProcessThreadcounter=0;
	
	public static void addThread(TCPPort thread){
		try {  
			TCPPortLock.writeLock().lock();
			TCPPort=thread; 
        } finally {  
        	TCPPortLock.writeLock().unlock();  
        }  
	}
	public static void addThread(UDPPort thread){
		try {  
			UDPPortListLock.writeLock().lock();
			UDPPortList.add(thread); 
        } finally {  
        	UDPPortListLock.writeLock().unlock();  
        }  
	}
	public static void addThread(ProcessThread thread){
		try {  
			ProcessThreadListLock.writeLock().lock();
			ProcessThreadList.add(thread); 
        } finally {  
        	ProcessThreadListLock.writeLock().unlock();  
        }  
	}
	
	public static void addMessage(ServerData input){
		try {  
			ServerDataListLock.writeLock().lock();
			ServerDataList.add(input); 
        } finally {  
        	ServerDataListLock.writeLock().unlock();  
        }  
	}
	public static TCPPort getTCPPort(){
		TCPPort output=null;
		try {  
			TCPPortLock.writeLock().lock();
				output=TCPPort;
        } finally {  
        	TCPPortLock.writeLock().unlock();  
        }  
		return output;
	}
	public static ProcessThread getProcessThread(){
		ProcessThread output=null;
		try {  
			ProcessThreadListLock.writeLock().lock();
			output=ProcessThreadList.get(ProcessThreadcounter);
        } finally {  
        	ProcessThreadListLock.writeLock().unlock();  
        }  
		ProcessThreadcounter++;
		ProcessThreadcounter=ProcessThreadcounter%ProcessThreadList.size();
		return output;
	}
}
