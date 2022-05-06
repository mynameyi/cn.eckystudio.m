package cn.eckystudio.m.web;

import android.util.Xml;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class EntityResponseModel extends EntityCommonModel {
    public boolean fromDeepLink(String deepLinkText){
        return false;
    }

    public boolean fromXML(String xmlText)
    {
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


    private String _json;
    public boolean fromJSON(String json){
        _json = json;
        if (!deserialize(json))
            return false;

        return postconditioning();
    }

    private  boolean deserialize(String json){
        try {
            mExtendMember.clear();
            Class<?> clz = this.getClass();
            HashMap<String, Object> table = JSONUtils.parseObject(json, HashMap.class);
            for(Map.Entry<String,Object> m : table.entrySet()){
                try {
                    Field f = clz.getField(m.getKey());
                    Class<?> filedType = f.getType();
                    if (EntityResponseModel.class.isAssignableFrom(filedType)) {
                        ((EntityResponseModel) f.get(this)).fromJSON(((JSONObject) m.getValue()).toString());
                    } else if (String.class.equals(filedType)) {
                        f.set(this, m.getValue().toString());
                    } else {
                        addMember(m.getKey(), m.getValue().toString());
                    }
                }catch (NoSuchFieldException noSuchFieldException){
                    addMember(m.getKey(), m.getValue().toString());
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    protected abstract boolean postconditioning();
}
