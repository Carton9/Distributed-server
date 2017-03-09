package Subserver;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import DataType.UserInfo;

public class Database {
	public static String serverName;
	private static ReadWriteLock TCPPortLock = new ReentrantReadWriteLock();  
	private static TCPPort TCPPort=null;
	
	private static ReadWriteLock UDPPortListLock = new ReentrantReadWriteLock();  
	private static ArrayList<UDPPort> UDPPortList=new ArrayList<UDPPort>();
	private static int  UDPPortcounter=0;
	
	private static ReadWriteLock ProcessThreadListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ProcessThread> ProcessThreadList=new ArrayList<ProcessThread>();
	private static int ProcessThreadcounter=0;
	
	private static ReadWriteLock UserInfoListLock = new ReentrantReadWriteLock();  
	private static ArrayList<UserInfo> UserInfoList=new ArrayList<UserInfo>();
	
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
	
	public static void setUserList(ArrayList<UserInfo> _UserInfoList){
		try {  
			UserInfoListLock.writeLock().lock();
			UserInfoList=_UserInfoList;
        } finally {  
        	UserInfoListLock.writeLock().unlock();  
        }
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<UserInfo> getUserList(){
		ArrayList<UserInfo> output;
		try {  
			UserInfoListLock.writeLock().lock();
			output=(ArrayList<UserInfo>)UserInfoList.clone();
        } finally {  
        	UserInfoListLock.writeLock().unlock();  
        }
		return output;
	}
	public static UserInfo getUserList(String userName){
		UserInfo output = null;
		try {  
			UserInfoListLock.writeLock().lock();
			for(UserInfo user:UserInfoList){
				if(user.userName==userName){
					output=user;
					break;
				}
			}
        } finally {  
        	UserInfoListLock.writeLock().unlock();  
        }
		return output;
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
	public static UDPPort getUDPPort(){
		UDPPort output=null;
		try {  
			UDPPortListLock.writeLock().lock();
			output=UDPPortList.get(UDPPortcounter);
        } finally {  
        	UDPPortListLock.writeLock().unlock();  
        }  
		UDPPortcounter++;
		UDPPortcounter=UDPPortcounter%UDPPortList.size();
		return output;
	}
}
