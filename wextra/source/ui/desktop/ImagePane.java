package org.concord.waba.extra.ui;
public class ImagePane extends waba.ui.Control{
String imagePath = null;

    public static boolean showImages = true;

	public ImagePane(String path){
		imagePath = path;
	}
	public void onPaint(waba.fx.Graphics g){
	    if(showImages){
     	try{
		waba.fx.Image wImage = new waba.fx.Image(imagePath);
		g.drawImage(wImage,0,0);
		wImage.free();
	}catch(Exception e){}
	    }
	}
}
