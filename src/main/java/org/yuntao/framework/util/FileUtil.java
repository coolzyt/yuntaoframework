package org.yuntao.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * <p>Title: </p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-17
 * @author zhaoyuntao
 * 
 */
public class FileUtil {
    private static Logger log = Logger.getLogger(FileUtil.class);

    private static final String DEFAULT_ENCODING = "GBK";

    /**
     * 从文件中读取内容，以String的形式返回
     * @param fileName 文件名
     * @param ignoreLines  忽略的行数
     */
    public static String readFile(String fileName, int ignoreLines) throws FileNotFoundException,
            IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName can not be null");
        }

        StringBuffer text = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));

            for (int i = 0; i < ignoreLines; i++) {
                reader.readLine();
            }

            String line = null;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return text.toString();
    }

    public static byte[] readDataFile(String fileName, int ignoreLines)
            throws FileNotFoundException, IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName can not be null");
        }

        RandomAccessFile in = null;
        byte[] buffer = null;
        try {
            in = new RandomAccessFile(fileName, "r");

            for (int i = 0; i < ignoreLines; i++) {
                in.readLine();
            }

            buffer = new byte[(int) in.length()];
            in.read(buffer);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }


	public static byte[] readFile(File file)
 			throws FileNotFoundException, IOException {
	 	FileInputStream fis = new FileInputStream(file);
	 	int length =(int) file.length();
	 	byte[] fileByte = new byte[length];
	 	int offset=0 ;
	 	int remain = length;
	 	int readed;
	 	while(remain>0&&((readed = fis.read(fileByte, offset, remain))!=-1)){
	 		offset += readed;
	 		remain = length - offset;
	 	};
	 	fis.close();
		return fileByte;
	}
    
    public static String readLine(String fileName, int line) throws FileNotFoundException,
            IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName can not be null");
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));

            for (int i = 1; i < line; i++) {
                reader.readLine();
            }

            return reader.readLine();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void move(String srcFile, String desFile) {
        File inputFile = new File(srcFile);
        File outputFile = new File(desFile);
        try {
            inputFile.renameTo(outputFile);
        } catch (Exception ex) {
            throw new RuntimeException("Can not mv" + srcFile + " to " + desFile + ex.getMessage());
        }
    }

    public static boolean copy(String src, String dest) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        try {
            if (!new File(src).exists()) {
                return false;
            }
            in = new FileInputStream(src).getChannel();
            out = new FileOutputStream(dest).getChannel();
            in.transferTo(0, in.size(), out);
            return true;
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void makehome(String home) throws Exception {
        File homedir = new File(home);
        if (!homedir.exists())
            try {
                homedir.mkdirs();
            } catch (Exception ex) {
                throw new Exception("Can not mkdir :" + home + " Maybe include special charactor!");
            }
    }

    public static void copyDir(String sourcedir, String destdir) throws Exception {
        File dest = new File(destdir);
        File source = new File(sourcedir);
        String files[] = source.list();
        try {
            makehome(destdir);
        } catch (Exception ex) {
            throw new Exception("CopyDir:" + ex.getMessage());
        }
        for (int i = 0; i < files.length; i++) {
            String sourcefile = source + File.separator + files[i];
            String destfile = dest + File.separator + files[i];
            File temp = new File(sourcefile);
            if (temp.isFile())
                try {
                    copy(sourcefile, destfile);
                } catch (Exception ex) {
                    throw new Exception("CopyDir:" + ex.getMessage());
                }
        }

    }

    public static void recursiveRemoveDir(File directory) throws Exception {
        if (!directory.exists())
            throw new IOException(directory.toString() + " do not exist!");
        String filelist[] = directory.list();
        File tmpFile = null;
        for (int i = 0; i < filelist.length; i++) {
            tmpFile = new File(directory.getAbsolutePath(), filelist[i]);
            if (tmpFile.isDirectory())
                recursiveRemoveDir(tmpFile);
            else if (tmpFile.isFile())
                try {
                    tmpFile.delete();
                } catch (Exception ex) {
                    throw new Exception(tmpFile.toString() + " can not be deleted "
                            + ex.getMessage());
                }
        }

        try {
            directory.delete();
        } catch (Exception ex) {
            throw new Exception(directory.toString() + " can not be deleted " + ex.getMessage());
        } finally {
            filelist = null;
        }
    }

    public static void writeToFile(String filedir, String fileName, String data) {
        try {
            makehome(filedir);
            OutputStreamWriter fw = new OutputStreamWriter(
                    new FileOutputStream(filedir + fileName), DEFAULT_ENCODING);
            fw.write(data);
            fw.close();
        } catch (Exception ex) {
           throw new RuntimeException(ex);
        }

    }
    
    public static void writeToFile(File file,byte[] data){
    	FileOutputStream fos = null;
    	try{
    		fos = new FileOutputStream(file);
    		fos.write(data);
    		fos.flush();
    	}catch(Exception e){
    		throw new RuntimeException(e);
    	}finally{
    		if(fos!=null){
    			try {
					fos.close();
				} catch (IOException e) {}
    		}
    	}
    }
    public static String getExtName(File file) {
        return getExtName(file.getName());
    }

    public static String getExtName(String fileName) {
        if (fileName == null)
            return "";
        int index = fileName.lastIndexOf(".");
        return (index == -1) ? "" : fileName.substring(index);
    }

    /**
     * 拷贝文件,文件名不变
     * @param src      被拷贝的文件
     * @param destDir 目标目录
     * @return  如果操作成功,返回true;
     */
    public static boolean copyFile(File src, File destDir) {
        return copyFile(src, destDir, src.getName(), false);
    }

    /**
     * 拷贝文件,该方法不会抛出异常
     * @param src    源文件
     * @param destDir 目标路径
     * @param destFileName 目标文件名
     * @return 如果操作成功,返回true;
     */
    public static boolean copyFile(File src, File destDir, String destFileName) {
        return copyFile(src, destDir, destFileName, false);
    }

    /**
     * 拷贝文件,如果不希望该方法影响正常程序流程,请设置isFatal参数为false.
     * @param src     源文件
     * @param destDir 目标路径
     * @param destFileName 目标文件名
     * @param isFatal   是否是严重操作,如果为true,拷贝文件失败将会抛出一个SurfilterException,否则不抛出异常
     * @return  如果操作成功,返回true;
     */
    public static boolean copyFile(File src, File destDir, String destFileName, boolean isFatal) {
        try {
            FileUtils.copyFile(src, new File(destDir, destFileName));
            return true;
        } catch (IOException e) {
            if (isFatal)
                throw new RuntimeException(e);
            log.warn("copy file failed from " + src.getAbsolutePath() + " to "
                    + destDir.getAbsolutePath() + " with the new name " + destFileName);
            log.warn("caused by " + e.getMessage());
            return false;
        }
    }
}

