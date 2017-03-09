package Gateserver;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import DataType.ServerData;
import DataType.UserData;

public class ProcessThread implements Runnable{
	private static ReadWriteLock UserDataListLock = new ReentrantReadWriteLock();  
	private static ArrayList<UserData> UserDataList=new ArrayList<UserData>();
	
	public void addData(UserData data){
		try {  
			UserDataListLock.writeLock().lock();
			UserDataList.add(data); 
        } finally {  
        	UserDataListLock.writeLock().unlock();  
        }  
	}
	private UserData getData(){
		UserData output;
		try {  
			UserDataListLock.writeLock().lock();
			output=UserDataList.get(0); 
			UserDataList.remove(0);
        } finally {  
        	UserDataListLock.writeLock().unlock();  
        }  
		return output;
	}
	private ServerData packetData(UserData input){
		ServerData output=new ServerData();
		output.userName=input.userName;
		output.userCommend=input.userCommend;
		output.userPassWord=input.userPassWord;
		output.targetUserName=input.targetUserName;
		output.text=input.text;
		output.serverName="Gateserver";
		output.targetServerName="Subserver";
		return output;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			UserData userMeaasge=getData();
			ServerData serveMessage=packetData(userMeaasge);
			if("JNC".equals(serveMessage.userCommend)){
				serveMessage.serverCommend="NCQI";
				TCPPort sender=Database.getTCPPort();
				sender.addData(serveMessage);
			}
			else if("GNL".equals(serveMessage.userCommend)){
				serveMessage.serverCommend="SNCL";
				TCPPort sender=Database.getTCPPort();
				sender.addData(serveMessage);
			}
			else{
				serveMessage.serverCommend="NRTM";
				TCPPort sender=Database.getTCPPort();
				sender.addData(serveMessage);
			}
			
		}
	}

}
