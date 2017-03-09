package DataType;

import java.io.Serializable;
import java.util.ArrayList;

public class UserData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7984378695545187181L;
	public String userName;// sender's user name
	public String userCommend;// sender's commend
	public String userPassWord;// sender's password
	
	public String targetUserName;// receiver's user name
	
	public ArrayList<String> UserNameList;
	
	public String text;
}
