package cn.eckystudio.m.web;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.eckystudio.m.common.KeyValuePair;

public abstract class JSONEntityResponseModel extends JSONEntityCommonModel {

    private String _json;
    public boolean from(String json) {
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
                    if (JSONEntityResponseModel.class.isAssignableFrom(filedType)) {
                        ((JSONEntityResponseModel) f.get(this)).from(((JSONObject) m.getValue()).toString());
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
