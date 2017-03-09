package DataType;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;



public class ServerData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 630384327118127877L;
	public String serverName;
	public String serverCommend;
	public String targetServerName;
	public double serverUsage;
	
	public boolean succeed;
	public ArrayList<UserInfo> userList=new ArrayList<UserInfo>();
	
	public String userName;// sender's user name
	public String userCommend;// sender's commend
	public String userPassWord;// sender's password
	public InetSocketAddress userAddress;// receiver's address
	
	public String targetUserName;// receiver's user name
	public InetSocketAddress targetUserAddress;// receiver's address
	public ArrayList<String> UserNameList;
	
	public String text;

}

