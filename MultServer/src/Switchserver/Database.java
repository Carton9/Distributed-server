package Switchserver;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Database {
		
	private static ReadWriteLock ProcessThreadListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ProcessThread> ProcessThreadList=new ArrayList<ProcessThread>();
	private static int ProcessThreadcounter=0;
	
	private static ReadWriteLock ServerInfoListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerInfo> ServerInfoList=new ArrayList<ServerInfo>();


	public static void addThread(ProcessThread thread){
		try {  
			ProcessThreadListLock.writeLock().lock();
			ProcessThreadList.add(thread); 
        } finally {  
        	ProcessThreadListLock.writeLock().unlock();  
        }  
	}
	public static void loadUserList(ArrayList<ServerInfo> _ServerInfoList){
		try {  
			ServerInfoListLock.writeLock().lock();
			ServerInfoList=_ServerInfoList;
        } finally {  
        	ServerInfoListLock.writeLock().unlock();  
        }
	}
	
	public static void setServerList(ServerInfo input){
		try {  
			ServerInfoListLock.writeLock().lock();
			//ServerInfoList
			for(ServerInfo server:ServerInfoList){
				if(server.serverName==input.serverName&&server.serverType==input.serverType){
					ServerInfoList.set(ServerInfoList.indexOf(server), input);
					break;
				}
			}
        } finally {  
        	ServerInfoListLock.writeLock().unlock();  
        }
	}
	public static ServerInfo getServer(ServerInfo input){
		ServerInfo output = null;
		try {  
			ServerInfoListLock.writeLock().lock();
			for(ServerInfo server:ServerInfoList){
				if(server.serverName==-1){
					if(server.serverType==input.serverType){
						//output=server;
						//break;
						ServerInfo buff=server;
						ServerInfoList.remove(server);
						ServerInfoList.add(buff);
						output=buff;
					}
				}
				else{
					if(server.serverName==input.serverName&&server.serverType==input.serverType){
						output=server;
						break;
					}
				}
			}
        } finally {  
        	ServerInfoListLock.writeLock().unlock();  
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
