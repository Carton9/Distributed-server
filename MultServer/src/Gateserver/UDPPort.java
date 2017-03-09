package Gateserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import DataType.UserData;


public class UDPPort implements Runnable{
	boolean alive=true;
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
	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] buf = new byte[1024];
		DatagramSocket reader = null;
		try {
			reader = new DatagramSocket(4659);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DatagramPacket packet = new DatagramPacket(buf, 1024);
		
		while(alive){
			try {
				reader.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UserData data=(UserData)openPacket(packet);
			ProcessThread processor=Database.getProcessThread();
			processor.addData(data);
		}
		reader.close();
	}
}
