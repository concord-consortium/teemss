package org.concord.waba.extra.ui;
public class ImagePane extends waba.ui.Control{
String imagePath = null;

    public static boolean showImages = true;

    protected ImagePane(){};

    public ImagePane(String path){
	imagePath = path;
    }

    public void onPaint(waba.fx.Graphics g){
	waba.fx.Image wImage = getImage();
	g.drawImage(wImage,0,0);
	wImage.free();
    }

    protected waba.fx.Image getImage()
    {
	return new waba.fx.Image(imagePath);
    }
}
