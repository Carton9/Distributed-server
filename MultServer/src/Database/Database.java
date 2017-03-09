package Database;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import DataType.UserInfo;

public class Database {
	public static String serverName;
	private static ReadWriteLock TCPPortLock = new ReentrantReadWriteLock();  
	private static TCPPort TCPPort=null;
	
	private static ReadWriteLock XmlAccessorListLock = new ReentrantReadWriteLock();  
	private static ArrayList<XmlAccessor> XmlAccessorList=new ArrayList<XmlAccessor>();
	
	private static ReadWriteLock ProcessThreadListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ProcessThread> ProcessThreadList=new ArrayList<ProcessThread>();
	private static int ProcessThreadcounter=0;
	
	private static ReadWriteLock UserInfoListLock = new ReentrantReadWriteLock();  
	private static ArrayList<UserInfo> UserInfoList=new ArrayList<UserInfo>();
	
	private static ReadWriteLock databaselock = new ReentrantReadWriteLock();  
	private static ArrayList<UserIntactInfo> database=new ArrayList<UserIntactInfo>();
	
	public static void addThread(TCPPort thread){
		try {  
			TCPPortLock.writeLock().lock();
			TCPPort=thread; 
        } finally {  
        	TCPPortLock.writeLock().unlock();  
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
	
	@SuppressWarnings("unchecked")
	public static ArrayList<UserIntactInfo> getUserDatabase(){
		ArrayList<UserIntactInfo> output=new ArrayList<UserIntactInfo>();
		try {  
			databaselock.writeLock().lock();
			output=(ArrayList<UserIntactInfo>)database.clone();
        } finally {  
        	databaselock.writeLock().unlock();  
        }
		return output;
	}
	
	public static UserIntactInfo getUserDatabase(String userName){
		UserIntactInfo output = null;
		try {  
			XmlAccessorListLock.writeLock().lock();
			for(XmlAccessor reader:XmlAccessorList){
				if(reader.hasUser(userName)){
					output=reader.getUser(userName);
					break;
				}
			}
        } finally {  
        	XmlAccessorListLock.writeLock().unlock();  
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
	
	public static void addUserInfo(UserInfo input){
		try {  
			UserInfoListLock.writeLock().lock();
			UserInfoList.add(input);
        } finally {  
        	UserInfoListLock.writeLock().unlock();  
        }
	}
	public static void deleteUserInfo(String name){
		try {  
			UserInfoListLock.writeLock().lock();
			for(UserInfo user:UserInfoList){
				if(user.userName.equals(name)){
					UserInfoList.remove(user);
				}
			}
        } finally {  
        	UserInfoListLock.writeLock().unlock();  
        }
	}
	@SuppressWarnings("unchecked")
	public static ArrayList<UserInfo> getUserInfoList(){
		ArrayList<UserInfo> output=new ArrayList<UserInfo>();
		try {  
			UserInfoListLock.writeLock().lock();
			output=(ArrayList<UserInfo>) UserInfoList.clone();
        } finally {  
        	UserInfoListLock.writeLock().unlock();  
        }
		return output;
	}
}








