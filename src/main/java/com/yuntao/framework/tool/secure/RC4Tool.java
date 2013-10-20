package org.yuntao.framework.tool.secure;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * 
 * @version 1.00
 * @since 2012-11-13
 * @author zhaoyuntao
 * 
 */
public class RC4Tool extends AbstractSecureTool {
    private static final String RC4 = "RC4";

    private static final byte[] DEFAULT_RC4_KEY = new byte[] { 0x57, 0x64, (byte) 0xab, 0x34, (byte) 0xcd, 0x2d,
            (byte) 0xe4, (byte) 0xf8 };

    public RC4Tool() {
        super(RC4);
    }

    public static Key initKey(byte[] keyData) {
        return new SecretKeySpec(keyData, "RC4");
    }

    public static Key getDefaultKey() {
        return initKey(DEFAULT_RC4_KEY);
    }


    public static Key generatekey(){ 
      try{
          SecretKeyFactory keyFac = SecretKeyFactory.getInstance(RC4);
          KeySpec keySpec = new DESKeySpec(DEFAULT_RC4_KEY);
          return keyFac.generateSecret(keySpec);
      }catch(GeneralSecurityException e){
          throw new IllegalArgumentException("生成des密钥时发生错误",e);
      }
    }
}
