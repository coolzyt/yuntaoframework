package org.yuntao.framework.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
public class ArrayUtil {
    /**
     * 等价于javascript的join方法
     * eg: String[] arr={"A","B","C"}
     *     join(arr)的结果是 "A,B,C"
     * 
     * 使用逗号,把一个字符串数组合并为一个字符串
     * @param array 字符串数组
     * @return 合并后的字符串
     * @see #join(String[], String)
     */
    public static String join(String[] array) {
        return join(array, ",");
    }

    /**
     * 可以指定合并的字符串分隔符号
     * eg: String[] arr={"A","B","C"}
     *     join(arr,"##")的结果为"A##B##C"
     * 合并字符串数组,指定合并字符串
     * @param array 字符串数组
     * @param delim 合并字符串
     * @return 合并后的字符串
     */
    public static String join(String[] array, String delim) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            sb.append(delim);
        }
        return sb.substring(0, sb.length() - delim.length());
    }

    /**
     * 合并一个列表为字符串,和数组合并查不错
     * 列表不能为空
     * @param list 非空列表
     * @return 字符数组
     */
    public static String join(List<?> list) {
        return join(list, ",");
    }

    /**
     * 合并一个集合对象为字符串,顺序是不确定的.
     * 依赖于迭代顺序
     * @param set 非空集合
     * @return 合并后的字符串
     */
    public static String join(Set<?> set) {
        return join(set, ",");
    }

    /**
     * 合并成一个多行的字符串
     * @param collection 非空集合
     * @return 多行数据
     */
    public static String joinLine(Collection<?> collection) {
        return join(collection, "\n");
    }

    /**
     * 逗号合并的字符串
     * @param collection 非空集合
     * @return 合并的字符串
     */
    public static String join(Collection<?> collection) {
        return join(collection, ",");
    }
    
    /**
     * @param numbers
     * @return 用逗号将数字数组合并成字符串
     */
    public static String join(Number[] numbers){
    	return join(numbers,",");
    }
    /**
     * @param numbers
     * @param delim
     * @return 将数字数组合并成字符串
     */
    public static String join(Number[] numbers,String delim){
		 if (numbers == null || numbers.length == 0) {
	         return "";
	     }
	     StringBuffer sb = new StringBuffer();
	     for (int i = 0; i < numbers.length; i++) {
	         sb.append(numbers[i]);
	         sb.append(delim);
	     }
	     return sb.substring(0, sb.length() - delim.length());
    }
    /**
     * 采用执行分隔符号合并集合成字符串
     * @param collection 非空集合
     * @param join  分隔符号
     * @return  合并的字符串
     */
    public static String join(Collection<?> collection, String join) {
        Object[] obj = collection.toArray();
        String[] arr = new String[obj.length];
        for (int i = 0; i < obj.length; i++) {
            arr[i] = String.valueOf(obj[i]);
        }
        return join(arr, join);
    }
}

