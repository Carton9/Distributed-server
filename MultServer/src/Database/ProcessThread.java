package Database;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import DataType.ServerData;
import DataType.UserInfo;

public class ProcessThread implements Runnable {
	private static ReadWriteLock UserDataListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerData> UserDataList=new ArrayList<ServerData>();
	
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
		while(true){
			ServerData Data=getData();
			if("NCQI".equals(Data.serverCommend)){
				UserIntactInfo info=Database.getUserDatabase(Data.userName);
				UserInfo input= new UserInfo();
				
				if(info.userPassWord.equals(Data.userPassWord)){
					Data.succeed=true;
					input.userName=info.userName;
					input.userAddress=new InetSocketAddress(info.userAddress,9090);
					Database.addUserInfo(input);
				}
				else Data.succeed=false;
				Data.userList=Database.getUserInfoList();
				Data.targetServerName=Data.serverName;
				Data.serverName=Database.serverName;
				Database.getTCPPort().addData(Data);
			}
			if(Data.serverCommend.equals("SNCL")){
				Data.userList=Database.getUserInfoList();
				Data.targetServerName=Data.serverName;
				Data.serverName=Database.serverName;
				Database.getTCPPort().addData(Data);
			}
			if(Data.serverCommend.equals(""));
			if(Data.serverCommend.equals(""));
		}
	}

}
