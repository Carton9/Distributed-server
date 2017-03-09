package Database;
import java.io.*;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList; 

public class XmlAccessor {
	Element element;
	File f;
	DocumentBuilder db;
	DocumentBuilderFactory dbf;
	//private ReadWriteLock databaselock = new ReentrantReadWriteLock();  
	private ArrayList<UserIntactInfo> database=new ArrayList<UserIntactInfo>();
	   //Load and parse XML file into DOM 
	public XmlAccessor(String fileName){
		element=null; 
	    f =new File(fileName); 
	    db=null;
	    dbf=null;  
	}
	public void loadList(){
		 try{
	          dbf= DocumentBuilderFactory.newInstance(); //返回documentBuilderFactory对象    
	          db =dbf.newDocumentBuilder();//返回db对象用documentBuilderFatory获得返回documentBuildr对象 
	          Document dt= db.parse(f); //得到一个DOM并返回给document对象 
	          element = dt.getDocumentElement();//得到一个elment根元素 
	          ArrayList<UserIntactInfo> newList=new ArrayList<UserIntactInfo>();
	          System.out.println("根元素："+element.getNodeName()); //获得根节点 
	          NodeList childNodes =element.getChildNodes() ;    // 获得根元素下的子节点 
	          for (int i = 0; i < childNodes.getLength(); i++){ 
	        	Node node1 = childNodes.item(i);
	        	UserIntactInfo newUser=new UserIntactInfo();
	        	if("Account".equals(node1.getNodeName())){ 
	        		newUser.userName=node1.getAttributes().getNamedItem("UserName").getNodeValue();
	        		newUser.userPassWord=node1.getAttributes().getNamedItem("PassWord").getNodeValue();
	        		NodeList nodeDetail = node1.getChildNodes();
	        		for (int j = 0; j < nodeDetail.getLength(); j++){
	        			Node detail = nodeDetail.item(j);
	        			if ("emailAddress".equals(detail.getNodeName()))   // 输出code 
	        				newUser.emailAdress=detail.getTextContent();
	        		}
	        		newList.add(newUser);
	        	}
	          }
	          database=newList;
	    }catch(Exception e){System.out.println(e);} 
	}
	public void saveList(){
		 try{
	          dbf= DocumentBuilderFactory.newInstance(); //返回documentBuilderFactory对象    
	          db =dbf.newDocumentBuilder();//返回db对象用documentBuilderFatory获得返回documentBuildr对象 
	          Document dt= db.parse(f); //得到一个DOM并返回给document对象 
	          element = dt.getDocumentElement();//得到一个elment根元素 
	          System.out.println("根元素："+element.getNodeName()); //获得根节点 
	          NodeList childNodes =element.getChildNodes() ;    // 获得根元素下的子节点 
	          for(UserIntactInfo input:database){
	        	  boolean change= false;
	        	  for (int i = 0; i < childNodes.getLength(); i++){ 
		  	        	Node node1 = childNodes.item(i);
		  	        	if(input.userName.equals(node1.getAttributes().getNamedItem("UserName").getNodeValue())){
		  	        		node1.getAttributes().getNamedItem("PassWord").setNodeValue(input.userPassWord);
		  	        		node1.getChildNodes().item(0).setNodeValue(input.emailAdress);
		  	        		change=true;
		  	        		break;
		  	        	}
		  	        }
	        	  if(change==false){
		        	  for (int i = 0; i < childNodes.getLength(); i++){ 
			  	        	Node node1 = childNodes.item(i);
			  	        	if("".equals(node1.getAttributes().getNamedItem("UserName").getNodeValue())){
			  	        		node1.getAttributes().getNamedItem("UserName").setNodeValue(input.userName);
			  	        		node1.getAttributes().getNamedItem("PassWord").setNodeValue(input.userPassWord);
			  	        		node1.getChildNodes().item(0).setNodeValue(input.emailAdress);
			  	        		change=true;
			  	        		break;
			  	        	}
			  	        }
		        	  if(change==false){
		        		 Element user=dt.createElement("Accent");
		        		 Attr userName=dt.createAttribute("UserName"); 
		        		 userName.setValue(input.userName); 
		        		 Attr password=dt.createAttribute("Password"); 
		        		 password.setValue(input.userPassWord); 
		        		 Node emailAddress=dt.createTextNode(input.emailAdress);
		        		 user.setAttributeNode(userName);
		        		 user.setAttributeNode(password);
		        		 user.appendChild(emailAddress);
		        		 element.appendChild(user);
		        	  }
	        	  }
	          }
	        
	    }catch(Exception e){System.out.println(e);} 
	}

	public boolean hasUser(String userName){
		for(UserIntactInfo user:database){
			if(user.userName.equals(userName))return true;
		}
		return false;
	}
	public UserIntactInfo getUser(String userName){
		for(UserIntactInfo user:database){
			if(user.userName.equals(userName))return user;
		}
		return null;
	}
}
