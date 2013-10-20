package org.yuntao.framework.tool.secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.yuntao.framework.util.ByteArrayUtil;

/** 
 * <p>Title:</p>
 * <p>Description:</p>
 * @version 1.00 
 * @since Jun 18, 2010
 * @author zhaoyuntao
 * 摘要算法,提供md5和sha1两种
 * Modified History: 
 *  
 */
public class DigestUtil {
	  /**
     * 计算字符串的消息摘要,使用SHA1算法
     * 
     * @param text
     *            文本
     * @return 消息摘要(160bit)的十六进制表示
     */
    public static String sha1MessageDigest(String text) {
        return messageDigest(text, "SHA1");
    }

    /**
     * 计算消息摘要,使用MD5算法
     * 
     * @param text
     *            文本
     * @return 消息摘要(128位)的十六进制表示
     */
    public static String md5MessageDigest(String text) {
        return messageDigest(text, "MD5");
    }

    /**
     * hmacsha1是基于密钥的摘要算法，比传统的md5和sha1算法更安全
     * @param key
     * @param text
     * @return
     */
    public static String hmacSHA1(String key,String text){
        try {
            SecretKeySpec secret  = new SecretKeySpec(key.getBytes("utf8"),"HmacSHA1");
            Mac mac               = Mac.getInstance("HmacSHA1");
            mac.init(secret);
            return ByteArrayUtil
            .byteArray2HexString(mac.doFinal(text.getBytes("utf8")));
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA1加密异常",e);
        }
    }
    
    // 消息摘要
    private static String messageDigest(String text, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return ByteArrayUtil
                    .byteArray2HexString(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            // 不会发生
            throw new RuntimeException("won't happen!");
        }
    }

}
