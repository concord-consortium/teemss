package org.concord.waba.extra.ui;
public class ImagePane extends waba.ui.Control{
String imagePath = null;
waba.fx.Image wImage = null;
boolean		freeOnPaint = false;
    public static boolean showImages = true;

    protected ImagePane(){};

    public ImagePane(String path){
		imagePath = path;
		freeOnPaint = true;
    }
    public ImagePane(waba.fx.Image wImage){
    	this.wImage = wImage;
		freeOnPaint = false;
    }

    public void onPaint(waba.fx.Graphics g){
		wImage = getImage();
		g.drawImage(wImage,0,0);
		if(freeOnPaint) freeImage();
    }

    protected waba.fx.Image getImage()
    {
    	if(wImage == null){
			wImage = new waba.fx.Image(imagePath);
		}
		return wImage;
    }
    
    public void setRect(int x,int y){
    	setRect(x,y,getImage().getWidth(),getImage().getHeight());
    }
    public waba.fx.Image getWabaImage(){
		return wImage;
    }
    public void freeImage(){
    	if(wImage != null){
    		 wImage.free();
    		 wImage = null;
    	}
    }
    
}
