package org.concord.waba.extra.ui;
public class ImagePane extends java.awt.Canvas{
String imagePath = null;
waba.fx.Image wImage = null;
	public ImagePane(String path){
		imagePath = path;
	}
	public void paint(java.awt.Graphics g){
     	try{
     		if(wImage == null){
			wImage = new waba.fx.Image(imagePath);
    		}
     		if(wImage != null){
			g.drawImage(wImage.getAWTImage(),0,0,null);
     		}
//		g.drawImage(wImage,0,0);
//		wImage.free();
	}catch(Exception e){}
	}
    protected void finalize() throws Throwable {
     		if(wImage != null){
			wImage.free();
     		}
    }
}
