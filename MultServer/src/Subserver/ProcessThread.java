package Subserver;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import DataType.ServerData;
import DataType.UserInfo;

public class ProcessThread implements Runnable{
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
	
	private void CTC(ServerData _message){
		ServerData sendMessage=new ServerData();
		sendMessage.userName=_message.userName;
		sendMessage.userCommend=_message.userCommend;
		sendMessage.text=_message.text;
		sendMessage.targetUserAddress=Database.getUserList(_message.targetUserName).userAddress;
		UDPPort sender=Database.getUDPPort();
		sender.addData(sendMessage);
	}
	private void CTS(ServerData _message){
		//ServerData sendMessage=new ServerData();
		System.out.println(_message.text);
	}
	private void CTA(ServerData _message){
		ServerData sendMessage=new ServerData();
		sendMessage.userName=_message.userName;
		sendMessage.userCommend=_message.userCommend;
		sendMessage.text=_message.text;
		ArrayList<UserInfo> UserInfoList=Database.getUserList();
		for(UserInfo user:UserInfoList){
			sendMessage.targetUserAddress=user.userAddress;
			UDPPort sender=Database.getUDPPort();
			sender.addData(sendMessage);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true){
			ServerData message=getData();
			if(message==null)continue;
			if("NCQI".equals(message.serverCommend)){
				ArrayList<String> newList= new ArrayList<String>();
				message.serverName=Database.serverName;
				message.targetServerName="Database";
				ServerData messageReplay=Database.getTCPPort().checkLoop(message);
				if(messageReplay.succeed){
					ArrayList<UserInfo> newlist=messageReplay.userList;
					Database.setUserList(newlist);
					for(UserInfo user:newlist){
						newList.add(user.userName);
					}
					ServerData sendMessage= new ServerData();
					sendMessage.userName="Server";
					sendMessage.userCommend="GNL";
					sendMessage.UserNameList=newList;
					CTA(sendMessage);
				}
			}
			else if("SNCL".equals(message.serverCommend)){
				
			}
			else if("NRTM".equals(message.serverCommend)){
				if("CTC".equals(message.userCommend))CTC(message);
				else if("CTA".equals(message.userCommend))CTA(message);
				else if("CTS".equals(message.userCommend))CTS(message);
			}
		}
	
	}

}
