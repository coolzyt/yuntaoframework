package org.yuntao.framework.tool.secure;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * @version 1.00 Jun 29, 2009
 * @author zhaoyuntao
 * 
 * Modified History:
 * 
 */
public class X509CertificateTool {
	private static final String DEFAULT_CA_ROOT_DN = "C=CN, L=四川, O=云涛软件工作室, OU=赵云涛";
	private String caRootDn;
	private static Provider provider = new BouncyCastleProvider();
	static {
		Security.addProvider(provider);
	}
	
	public X509CertificateTool(String caRootDn){
		this.caRootDn = caRootDn;
	}
	
	public X509CertificateTool(){
		this(DEFAULT_CA_ROOT_DN);
	}
	/**
	 * 传入公钥和私钥（一对）,生成CA根证书
	 * 
	 * @param publicKey
	 * @param privateKey
	 * @return
	 */
	public X509Certificate generateCaRootCertificate(
			PublicKey publicKey, PrivateKey privateKey) {
		X509V1CertificateGenerator v1CertGen = new X509V1CertificateGenerator();
		String issuer = caRootDn;
		String subject = caRootDn;
		v1CertGen.setSerialNumber(BigInteger.valueOf(82990590));
		v1CertGen.setIssuerDN(new X509Principal(issuer));
		v1CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60
				* 60 * 24 * 3650));
		v1CertGen.setNotAfter(new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24 * 365)));
		v1CertGen.setSubjectDN(new X509Principal(subject));
		v1CertGen.setPublicKey(publicKey);
		v1CertGen.setSignatureAlgorithm("MD5WITHRSAENCRYPTION");
		X509Certificate cert;
		try {
			cert = v1CertGen.generate(privateKey, "BC");
		} catch (CertificateEncodingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
		return cert;
	}
	private Random serialNumberGenerator = new Random();
	/**
	 * 传入用户公钥和CA的公钥私钥，以及用户的相关信息，生成用户证书
	 * @param publicKey
	 * @param caPrivateKey
	 * @param subjectDN
	 * @return 
	 */
	public X509Certificate generateClientCertificate(PublicKey publicKey,
			PrivateKey caPrivateKey,
			SimpleSubjectDN subjectDN) {
		String issuer = DEFAULT_CA_ROOT_DN;
		 Hashtable                   attrs = new Hashtable();
	        Vector                      order = new Vector();

        attrs.put(X509Principal.C, subjectDN.country);
        attrs.put(X509Principal.O, subjectDN.company);
        attrs.put(X509Principal.L, subjectDN.location);
        attrs.put(X509Principal.CN, subjectDN.username);
        attrs.put(X509Principal.EmailAddress, subjectDN.email);

        order.addElement(X509Principal.C);
        order.addElement(X509Principal.O);
        order.addElement(X509Principal.L);
        order.addElement(X509Principal.CN);
        order.addElement(X509Principal.EmailAddress);
		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
		v3CertGen.reset();
		byte[] serialBytes = new byte[16];
		serialNumberGenerator.nextBytes(serialBytes);
		serialBytes[0] = 0;
		v3CertGen.setSerialNumber(new BigInteger(serialBytes));
		v3CertGen.setIssuerDN(new X509Principal(issuer));
		v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60
				* 60 * 24 * 30));
		v3CertGen.setNotAfter(new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24 * 3650)));
		v3CertGen.setSubjectDN(new X509Principal(order,attrs));
		v3CertGen.setPublicKey(publicKey);
		v3CertGen.setSignatureAlgorithm("MD5WithRSAEncryption");
		X509Certificate cert;
		try {
			cert = v3CertGen.generate(caPrivateKey,"BC");
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		} catch (CertificateEncodingException e) {
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return cert;
	}
	
	public void writeCertificateToPemFile(X509Certificate cert,File file){
		PEMWriter writer = null;
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			writer= new PEMWriter(new FileWriter(file));
			writer.writeObject(cert);
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
	
	public X509Certificate readCertificateFromPemFile(File file){
		PEMReader reader = null;
		try{
			if(!file.exists()){
				throw new IllegalArgumentException("pem文件不存在");
			}
			reader = new PEMReader(new FileReader(file));
			Object obj = reader.readObject();
			return (X509Certificate)obj;
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

	public static class SimpleSubjectDN{
		public String country = "CN";
		public String location = "四川";
		public String company = "云涛软件工作室";
		public String username = "赵云涛";
		public String email = "zyuntao@126.com";
	}
	
	public static void main(String args[]){
		new File("C:/ca-root/jk_smcg/").mkdirs();
		RsaTool tool = new RsaTool();
		KeyPair caKeyPair = tool.generateKeyPair(1024);
		PrivateKey caprivateKey = caKeyPair.getPrivate();
		PublicKey capublicKey = caKeyPair.getPublic();
		
		tool.writeKeyToPemFile((Key)caprivateKey, new File("C:/ca-root/ca-private.pem"));
		X509Certificate caCert = new X509CertificateTool().generateCaRootCertificate((PublicKey)capublicKey, (PrivateKey)caprivateKey);
		new X509CertificateTool().writeCertificateToPemFile(caCert, new File("C:/ca-root/ca-cert.cert"));
		KeyPair keyPair = tool.generateKeyPair(1024);
		tool.writeKeyToPemFile((Key)keyPair.getPrivate(), new File("C:/ca-root/jk_smcg/jk_smcg-private.pem"));
		tool.writeKeyToPemFile((Key)keyPair.getPublic(), new File("C:/ca-root/jk_smcg/jk_smcg-public.pem"));
		SimpleSubjectDN subject = new SimpleSubjectDN();
		subject.username = "JK_SMCG";
		X509Certificate jkSmcgCert = new X509CertificateTool().generateClientCertificate(keyPair.getPublic(),
				(PrivateKey)tool.readKeyFromPemFile(new File("C:/ca-root/ca-private.pem")),subject);
		new X509CertificateTool().writeCertificateToPemFile(jkSmcgCert,new File("C:/ca-root/jk_smcg/jk_smcg.cer"));		
	}
}
