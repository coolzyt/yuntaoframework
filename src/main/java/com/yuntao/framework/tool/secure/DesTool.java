package org.yuntao.framework.tool.secure;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;


/** 
 * <p>Title:</p>
 * <p>Description:</p>
 * @version 1.00 
 * @since Jun 18, 2010
 * @author zhaoyuntao
 * DES加密工具
 * Modified History: 
 *  
 */
public class DesTool extends AbstractSecureTool{
    private static Logger logger = Logger.getLogger(DesTool.class);

    private static final String DES="DES";
    
    private static final byte[] DEFAULT_DES_KEY=new byte[]{
        0x57,0x64,(byte)0xab,0x34,(byte)0xcd,0x2d,(byte)0xe4,(byte)0xf8
    };
    
    public DesTool(){
    	super(DES);
    }
  
    public static Key initKey(byte[] keyData){
        return new SecretKeySpec(keyData, "DES");

    	
    }
    
    public static Key getDefaultKey(){
    	return initKey(DEFAULT_DES_KEY);
    }

    public static Key generatekey(){ 
      try{
          SecretKeyFactory keyFac = SecretKeyFactory.getInstance(DES);
          KeySpec keySpec = new DESKeySpec(DEFAULT_DES_KEY);
          return keyFac.generateSecret(keySpec);
      }catch(GeneralSecurityException e){
          logger.error(e.getMessage(), e);
          throw new IllegalArgumentException("生成des密钥时发生错误",e);
      }
    }
    
    

    
    
    
    
    

}
