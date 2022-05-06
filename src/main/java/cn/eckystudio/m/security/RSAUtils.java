package cn.eckystudio.m.security;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSAUtils {
    /***
     * RSA签名方法，默认使用SHA256哈希签名方法，默认使用UTF-8编码格式
     * @param content
     * @param privateKey
     * @return
     */
    public static String sign(String content,String privateKey){
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            return sign(content,signature,privateKey,"UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sign(String content, Signature signature,String privateKey,String inputCharset){
        try {
            byte[] plainBytes = content.getBytes(inputCharset);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey,Base64.NO_WRAP));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey pk = keyFactory.generatePrivate(priPKCS8);

            signature.initSign(pk);
            signature.update(plainBytes);
            byte[] signBytes = signature.sign();
            signBytes = Base64.encode(signBytes,Base64.NO_WRAP);
            String ret = new String(signBytes,inputCharset);
            return ret;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
