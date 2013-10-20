package org.yuntao.framework.tool.secure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

/** 
 * <p>Title:</p>
 * <p>Description:</p>
 * @version 1.00 
 * @since Jun 18, 2010
 * @author zhaoyuntao
 * 抽象的加解密算法类
 * Modified History: 
 *  
 */
public class AbstractSecureTool {
	private static Logger log = Logger.getLogger(AbstractSecureTool.class);
	private String algorithm;
	public AbstractSecureTool(String algorithm){
		this.algorithm = algorithm;
	}
	private byte[] processData(Cipher c,byte[] input){
		ByteArrayInputStream bais = new ByteArrayInputStream(input);
		CipherInputStream cis = new CipherInputStream(bais,c);
		int p = 0 ;
		byte[] buffer = new byte[64];
		ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
		try {
			while((p=cis.read(buffer))!=-1){
				bos.write(buffer,0,p);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bos.toByteArray();
	} 
	
	/**
	 * @param key
	 * @param data
	 * @return 使用密钥对数据进行加密
	 */
	public byte[] encryptData(Key key,byte[] data){
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.ENCRYPT_MODE, key);
			return processData(c,data);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * @param key
	 * @param data
	 * @return 使用密钥对数据进行解密
	 */
	public byte[] decryptData(Key key,byte[] data){
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.DECRYPT_MODE, key);
			return processData(c,data);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} 
		
	}
	
    public void encryptFile(File in, File out,Key key) throws IOException, GeneralSecurityException {
        InputStream is=null;
        CipherOutputStream output = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            output = new CipherOutputStream(
                    new BufferedOutputStream(new FileOutputStream(out)), cipher);
            is = new BufferedInputStream(new FileInputStream(in));
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
            	output.write(buffer, 0, read);
            }
        } finally {
            try {
                if (is != null)
                    is.close();
                if (output != null)
                	output.close();
            } catch (IOException e) {
            	log.error(e.getMessage(), e);
            }
        }
    }
    
    
    
    public void decryptFile(File in, File out,Key key) throws IOException, GeneralSecurityException {
        OutputStream output = null;
        CipherInputStream is = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            is = new CipherInputStream(new BufferedInputStream(
                    new FileInputStream(in)), cipher);
            output = new BufferedOutputStream(new FileOutputStream(out));
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } finally {
            try {
                if(is!=null) is.close();
                if(output!=null) output.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
	/**
	 * @param key
	 * @param file 
	 * 将密钥写入PEM文件
	 */
	public void writeKeyToPemFile(Key key,File file){
		PEMWriter writer = null;
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			writer= new PEMWriter(new FileWriter(file));
			writer.writeObject(key);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	/**
	 * @param file
	 * @return 从PEM文件读取密钥
	 */
	public Key readKeyFromPemFile(File file){
		PEMReader reader = null;
		try{
			if(!file.exists()){
				throw new IllegalArgumentException("pem文件不存在");
			}
			reader = new PEMReader(new FileReader(file));
			Object obj = reader.readObject();
			if(obj instanceof KeyPair){//读取私钥PEM文件会返回密钥对类型
				return (Key)((KeyPair)obj).getPrivate();
			}
			return (Key)obj;
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
