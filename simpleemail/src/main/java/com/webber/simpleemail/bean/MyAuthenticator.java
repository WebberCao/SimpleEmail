package com.webber.simpleemail.bean;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuthenticator extends Authenticator {
	String userName=null;
    String password=null;
       
    public MyAuthenticator(){
    }
    public MyAuthenticator(String username, String password) { 
        this.userName = username; 
        this.password = password; 
    }
    
    /**
     * 登入校验
     */
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, password);
	} 
}
