package cn.eckystudio.m.web;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.eckystudio.m.common.KeyValuePair;
import cn.eckystudio.m.security.RSAUtils;

public class EntityCommonModel {

    protected enum SignType{
        MD5,
        HMAC_SHA256,
        HMAC_SHA1,
        RSA
    }
    protected HashMap<String,String> mExtendMember = new HashMap<String,String>();

    protected String getDeepLinkSign(SignType type, String key_str){
        try {
            Class cls = getClass();
            Field[] fields = cls.getFields();

            KeyValuePair<String,Integer>[] members = new KeyValuePair[mExtendMember.size() + fields.length];
            for(int i=0;i<members.length;i++){
                members[i] = new KeyValuePair();
            }

            //添加命名成员
            for(int i=0;i<fields.length;i++){
                members[i].setKey(fields[i].getName());
                members[i].setValue(0);
            }

            //添加扩展成员
            int index = fields.length;
            int count = mExtendMember.size();
            int i_index = 0;
            for(String k: mExtendMember.keySet()){
                members[i_index + index].setKey(k);
                members[i_index + index].setValue(1);
                ++i_index;
            }
            Arrays.sort(members);

            StringBuilder sb = new StringBuilder();
            for(KeyValuePair<String,Integer> m:members){
                String value;
                if(m.getValue() == 0){
                    value = (String)cls.getField(m.getKey()).get(this);
                }else{
                    value = mExtendMember.get(m.getKey());
                }

                if(!TextUtils.isEmpty(value)){
                    sb.append(m.getKey()+'='+value+'&');
                }
            }

            byte[] hash;
            switch(type){
                case RSA:
                    sb.delete(sb.length()-1,sb.length());
                    return RSAUtils.sign(sb.toString(),key_str);
                case HMAC_SHA256:
                    //hash =  MessageDigest.getInstance("HMAC_SHA256").digest(sb.toString().getBytes("UTF-8"));
                    sb.append("key="+key_str);
                    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                    hash = sha256_HMAC.doFinal(sb.toString().getBytes("UTF-8"));
                    break;
                case HMAC_SHA1:
                    Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
                    SecretKeySpec keySpec = new SecretKeySpec(key_str.getBytes("UTF-8"), "HmacSHA1");
                    sha1_HMAC.init(keySpec);
                    hash = sha1_HMAC.doFinal(sb.toString().getBytes("UTF-8"));
                    break;
                case MD5:
                default:
                    sb.append("key="+key_str);
                    hash = MessageDigest.getInstance("MD5").digest(sb.toString().getBytes("UTF-8"));
                    break;
            }

            StringBuilder md5_str = new StringBuilder();
            for(byte b : hash){
                String hex = Integer.toHexString(b & 0xff);
                if(hex.length() == 1){
                    hex = '0' + hex;
                }
                md5_str.append(hex);
            }
            return md5_str.toString().toUpperCase();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     *
     * @param isEncodeValue 是否使用URL编码参数值
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    protected StringBuilder getDeepLink(boolean isEncodeValue) throws NoSuchFieldException, IllegalAccessException {

        Class cls = getClass();
        Field[] fields = cls.getFields();

        KeyValuePair<String, Integer>[] members = new KeyValuePair[mExtendMember.size() + fields.length];
        for (int i = 0; i < members.length; i++) {
            members[i] = new KeyValuePair();
        }

        //添加命名成员
        for (int i = 0; i < fields.length; i++) {
            members[i].setKey(fields[i].getName());
            members[i].setValue(0);
        }

        //添加扩展成员
        int index = fields.length;
        int count = mExtendMember.size();
        int i_index = 0;
        for (String k : mExtendMember.keySet()) {
            members[i_index + index].setKey(k);
            members[i_index + index].setValue(1);
            ++i_index;
        }
        Arrays.sort(members);

        StringBuilder sb = new StringBuilder();
        for (KeyValuePair<String, Integer> m : members) {
            String value;
            if (m.getValue() == 0) {
                value = (String) cls.getField(m.getKey()).get(this);
            } else {
                value = mExtendMember.get(m.getKey());
            }

            if (!TextUtils.isEmpty(value)) {
                sb.append(m.getKey() + '=' + (isEncodeValue? URLEncoder.encode(value):value) + '&');
            }
        }
        sb.deleteCharAt(sb.length()-1);

        return sb;
    }

    protected  String generateNonceStr(){
        return generateNonceStr(false,16);
    }

    protected  String generateNonceStr(boolean is_fixed_len,int shortest_len){
        Random random = new Random();
        int length = is_fixed_len? shortest_len : random.nextInt(shortest_len) + shortest_len;//生成随机字符串长度，16位起，不超过32位
        String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        StringBuffer sb = new StringBuffer();
        //长度为几就循环几次
        for(int i=0; i<length; ++i){
            //产生0-61的数字
            int number = random.nextInt(str.length());
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    };

    public static String generatedRandomStrByTime(String prefix){
        return generatedRandomStrByTime(prefix,"","yyyyMMddHHmmssSSS");
    }

    public static String generatedRandomStrByTime(String prefix,String postfix,String format)
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String date = sDateFormat.format(new java.util.Date());
        Random r = new Random();
        return  prefix + date + (r.nextInt(900) + 100) + postfix;
    }

    public void addMember(String name,String value){
        mExtendMember.put(name,value);
    }

    public HashMap<String,String> getExtendMembers(){
        return mExtendMember;
    }
}
