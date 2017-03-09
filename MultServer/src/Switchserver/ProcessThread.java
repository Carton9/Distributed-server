package Switchserver;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DataType.ServerData;

public class ProcessThread implements Runnable{
	private static ReadWriteLock UserDataListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerData> UserDataList=new ArrayList<ServerData>();
	private static Pattern Subserverpattern = Pattern.compile("Subserver");
	private static Pattern Gateserverpattern = Pattern.compile("Gateserver");
	private static Pattern Databasepattern = Pattern.compile("Database");
	private static Pattern Numberpattern = Pattern.compile("[0-9]*");
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
	public ServerInfo makeFindingPacket(ServerData input){
		ServerInfo output=new ServerInfo();
		if(input.targetServerName=="Gateserver"){
			output.serverType=ServerType.Gateserver;
			output.serverName=-1;
		}
		else if(input.targetServerName=="Subserver"){
			output.serverType=ServerType.Subserver;
			output.serverName=-1;
		}
		else if(input.targetServerName=="Database"){
			output.serverType=ServerType.Database;
			output.serverName=-1;
		}
		else{
			 Matcher matcherS = Subserverpattern.matcher(input.targetServerName);
			 Matcher matcherG = Gateserverpattern.matcher(input.targetServerName);
			 Matcher matcherD = Databasepattern.matcher(input.targetServerName);
			 Matcher matcherN = Numberpattern.matcher(input.targetServerName);
			 boolean s= matcherS.matches();
			 boolean g= matcherG.matches();
			 boolean d= matcherD.matches();
			 if(s)output.serverType=ServerType.Subserver;
			 else if(g)output.serverType=ServerType.Gateserver;
			 else if(d)output.serverType=ServerType.Database;
			 else output.serverType=ServerType.Error;
			 output.serverName=Integer.parseInt(matcherN.group());
		}
		return output;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			ServerData data=getData();
			ServerInfo note=makeFindingPacket(data);
			ServerInfo sender=Database.getServer(note);
			switch(sender.serverType){
				case Database:
					{
						data.targetServerName="Database";
						data.targetServerName+=sender.serverName;
						break;}
				case Gateserver:
					{
						data.targetServerName="Gateserver";
						data.targetServerName+=sender.serverName;
						break;}
				case Subserver:
					{
						data.targetServerName="Subserver";
						data.targetServerName+=sender.serverName;
						break;}
				case Error:
					{
						data.targetServerName="Error";
						data.targetServerName+=sender.serverName;
						break;}
			}
			sender.serverPort.addData(data);
		}

	}
}
