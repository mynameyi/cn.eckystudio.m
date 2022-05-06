package cn.eckystudio.m.web;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class JSONEntityRequestModel extends JSONEntityCommonModel {

    public JSONObject toJSONObject(){
        try {
            JSONObject ret = new JSONObject();
            Class cls = this.getClass();
            Field[] fields = cls.getFields();
            for (int i = 0; i < fields.length; i++) {
                Object value = fields[i].get(this);
                if(value instanceof  JSONEntityRequestModel){
                    value = ((JSONEntityRequestModel)value).toJSONObject();
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

    public String toJSON(){
        try {
            Preconditioning();
            HashMap<String, Object> table = new HashMap<>();
            Class cls = this.getClass();
            Field[] fields = cls.getFields();

            for (int i = 0; i < fields.length; i++) {
                //Class<?> c = fields[i].getDeclaringClass();
                Object value = fields[i].get(this);
                if(value instanceof  JSONEntityRequestModel){
                    value = ((JSONEntityRequestModel)value).toJSONObject();
                }else if(value instanceof JSONEntityRequestModel[]){

                    LinkedList<String> list = new LinkedList<String>();
                    for (int j = 0; j < Array.getLength(value); ++i) {
                        Object o = Array.get(value, i);
                        list.addLast( ((JSONEntityRequestModel)o).toJSON());
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

    public String toDeepLink() {
        Preconditioning();
        return "";//GetDeepLink();
    }
    /// <summary>
    /// toJSON前的预置处理方法,例如签名和自动生成一些字段等特殊处理
    /// </summary>
    protected abstract void Preconditioning();
}
