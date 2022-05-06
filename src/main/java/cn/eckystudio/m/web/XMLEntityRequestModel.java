package cn.eckystudio.m.web;

import android.text.TextUtils;
import android.util.Xml;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Map;

import cn.eckystudio.m.common.KeyValuePair;

/*
 * Copyright (C) 2017 Ecky Studio
 * Author:Ecky Leung(liangyi)
 * Creation Date:2019-08-09 15:59
 * Function:provide some usual methods about Screen,Layout to use easily
 */
public abstract class XMLEntityRequestModel extends XMLEntityCommonModel {
    public String toXML(){
        try {
            preconditioning();//执行预处理方法

            Class cls = this.getClass();
            Field[] fields = cls.getFields();//cls.getDeclaredFields();
            XmlSerializer serializer = Xml.newSerializer();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.setOutput(baos,"UTF-8");

            serializer.startTag(null, "xml");

            //写入命名成员
            for (int i = 0; i < fields.length; i++) {
                String memberName = fields[i].getName();
//                serializer.startTag(null, memberName);
                String memberValue = (String) fields[i].get(this);
                if(!TextUtils.isEmpty(memberValue)) {
                    serializer.startTag(null, memberName);
                    serializer.text(memberValue);
                    serializer.endTag(null, memberName);
                }
//                serializer.endTag(null, memberName);
            }

            //写入扩展成员
            for(Map.Entry<String,String> m : mExtendMember.entrySet()){
                if(!TextUtils.isEmpty(m.getValue())){
                    serializer.startTag(null, m.getKey());
                    serializer.text(m.getValue());
                    serializer.endTag(null, m.getKey());
                }
            }

            serializer.endTag(null, "xml");
            serializer.flush();

            System.out.println(serializer.toString());

            String ret =  baos.toString();
            baos.close();

            return ret;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    /***
     * 签名前的预处理方法
     */
    protected abstract void preconditioning();
}
