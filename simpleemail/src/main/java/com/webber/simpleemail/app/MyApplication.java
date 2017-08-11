package com.webber.simpleemail.app;
import java.io.InputStream;
import java.util.ArrayList;

import javax.mail.Session;
import javax.mail.Store;

import com.webber.simpleemail.bean.MailInfo;

import android.app.Application;

public class MyApplication extends Application {
	public static Session session = null;
	public static Store getStore() {
		return store;
	}

	public static void setStore(Store store) {
		MyApplication.store = store;
	}

	public static MailInfo info=new MailInfo();
	
	private static Store store;
    private ArrayList<InputStream> attachmentsInputStreams;

    public ArrayList<InputStream> getAttachmentsInputStreams() {
        return attachmentsInputStreams;
    }

    public void setAttachmentsInputStreams(ArrayList<InputStream> attachmentsInputStreams) {
        this.attachmentsInputStreams = attachmentsInputStreams;
    }
	
}
