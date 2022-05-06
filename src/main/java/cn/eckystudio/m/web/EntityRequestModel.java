package cn.eckystudio.m.web;

import android.text.TextUtils;
import android.util.Xml;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class EntityRequestModel extends EntityCommonModel {
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

    public String toJSON(){
        try {
            preconditioning();
            HashMap<String, Object> table = new HashMap<>();
            Class cls = this.getClass();
            Field[] fields = cls.getFields();

            for (int i = 0; i < fields.length; i++) {
                //Class<?> c = fields[i].getDeclaringClass();
                Object value = fields[i].get(this);
                if(value instanceof  EntityRequestModel){
                    value = ((EntityRequestModel)value).toJSONObject();
                }else if(value instanceof JSONEntityRequestModel[]){

                    LinkedList<String> list = new LinkedList<String>();
                    for (int j = 0; j < Array.getLength(value); ++i) {
                        Object o = Array.get(value, i);
                        list.addLast( ((EntityRequestModel)o).toJSON());
                    }
                    value = list.toArray();
                }
                table.put(fields[i].getName(),value == null ? "": value);
            }

            //添加未名元素
            for(Map.Entry<String,String> m : mExtendMember.entrySet()){
                if(!TextUtils.isEmpty(m.getValue())){
                    table.put(m.getKey(),m.getValue());
                }
            }
            return JSONUtils.toJSON(table);
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public JSONObject toJSONObject(){
        try {
            JSONObject ret = new JSONObject();
            Class cls = this.getClass();
            Field[] fields = cls.getFields();
            for (int i = 0; i < fields.length; i++) {
                Object value = fields[i].get(this);
                if(value instanceof  EntityRequestModel){
                    value = ((EntityRequestModel)value).toJSONObject();
                }
                ret.put(fields[i].getName(),value);
            }
            return ret;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toDeepLink() throws NoSuchFieldException, IllegalAccessException {
        return toDeepLink(false);
    }

    /***
     * 生成DeepLink字符串
     * @param isURLEncodeValue 是否使用URL编码参数值，为了防止参数值有特殊字符，一般需要对参数值进行加密
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public String toDeepLink(boolean isURLEncodeValue) throws NoSuchFieldException, IllegalAccessException {
        preconditioning();
        return getDeepLink(isURLEncodeValue).toString();
    }

    /// <summary>
    /// toXXXX前的预置处理方法,例如签名和自动生成一些字段等特殊处理
    /// </summary>
    protected abstract void preconditioning();
}
