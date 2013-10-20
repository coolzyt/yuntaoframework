package org.yuntao.framework.util;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-7-26
 * @author zhaoyuntao
 * 
 */
public class NamingUtil {
	public static String underLine2UpperCase(String property){
		StringBuilder buf = new StringBuilder(property.replace(".", "_"));
		for(int i=1;i<buf.length()-1;i++){
			if(
                Character.isLowerCase( buf.charAt(i-1) ) &&
                buf.charAt(i)=='_' &&
                Character.isLowerCase( buf.charAt(i+1) )
	        ){
				buf.setCharAt(i+1, Character.toUpperCase(buf.charAt(i+1)));
				buf.deleteCharAt(i);
			}
		}
		return buf.toString();
	}
	
	
	public static String upperCase2UnderLine(String property){
		StringBuilder buf = new StringBuilder(property.replace('.', '_') );
        for (int i=1; i<buf.length()-1; i++) {
            if (
                Character.isLowerCase( buf.charAt(i-1) ) &&
                Character.isUpperCase( buf.charAt(i) ) &&
                Character.isLowerCase( buf.charAt(i+1) )
            ) {
            	
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase();
	}
	
	public static void main(String args[]){
		System.out.println(NamingUtil.upperCase2UnderLine("modifyTimeLast"));
	}
}
