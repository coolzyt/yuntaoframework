package org.yuntao.framework.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * <p>Title: 图片处理工具类</p> 
 * <p>Description: </p>
 * @version 1.00 
 * @since 2011-3-18
 * @author zhaoyuntao
 * 
 */
public class ImageUtil {
	public static BufferedImage readImage(File picture) throws IOException{
		return ImageIO.read(picture);
	}
	
	public static BufferedImage cut(File picture,int x, int y, int width, int height) throws IOException{ 
        FileInputStream is = null ;
        ImageInputStream iis =null ;
        try{   
            is = new FileInputStream(picture);
            String suffix = picture.getName().substring(picture.getName().lastIndexOf(".")+1);
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(suffix);  
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis,true);
            ImageReadParam param = reader.getDefaultReadParam(); 
            Rectangle rect = new Rectangle(x, y, width, height); 
            param.setSourceRegion(rect); 
            BufferedImage bi = reader.read(0,param); 
            return bi;
        }finally{
            if(is!=null)
               is.close() ;       
            if(iis!=null)
               iis.close();  
        }
	}
	
	public static BufferedImage cut(BufferedImage image,int x, int y, int width, int height) throws IOException{ 
	    return image.getSubimage(x, y, width, height);
    }
	
    public static BufferedImage zoom(BufferedImage image, int sw, int sh) throws IOException {   
        BufferedImage bufTarget = null;
        int type = image.getType();
        double sx = (double) sw / image.getWidth();
        double sy = (double) sh / image.getHeight();
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = image.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(sw,sh);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            bufTarget = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else{
            bufTarget = new BufferedImage(sw, sh, type);
        }
        Graphics2D g = bufTarget.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(image,AffineTransform.getScaleInstance(sx,sy));
        g.dispose();
        return bufTarget; 
    }
    
    public static void writeImage(BufferedImage image,String format,File file) throws IOException{
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	ImageIO.write(image, format, file);
    }
    
    
    public static boolean isImage(File file) {
	    FileInputStream imgFile = null;
	    byte[] b = new byte[10];
	    int l = -1;
	    try {
	    	imgFile = new FileInputStream(file);
	    	l = imgFile.read(b);
	    } catch (Exception e) {
	    	return false;
	    }finally{
	    	if(imgFile!=null){
				try {
					imgFile.close();
				} catch (IOException e) {}
	    	}
	    }
	    if (l == 10) {
	    	byte b0 = b[0];
	    	byte b1 = b[1];
	    	byte b2 = b[2];
	    	byte b3 = b[3];
	    	byte b6 = b[6];
	    	byte b7 = b[7];
	    	byte b8 = b[8];
	    	byte b9 = b[9];
		    if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') { //gif
		    	return true;
		    } else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') { //png
		    	return true;
		    } else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I'&& b9 == (byte) 'F') {//jpg
		    	return true;
		    } else if (b6 == (byte) 'E' && b7 == (byte) 'x' && b8 == (byte) 'i'&& b9 == (byte) 'f') {//exif(jpg)
		    	return true;
		    } else if (b0 == (byte) 'B' && b1 == (byte) 'M' ) {//bmp
		    	return true;
		    } else {
		    	return false;
		    }
	    } 
	    return false;
    }
    
    public static void main(String args[]) throws IOException{
    	BufferedImage img = ImageIO.read(new File("c:/2.jpg"));
    	ImageUtil.writeImage(ImageUtil.zoom(img,600,480),"jpg",new File("c:/2-s.jpg"));
    	BufferedImage img2 = ImageIO.read(new File("c:/3.jpg"));
        ImageUtil.writeImage(ImageUtil.zoom(img2,600,480),"jpg",new File("c:/3-s.jpg"));
        BufferedImage img3 = ImageIO.read(new File("c:/4.jpg"));
        ImageUtil.writeImage(ImageUtil.zoom(img3,600,480),"jpg",new File("c:/4-s.jpg"));
    }
}
