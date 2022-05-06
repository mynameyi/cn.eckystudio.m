package cn.eckystudio.m.web;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

/*
 * Copyright (C) 2017 Ecky Studio
 * Author:Ecky Leung(liangyi)
 * Creation Date:2019-08-09 16:00
 * Function:provide some usual methods about Screen,Layout to use easily
 */
public abstract class XMLEntityResponseModel extends XMLEntityCommonModel {
    public boolean from(String xmlText){
        if(!parseXML(xmlText))
            return false;

        return postconditioning();
    }

    private boolean parseXML(String xmlText)
    {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(xmlText.getBytes("UTF-8"));
            Class cls = this.getClass();
            //Field[] fields = cls.getFields();//cls.getDeclaredFields();

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(bais, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
//                        for(int i=0;i<fields.length;i++){
//                            if(fields[i].getName().equals(name))
//                            {
//                                parser.next();
//                                fields[i].set(this,parser.getText());
//                                break;
//                            }
//                        }
                        if(parser.getDepth() < 2) //跳过根节点
                            break;

                        try {
                            Field f = cls.getField(name);
                            parser.next();
                            f.set(this, parser.getText());
                        }catch(NoSuchFieldException necep){
                            parser.next();
                            addMember(name,parser.getText());
                        }

                }
                eventType = parser.next();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract boolean postconditioning();
}
