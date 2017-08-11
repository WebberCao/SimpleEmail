
package com.webber.simpleemail.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import android.content.Context;

import com.webber.simpleemail.app.MyApplication;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class MailHelper {

    private static MailHelper instance;
    private List<MailReceiver> mailList;
    private HashMap<String, Integer> serviceHashMap;
    private Context context;

    public static MailHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MailHelper(context);
        }
        return instance;
    }

    /**
     * 构造函数
     *
     */
    private MailHelper(Context context) {
        this.context = context;
    }

    public String getUpdateUrlStr() throws Exception {
        String urlStr = null;
        if (serviceHashMap == null) {
            serviceHashMap = this.getServeHashMap();
        }
        if (serviceHashMap.get("update") == 1) {
            urlStr = mailList.get(1).getSubject();
        }
        return urlStr;
    }

    public String getUserHelp() throws Exception {
        String userandmoney = null;
        if (serviceHashMap == null) {
            serviceHashMap = this.getServeHashMap();
        }
        if (serviceHashMap.get("userhelp") == 1) {
            userandmoney = mailList.get(3).getSubject();
        }
        return userandmoney;
    }

    public int getAllUserHelp() throws Exception {
        String userandmoney = null;
        int money = 0;
        if (serviceHashMap == null) {
            serviceHashMap = this.getServeHashMap();
        }
        if (serviceHashMap.get("userhelp") == 1) {
            userandmoney = mailList.get(3).getSubject();
        }
        if (userandmoney != null && userandmoney.contains("all-user-100")) {
            money = Integer.parseInt(userandmoney.substring(userandmoney.lastIndexOf("-" + 1),
                    userandmoney.length()));
        }
        return money;
    }

    public boolean getAdControl() throws Exception {
        String ad = null;
        if (serviceHashMap == null) {
            serviceHashMap = this.getServeHashMap();
        }
        if (serviceHashMap.get("adcontrol") == 1) {
            ad = mailList.get(2).getSubject();
        }
        if (ad.equals("ad=close")) {
            return false;
        }
        return true;
    }

    public HashMap<String, Integer> getServeHashMap() throws Exception {
        serviceHashMap = new HashMap<String, Integer>();
        if (mailList == null) {
            mailList = getAllMail("INBOX");
        }
        String serviceStr = mailList.get(0).getSubject();
        if (serviceStr.contains("update 1.0=true")) {
            serviceHashMap.put("update", 1);
        } else if (serviceStr.contains("update 1.0=false")) {
            serviceHashMap.put("update", 0);
        }
        if (serviceStr.contains("adcontrol 1.0=true")) {
            serviceHashMap.put("adcontrol", 1);
        } else if (serviceStr.contains("adcontrol 1.0=false")) {
            serviceHashMap.put("adcontrol", 0);
        }
        if (serviceStr.contains("userhelp 1.0=true")) {
            serviceHashMap.put("userhelp", 1);
        } else if (serviceStr.contains("userhelp 1.0=false")) {
            serviceHashMap.put("userhelp", 0);
        }
        return serviceHashMap;
    }

    /**
     * 取得所有的邮件
     * 
     * @param folderName 文件夹名，例：收件箱是"INBOX"
     * @return　List<MailReceiver> 放有ReciveMail对象的List
     * @throws MessagingException
     */
    public List<MailReceiver> getAllMail(String folderName) throws MessagingException {
        List<MailReceiver> mailList = new ArrayList<MailReceiver>();

        // 连接服务器
/*        Store store=MyApplication.session.getStore("pop3");
        String temp=MyApplication.info.getMailServerHost();
        String host=temp.replace("smtp", "pop");
        
        store.connect(host, MyApplication.info.getUserName(), MyApplication.info.getPassword());*/
        
        
      
/*        String temp=MyApplication.info.getMailServerHost();
        String host=temp.replace("smtp", "imap");
        
        Properties prop = System.getProperties();  
        prop.put("mail.store.protocol", "imap");  
        prop.put("mail.imap.host", host);  
      
        Session session = Session.getInstance(prop);  
      
        IMAPStore store = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器  
      
        store.connect(MyApplication.info.getUserName(), MyApplication.info.getPassword());  
      
        IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱  
        folder.open(Folder.READ_WRITE);  */
        
        Store store=MyApplication.session.getStore("pop3");
        String temp=MyApplication.info.getMailServerHost();
        String host=temp.replace("smtp", "pop3");
        
        store.connect(host, MyApplication.info.getUserName(), MyApplication.info.getPassword());
        
/*        URLName urln = new URLName("pop3", "pop3.163.com", 110, null,  
                "邮箱名（没有@163.com）", "密码");  
        Store store = session.getStore(urln);  
        store.connect();  */
        // 邮件协议为pop3，邮件服务器是pop3.163.com，端口为110，用户名/密码为abcw111222/123456w  

        
        Folder folder = store.getFolder("INBOX");  
        folder.open(Folder.READ_WRITE);  
        
        // 打开文件夹
/*        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);*/
        // 总的邮件数
        int mailCount = folder.getMessageCount();
        if (mailCount == 0) {
            folder.close(true);
            store.close();
            return null;
        } else {
            // 取得所有的邮件
            Message[] messages = folder.getMessages();
             for (int i = 0; i < messages.length; i++) {
                // 自定义的邮件对象
                MailReceiver reciveMail = new MailReceiver((MimeMessage) messages[i]);
                mailList.add(reciveMail);// 添加到邮件列表中
            }
            return mailList;
        }
    }
}
