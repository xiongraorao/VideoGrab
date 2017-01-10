package com.persist.util.helper;

import javax.crypto.spec.RC2ParameterSpec;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by taozhiheng on 16-10-20.
 *
 * rotate and resize BufferedImage
 *
 * author: persist
 */
public class BufferedImageHelper {

	//private static PyModule module;

    public static BufferedImage rotate(BufferedImage image, int degree) {
        int iw = image.getWidth();
        int ih = image.getHeight();
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        degree = degree % 360;
        if (degree < 0)
            degree = 360 + degree;// ??????0-360???
        double ang = degree * 0.0174532925;// ???????

        if (degree == 180 || degree == 0 || degree == 360) {
            w = iw;
            h = ih;
        } else if (degree == 90 || degree == 270) {
            w = ih;
            h = iw;
        } else {
            int d = iw + ih;
            w = (int) (d * Math.abs(Math.cos(ang)));
            h = (int) (d * Math.abs(Math.sin(ang)));
        }
        x = (w / 2) - (iw / 2);
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        Graphics gs = rotatedImage.getGraphics();
        gs.fillRect(0, 0, w, h);
        AffineTransform at = new AffineTransform();
        at.rotate(ang, w / 2, h / 2);
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BILINEAR);
        op.filter(image, rotatedImage);
        image = rotatedImage;
        return image;
    }

    /**
     * @param image the source image
     * @param width the destination width
     * @param height the destination height
     * @return the destination image
     * */
    public static BufferedImage resize(BufferedImage image, int width, int height)
    {   //resize our image and preserve hard edges which we need for pixel art
        int imageWidth  = image.getWidth();
        int imageHeight = image.getHeight();
        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp op = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, new BufferedImage(width, height, image.getType()));
    }
    
    /**
     * segment the central 1/4 pixels 
     */
    public static BufferedImage segmentTest(BufferedImage image,String imageSuffix, 
    		int x, int y, int width, int height){

        //if the 'x' or 'y' is negative, then set it to zero
        //is the coordinate out of boundary?
        int xx = x;
        int yy = y;
        int ww = width;
        int hh = height;
        if( x < 0 )
            xx = 0;
        if(y < 0 )
            yy = 0;
        if(ww > image.getWidth())
            ww = image.getWidth();
        if(hh > image.getHeight())
            hh = image.getHeight();

    	return image.getSubimage(xx, yy, ww, hh);
    	
    }
    public static BufferedImage segmentTest(BufferedImage image,String imageSuffix, 
    		Rectangle rect){
    	int x = rect.x;
    	int y = rect.y;
    	int width = rect.width;
    	int height = rect.height;

    	return BufferedImageHelper.segmentTest(image, imageSuffix, x, y, width, height);
    }
    public static BufferedImage[] segmentTest(BufferedImage image,String imageSuffix, 
    		Rectangle[] rects){
    	BufferedImage[] buffs = new BufferedImage[rects.length];
    	for(int i=0;i<rects.length;i++){
    		buffs[i] = segmentTest(image,imageSuffix,rects[i]);
    	}
    	return buffs;
    }

    public static BufferedImage[] segmentTest(String imageUrl,String imageSuffix, Rectangle[] rects){
        BufferedImage[] buffs = new BufferedImage[rects.length];
        try {
            BufferedImage buffImg = ImageIO.read(new File(imageUrl));
            buffs = segmentTest(buffImg,imageSuffix,rects);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffs;
    }

    public static BufferedImage segmentTest(String imageUrl, String imageSuffix, Rectangle rect){
        try {
            BufferedImage bi = ImageIO.read(new File(imageUrl));
            bi = segmentTest(bi, imageSuffix, rect);
            return bi;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage segmentTest(BufferedImage image,String imageSuffix){
    	return image;
    }

	public static boolean saveBufImg(BufferedImage image, String fileName, HDFSHelper helper,
									  FileLogger log){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ImageIO.write(image, "png", baos);
		}catch (IOException e) {
			e.printStackTrace(log.getPrintWriter());
			log.getPrintWriter().flush();
		}
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		boolean isOk = helper.upload(is, fileName);
		if(isOk)
		    log.log("save-buff-img: " , fileName + "  saved success!");
		else
            log.log("save-buff-img: " , fileName + "  saved fail!");
		return isOk;
    }

    public static boolean saveBufImg(BufferedImage[] images, String fileName, HDFSHelper helper,
                                     FileLogger log){
	    int count = 0;
	    for (int i = 0; i < images.length; i++){
	        saveBufImg(images[i], fileName, helper, log);
	        count ++;
        }
        if(count == images.length)
            return true;
        else
            return false;
    }


	
}
