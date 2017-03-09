package Subserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import DataType.ServerData;
import DataType.UserData;

public class UDPPort implements Runnable{
	private static ReadWriteLock MessageListLock = new ReentrantReadWriteLock();  
	private static ArrayList<ServerData> MessageList=new ArrayList<ServerData>();
	public boolean alive;
	public static DatagramPacket makePacket(Object Data,InetSocketAddress address){
	    byte[] by=new byte[1024*1024];
	    ByteArrayOutputStream bs=new ByteArrayOutputStream();
	    ObjectOutputStream bo;
	    try {
	        bo = new ObjectOutputStream(bs);
			bo.writeObject(Data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    by=bs.toByteArray();
		DatagramPacket data=new DatagramPacket(by,by.length,address);
		return data;
	}
	public static Object openPacket(DatagramPacket Data){
		ByteArrayInputStream bs=new ByteArrayInputStream(Data.getData());
		Object output = null;
		try {
			ObjectInputStream os=new ObjectInputStream(bs);
			output = os.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
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
		DatagramSocket sender = null;
		DatagramPacket packet = null;
		try {
			sender = new DatagramSocket(6600);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(alive){
			ServerData rawPacket=getData();
			UserData data=new UserData();
			data.userName=rawPacket.userName;
			data.userCommend=rawPacket.userCommend;
			data.userPassWord="";
			data.targetUserName=rawPacket.targetUserName;
			data.UserNameList=rawPacket.UserNameList;
			data.text=rawPacket.text;
			packet=makePacket(data,rawPacket.targetUserAddress);
			try {
				if(packet!=null)sender.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sender.close();
	}
}
