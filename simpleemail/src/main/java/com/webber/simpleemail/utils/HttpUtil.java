package com.webber.simpleemail.utils;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import com.webber.simpleemail.app.MyApplication;
import com.webber.simpleemail.bean.Attachment;
import com.webber.simpleemail.bean.MailInfo;
import com.webber.simpleemail.bean.MyAuthenticator;
public class HttpUtil {
	/**
	 * 连接邮箱
	 */
	public Session login(){
		//连接服务器
		Session session=isLoginRight(MyApplication.info);
		return session;
	}
    
	/**
	 * 登入操作
	 * @param info
	 * @return
	 */
	private Session isLoginRight(MailInfo info) {
		//判断是否要登入验证
		MyAuthenticator authenticator=null;
		if(info.isValidate()){
			//创建一个密码验证器
			authenticator=new MyAuthenticator(info.getUserName(), info.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession=Session.getDefaultInstance(info.getProperties(), authenticator);
		try {
			Transport transport=sendMailSession.getTransport("smtp");
			transport.connect(info.getMailServerHost(), info.getUserName(), info.getPassword());
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
		return sendMailSession;
	}
	
	/**
	 * 以文本格式发送邮件
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public boolean sendTextMail(MailInfo mailInfo, Session sendMailSession) {
		// 判断是否需要身份认证
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address address=new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(address);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address[] tos = null;
			String[] receivers = mailInfo.getReceivers();
			if (receivers != null) {
				// 为每个邮件接收者创建一个地址
				tos = new InternetAddress[receivers.length];
				for (int i = 0; i < receivers.length; i++) {
					tos[i] = new InternetAddress(receivers[i]);
				}
			} else {
				return false;
			}
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipients(Message.RecipientType.TO, tos);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();

			Multipart mm = new MimeMultipart();// 新建一个MimeMultipart对象用来存放多个BodyPart对象
			// 设置信件文本内容
			BodyPart mdp = new MimeBodyPart();// 新建一个存放信件内容的BodyPart对象
			mdp.setContent(mailContent, "text/html;charset=UTF-8");// 给BodyPart对象设置内容和格式/编码方式
			mm.addBodyPart(mdp);// 将含有信件内容的BodyPart加入到MimeMultipart对象中

			Attachment affInfos;
			FileDataSource fds1;
			List<Attachment> list = mailInfo.getAttachmentInfos();
				for (int i = 0; i < list.size(); i++) {
					affInfos = list.get(i);
					fds1 = new FileDataSource(affInfos.getFilePath());
					mdp = new MimeBodyPart();
					mdp.setDataHandler(new DataHandler(fds1));
					try {
						mdp.setFileName(MimeUtility.encodeText(fds1.getName()));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					mm.addBodyPart(mdp);
				}
			mailMessage.setContent(mm);
			mailMessage.saveChanges();
			
			
			// 设置邮件支持多种格式
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);
            
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
